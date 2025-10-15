package br.ufg.inf.tutorial.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitária para gerenciar EntityManager e EntityManagerFactory do JPA.
 * 
 * Esta classe implementa o padrão Singleton para garantir uma única instância
 * do EntityManagerFactory durante a execução da aplicação.
 * 
 * @author Tutorial Modelagem ORM
 * @version 1.0
 */
public class JPAUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    
    private static final String PERSISTENCE_UNIT_NAME = "tutorial-jpa";
    private static EntityManagerFactory entityManagerFactory;
    
    // Construtor privado para implementar Singleton
    private JPAUtil() {}
    
    /**
     * Obtém a instância única do EntityManagerFactory.
     * Inicializa a factory na primeira chamada.
     * 
     * @return EntityManagerFactory configurado
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            try {
                logger.info("Inicializando EntityManagerFactory...");
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                logger.info("EntityManagerFactory inicializado com sucesso!");
            } catch (Exception e) {
                logger.error("Erro ao inicializar EntityManagerFactory: {}", e.getMessage(), e);
                throw new RuntimeException("Falha na inicialização do EntityManagerFactory", e);
            }
        }
        return entityManagerFactory;
    }
    
    /**
     * Cria um novo EntityManager.
     * 
     * @return EntityManager novo e pronto para uso
     */
    public static EntityManager createEntityManager() {
        try {
            EntityManager em = getEntityManagerFactory().createEntityManager();
            logger.debug("EntityManager criado com sucesso");
            return em;
        } catch (Exception e) {
            logger.error("Erro ao criar EntityManager: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na criação do EntityManager", e);
        }
    }
    
    /**
     * Fecha o EntityManager de forma segura.
     * 
     * @param entityManager EntityManager a ser fechado
     */
    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            try {
                entityManager.close();
                logger.debug("EntityManager fechado com sucesso");
            } catch (Exception e) {
                logger.warn("Erro ao fechar EntityManager: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * Fecha o EntityManagerFactory.
     * Deve ser chamado ao finalizar a aplicação.
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                logger.info("Fechando EntityManagerFactory...");
                entityManagerFactory.close();
                logger.info("EntityManagerFactory fechado com sucesso!");
            } catch (Exception e) {
                logger.warn("Erro ao fechar EntityManagerFactory: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * Executa uma operação dentro de uma transação.
     * Automaticamente faz commit em caso de sucesso ou rollback em caso de erro.
     * 
     * @param operation operação a ser executada
     */
    public static void executeInTransaction(TransactionOperation operation) {
        EntityManager em = createEntityManager();
        try {
            em.getTransaction().begin();
            logger.debug("Transação iniciada");
            
            operation.execute(em);
            
            em.getTransaction().commit();
            logger.debug("Transação confirmada (commit)");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.debug("Transação cancelada (rollback)");
            }
            logger.error("Erro durante execução da transação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na transação", e);
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Interface funcional para operações que precisam de transação.
     */
    @FunctionalInterface
    public interface TransactionOperation {
        void execute(EntityManager entityManager) throws Exception;
    }
}