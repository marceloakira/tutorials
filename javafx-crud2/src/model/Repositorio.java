package model;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class Repositorio<T, ID> {

    private static Database database;
    private Dao<T, ID> dao;
    private List<T> loadedEntities;
    private T loadedEntity;
    private Class<T> entityClass;

    public Repositorio(Database database, Class<T> entityClass) {
        this.entityClass = entityClass;
        setDatabase(database);
        loadedEntities = new ArrayList<>();
    }

    public void setDatabase(Database database) {
        Repositorio.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), entityClass);
            TableUtils.createTableIfNotExists(database.getConnection(), entityClass);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public T create(T entity) {
        try {
            int nrows = dao.create(entity);
            if (nrows == 0)
                throw new SQLException("Error: object not saved");
            this.loadedEntity = entity;
            loadedEntities.add(entity);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return entity;
    }

    public void update(T entity) {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void delete(T entity) {
        try {
            dao.delete(entity);
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
