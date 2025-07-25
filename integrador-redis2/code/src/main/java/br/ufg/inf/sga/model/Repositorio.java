package br.ufg.inf.sga.model;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import br.ufg.inf.integrador.CrudOperation;
import br.ufg.inf.integrador.RedisPublisher;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Repositorio<T, ID> {

    private Dao<T, ID> dao;
    private List<T> loadedEntities;
    private T loadedEntity;
    private Class<T> entityClass;
    private RedisPublisher redisPublisher;
    private Gson gson;

    public Repositorio(Database database, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.redisPublisher = new RedisPublisher();
        this.gson = new Gson();
        setDatabase(database);
        loadedEntities = new ArrayList<>();
    }

    public void setDatabase(Database database) {
        try {
            dao = DaoManager.createDao(database.getConnection(), entityClass);
            TableUtils.createTableIfNotExists(database.getConnection(), entityClass);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

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

    public T create(T entity) {
        try {
            int nrows = dao.create(entity);
            if (nrows == 0)
                throw new SQLException("Error: object not saved");
            this.loadedEntity = entity;
            loadedEntities.add(entity);
            
            // Publicar operação CREATE no Redis
            publishCrudOperation(CrudOperation.OperationType.CREATE, entity);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return entity;
    }

    public void update(T entity) {
        try {
            dao.update(entity);
            
            // Publicar operação UPDATE no Redis
            publishCrudOperation(CrudOperation.OperationType.UPDATE, entity);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void delete(T entity) {
        try {
            dao.delete(entity);
            
            // Publicar operação DELETE no Redis
            publishCrudOperation(CrudOperation.OperationType.DELETE, entity);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

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

    // Getters e setters podem ser adicionados se desejar
}
