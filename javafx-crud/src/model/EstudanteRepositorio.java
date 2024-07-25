package model;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import com.j256.ormlite.table.TableUtils;
import java.util.List;
import java.util.ArrayList;

public class EstudanteRepositorio
{
    private static Database database;
    private static Dao<Estudante, Integer> dao;
    private List<Estudante> loadedEstudantes;
    private Estudante loadedEstudante;
    
    public EstudanteRepositorio(Database database) {
        EstudanteRepositorio.setDatabase(database);
        loadedEstudantes = new ArrayList<Estudante>();
    }
    
    public static void setDatabase(Database database) {
        EstudanteRepositorio.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Estudante.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Estudante.class);
        }
        catch(SQLException e) {
            System.out.println(e);
        }            
    }
    
    public Estudante create(Estudante estudante) {
        int nrows = 0;
        try {
            nrows = dao.create(estudante);
            if ( nrows == 0 )
                throw new SQLException("Error: object not saved");
            this.loadedEstudante = estudante;
            loadedEstudantes.add(estudante);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return estudante;
    }    

    public void update(Estudante Estudante) {
      // TODO
    }

    public void delete(Estudante Estudante) {
      // TODO
    }
    
    public Estudante loadFromId(int id) {
        try {
            this.loadedEstudante = dao.queryForId(id);
            if (this.loadedEstudante != null)
                this.loadedEstudantes.add(this.loadedEstudante);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedEstudante;
    }    
    
    public List<Estudante> loadAll() {
        try {
            this.loadedEstudantes =  dao.queryForAll();
            if (this.loadedEstudantes.size() != 0)
                this.loadedEstudante = this.loadedEstudantes.get(0);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedEstudantes;
    }

    // getters and setters ommited...        
}