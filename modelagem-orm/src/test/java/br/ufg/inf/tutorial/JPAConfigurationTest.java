package br.ufg.inf.tutorial;

import br.ufg.inf.tutorial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Testes básicos para verificar a configuração do JPA/Hibernate.
 * 
 * @author Tutorial Modelagem ORM
 * @version 1.0
 */
class JPAConfigurationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(JPAConfigurationTest.class);
    private static EntityManagerFactory emf;
    private EntityManager em;
    
    @BeforeAll
    static void setupClass() {
        logger.info("Inicializando testes de configuração JPA...");
        emf = Persistence.createEntityManagerFactory("tutorial-jpa-test");
    }
    
    @AfterAll
    static void tearDownClass() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.info("EntityManagerFactory de teste fechado");
        }
    }
    
    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
    }
    
    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    
    @Test
    @DisplayName("Deve criar EntityManagerFactory com sucesso")
    void deveInicializarEntityManagerFactory() {
        assertThat(emf).isNotNull();
        assertThat(emf.isOpen()).isTrue();
        logger.info("✓ EntityManagerFactory inicializado corretamente");
    }
    
    @Test
    @DisplayName("Deve criar EntityManager com sucesso")
    void deveCriarEntityManager() {
        assertThat(em).isNotNull();
        assertThat(em.isOpen()).isTrue();
        logger.info("✓ EntityManager criado corretamente");
    }
    
    @Test
    @DisplayName("Deve executar transação básica")
    void deveExecutarTransacaoBasica() {
        assertDoesNotThrow(() -> {
            em.getTransaction().begin();
            
            // Query simples para testar a conexão
            Object resultado = em.createNativeQuery("SELECT 1 as teste").getSingleResult();
            assertThat(resultado).isEqualTo(1);
            
            em.getTransaction().commit();
            logger.info("✓ Transação básica executada com sucesso");
        });
    }
    
    @Test
    @DisplayName("Deve testar JPAUtil")
    void deveTestarJPAUtil() {
        assertDoesNotThrow(() -> {
            EntityManager emUtil = JPAUtil.createEntityManager();
            assertThat(emUtil).isNotNull();
            assertThat(emUtil.isOpen()).isTrue();
            
            JPAUtil.closeEntityManager(emUtil);
            assertThat(emUtil.isOpen()).isFalse();
            
            logger.info("✓ JPAUtil funcionando corretamente");
        });
    }
    
    @Test
    @DisplayName("Deve executar operação em transação com JPAUtil")
    void deveExecutarOperacaoEmTransacao() {
        assertDoesNotThrow(() -> {
            JPAUtil.executeInTransaction(entityManager -> {
                Object resultado = entityManager
                    .createNativeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES")
                    .getSingleResult();
                
                assertThat(resultado).isNotNull();
                logger.info("✓ Operação em transação executada. Tabelas encontradas: {}", resultado);
            });
        });
    }
}