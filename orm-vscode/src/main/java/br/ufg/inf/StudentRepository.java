package br.ufg.inf;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import com.j256.ormlite.table.TableUtils;
import java.util.List;
import java.util.ArrayList;

public class StudentRepository
{
    private static Database database;
    private static Dao<Student, Integer> dao;
    private List<Student> loadedStudents;
    private Student loadedStudent; 
    
    public StudentRepository(Database database) {
        StudentRepository.setDatabase(database);
        loadedStudents = new ArrayList<Student>();
    }
    
    public static void setDatabase(Database database) {
        StudentRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Student.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Student.class);
        }
        catch(SQLException e) {
            System.out.println(e);
        }            
    }
    
    public Student create(Student student) {
        int nrows = 0;
        try {
            nrows = dao.create(student);
            if ( nrows == 0 )
                throw new SQLException("Error: object not saved");
            this.loadedStudent = student;
            loadedStudents.add(student);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return student;
    }    

    public void update(Student student) {
      // TODO
    }

    public void delete(Student student) {
      // TODO
    }
    
    public Student loadFromId(int id) {
        try {
            this.loadedStudent = dao.queryForId(id);
            if (this.loadedStudent != null)
                this.loadedStudents.add(this.loadedStudent);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedStudent;
    }    
    
    public List<Student> loadAll() {
        try {
            this.loadedStudents =  dao.queryForAll();
            if (this.loadedStudents.size() != 0)
                this.loadedStudent = this.loadedStudents.get(0);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedStudents;
    }

    // getters and setters ommited...        
}