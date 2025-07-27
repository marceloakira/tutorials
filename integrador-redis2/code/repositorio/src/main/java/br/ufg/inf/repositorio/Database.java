package br.ufg.inf.repositorio;

import java.sql.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados SQLite.
 * Esta classe é reutilizável e pode ser utilizada em diferentes componentes.
 */
public class Database {
    private String databaseName = null;
    private JdbcConnectionSource connection = null;
    
    /**
     * Construtor que inicializa o banco de dados com o nome especificado.
     * @param databaseName Nome do arquivo de banco de dados SQLite
     */
    public Database(String databaseName) {
        this.databaseName = databaseName;
    }    
    
    /**
     * Obtém a conexão com o banco de dados.
     * Se a conexão não existir, cria uma nova.
     * @return Conexão JDBC do ORMLite
     * @throws SQLException Se houver erro na conexão
     */
    public JdbcConnectionSource getConnection() throws SQLException {
        if (databaseName == null) {
            throw new SQLException("database name is null");
        }
        if (connection == null) {
            try {
                connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);             
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        return connection;
    }
    
    /**
     * Fecha a conexão com o banco de dados.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                this.connection = null;
            } catch (java.lang.Exception e) {
                System.err.println(e);
            }
        }
    }
}
