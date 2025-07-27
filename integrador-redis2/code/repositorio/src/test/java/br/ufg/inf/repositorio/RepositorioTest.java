package br.ufg.inf.repositorio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;

/**
 * Testes básicos para as classes Database e Repositorio.
 */
public class RepositorioTest {
    
    private Database database;
    private Repositorio<TestEntity, Long> repositorio;
    private final String testDbName = "test_repositorio.sqlite";
    
    @DatabaseTable(tableName = "test_entity")
    public static class TestEntity {
        @DatabaseField(generatedId = true)
        private Long id;
        
        @DatabaseField
        private String nome;
        
        public TestEntity() {
            // Construtor padrão necessário para ORMLite
        }
        
        public TestEntity(String nome) {
            this.nome = nome;
        }
        
        // Getters e setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        
        @Override
        public String toString() {
            return "TestEntity{id=" + id + ", nome='" + nome + "'}";
        }
    }
    
    @BeforeEach
    public void setUp() {
        // Remove o arquivo de teste se existir
        File testFile = new File(testDbName);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        database = new Database(testDbName);
        repositorio = new Repositorio<>(database, TestEntity.class);
    }
    
    @AfterEach
    public void tearDown() {
        if (database != null) {
            database.close();
        }
        
        // Remove o arquivo de teste
        File testFile = new File(testDbName);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @Test
    public void testDatabaseConnection() throws Exception {
        assertNotNull(database.getConnection());
        // Verifica se a conexão não é nula, o que indica que foi criada com sucesso
    }
    
    @Test
    public void testCreateEntity() {
        TestEntity entity = new TestEntity("Teste");
        TestEntity created = repositorio.create(entity);
        
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Teste", created.getNome());
    }
    
    @Test
    public void testLoadFromId() {
        // Criar uma entidade
        TestEntity entity = new TestEntity("Teste Load");
        TestEntity created = repositorio.create(entity);
        
        // Carregar por ID
        TestEntity loaded = repositorio.loadFromId(created.getId());
        
        assertNotNull(loaded);
        assertEquals(created.getId(), loaded.getId());
        assertEquals("Teste Load", loaded.getNome());
    }
    
    @Test
    public void testLoadAll() {
        // Criar algumas entidades
        repositorio.create(new TestEntity("Entidade 1"));
        repositorio.create(new TestEntity("Entidade 2"));
        repositorio.create(new TestEntity("Entidade 3"));
        
        // Carregar todas
        var entities = repositorio.loadAll();
        
        assertNotNull(entities);
        assertEquals(3, entities.size());
    }
    
    @Test
    public void testUpdate() {
        // Criar uma entidade
        TestEntity entity = new TestEntity("Nome Original");
        TestEntity created = repositorio.create(entity);
        
        // Atualizar
        created.setNome("Nome Atualizado");
        repositorio.update(created);
        
        // Verificar se foi atualizada
        TestEntity loaded = repositorio.loadFromId(created.getId());
        assertEquals("Nome Atualizado", loaded.getNome());
    }
    
    @Test
    public void testDelete() {
        // Criar uma entidade
        TestEntity entity = new TestEntity("Para Deletar");
        TestEntity created = repositorio.create(entity);
        Long id = created.getId();
        
        // Deletar
        repositorio.delete(created);
        
        // Verificar se foi deletada
        TestEntity loaded = repositorio.loadFromId(id);
        assertNull(loaded);
    }
}
