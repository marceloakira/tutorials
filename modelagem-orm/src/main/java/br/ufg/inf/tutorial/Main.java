package br.ufg.inf.tutorial;

import br.ufg.inf.tutorial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principal para demonstrar o uso do JPA/Hibernate.
 * 
 * Esta classe serve como ponto de entrada para testes e demonstrações
 * das funcionalidades de mapeamento objeto-relacional.
 * 
 * @author Tutorial Modelagem ORM
 * @version 1.0
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("=== Iniciando Tutorial de Modelagem ORM com JPA/Hibernate ===");
        
        try {
            // Teste básico de conexão
            testarConexao();
            
            logger.info("=== Tutorial executado com sucesso! ===");
            
        } catch (Exception e) {
            logger.error("Erro durante execução do tutorial: {}", e.getMessage(), e);
        } finally {
            // Fechar recursos
            JPAUtil.closeEntityManagerFactory();
            logger.info("=== Recursos liberados. Fim do tutorial. ===");
        }
    }
    
    /**
     * Testa a conexão básica com o banco de dados.
     */
    private static void testarConexao() {
        logger.info("Testando conexão com o banco de dados...");
        
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Executa uma query simples para testar a conexão
            String sql = "SELECT 1 as teste";
            Object resultado = em.createNativeQuery(sql).getSingleResult();
            
            logger.info("Conexão testada com sucesso! Resultado: {}", resultado);
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}