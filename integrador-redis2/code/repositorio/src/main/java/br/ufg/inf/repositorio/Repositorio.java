package br.ufg.inf.repositorio;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import br.ufg.inf.publicador.RedisPublisher;
import br.ufg.inf.publicador.CrudOperation;

import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe genérica para repositório de dados que implementa operações CRUD
 * com integração ao Redis para publicação de eventos.
 * 
 * @param <T> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 */
public class Repositorio<T, ID> {

    private Dao<T, ID> dao;
    private List<T> loadedEntities;
    private T loadedEntity;
    private Class<T> entityClass;
    private RedisPublisher redisPublisher;
    private boolean publishCrudOperation = true;
    private Gson gson;

    /**
     * Construtor do repositório.
     * @param database Instância da classe Database
     * @param entityClass Classe da entidade que será gerenciada
     */
    public Repositorio(Database database, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.redisPublisher = new RedisPublisher();
        this.gson = new Gson();
        setDatabase(database);
        loadedEntities = new ArrayList<>();
    }

    /**
     * Define o banco de dados e configura o DAO.
     * @param database Instância da classe Database
     */
    public void setDatabase(Database database) {
        try {
            dao = DaoManager.createDao(database.getConnection(), entityClass);
            TableUtils.createTableIfNotExists(database.getConnection(), entityClass);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Publica uma operação CRUD no Redis.
     * @param operationType Tipo da operação (CREATE, UPDATE, DELETE)
     * @param entity Entidade envolvida na operação
     */
    private void publishCrudOperation(CrudOperation.OperationType operationType, T entity) {
        try {
            String entityName = entityClass.getSimpleName();
            String entityJson = gson.toJson(entity);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            CrudOperation operation = new CrudOperation(
                entityName, 
                operationType, 
                CrudOperation.Source.ORM, 
                entityJson, 
                timestamp
            );
            redisPublisher.publishOperation(operation);
        } catch (Exception e) {
            System.err.println("Erro ao publicar operação no Redis: " + e.getMessage());
        }
    }

    /**
     * Cria uma nova entidade no banco de dados.
     * @param entity Entidade a ser criada
     * @return Entidade criada
     */
    public T create(T entity) {
        try {
            System.out.println("🔍 Criando entidade: " + entity);
            int nrows = dao.create(entity);
            if (nrows == 0)
                throw new SQLException("Error: object not saved");
            this.loadedEntity = entity;
            loadedEntities.add(entity);
            
            // Publicar operação CREATE no Redis
            if (publishCrudOperation) {
                System.out.println("✅ Entidade criada: " + entity);
                System.out.println("  - Operation: CREATE");
                publishCrudOperation(CrudOperation.OperationType.CREATE, entity);
            }
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * Atualiza uma entidade existente no banco de dados.
     * @param entity Entidade a ser atualizada
     */
    public void update(T entity) {
        try {
            dao.update(entity);
            if (publishCrudOperation) {
                publishCrudOperation(CrudOperation.OperationType.UPDATE, entity);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Remove uma entidade do banco de dados.
     * @param entity Entidade a ser removida
     */
    public void delete(T entity) {
        try {
            dao.delete(entity);
            
            // Publicar operação DELETE no Redis
            if (publishCrudOperation) {
                publishCrudOperation(CrudOperation.OperationType.DELETE, entity);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Carrega uma entidade pelo seu ID.
     * @param id ID da entidade
     * @return Entidade encontrada ou null se não encontrada
     */
    public T loadFromId(ID id) {
        try {
            this.loadedEntity = dao.queryForId(id);
            if (this.loadedEntity != null)
                this.loadedEntities.add(this.loadedEntity);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedEntity;
    }

    /**
     * Carrega todas as entidades do banco de dados.
     * @return Lista com todas as entidades
     */
    public List<T> loadAll() {
        try {
            this.loadedEntities = dao.queryForAll();
            if (!this.loadedEntities.isEmpty())
                this.loadedEntity = this.loadedEntities.get(0);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedEntities;
    }

    /**
     * Obtém a última entidade carregada.
     * @return Última entidade carregada
     */
    public T getLoadedEntity() {
        return loadedEntity;
    }

    /**
     * Obtém a lista de entidades carregadas.
     * @return Lista de entidades carregadas
     */
    public List<T> getLoadedEntities() {
        return loadedEntities;
    }

    /**
     * Obtém a classe da entidade gerenciada por este repositório.
     * @return Classe da entidade
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Define se as operações CRUD devem ser publicadas no Redis.
     * @param publish true para publicar, false para não publicar
     */
    public void setPublishCrudOperation(boolean publish) {
        this.publishCrudOperation = publish;
    }
    
    /**
     * Verifica se as operações CRUD estão configuradas para serem publicadas no Redis.
     * @return true se as operações devem ser publicadas, false caso contrário
     */
    public boolean getPublishCrudOperation() {
        return this.publishCrudOperation;
    }
}