package br.ufg.inf.integrador;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;

/**
 * Classe de teste para RedisPublisher.
 * 
 * @author Marcelo Akira Inuzuka <marceloakira@ufg.br>
 * @version 1.0
 */
public class RedisPublisherTest {
    
    private RedisPublisher redisPublisher;
    private CrudOperation operacaoTeste;
    
    /**
     * Construtor default para a classe de teste RedisPublisherTest
     */
    public RedisPublisherTest() {
    }
    
    /**
     * Define a 'fixture' do teste.
     *
     * Chamado antes de cada método de caso de teste.
     */
    @BeforeEach
    public void setUp() {
        redisPublisher = new RedisPublisher();
        operacaoTeste = new CrudOperation(
            "Estudante",
            CrudOperation.OperationType.CREATE,
            CrudOperation.Source.ORM,
            "{\"id\":1,\"nome\":\"João\"}",
            "2025-07-25T10:30:00"
        );
    }
    
    /**
     * Desfaz a 'fixture' do teste.
     *
     * Chamado após cada método de teste de caso.
     */
    @AfterEach
    public void tearDown() {
        redisPublisher = null;
        operacaoTeste = null;
    }
    
    @Test
    public void testRedisPublisherNaoNulo() {
        assertNotNull(redisPublisher, "RedisPublisher deve ser criado com sucesso");
    }
    
    @Test
    public void testCrudOperationSerializacao() {
        // Testa se a operação CRUD pode ser serializada em JSON
        Gson gson = new Gson();
        String json = gson.toJson(operacaoTeste);
        
        assertNotNull(json, "JSON da operação não deve ser nulo");
        assertTrue(json.contains("Estudante"), "JSON deve conter o nome da entidade");
        assertTrue(json.contains("CREATE"), "JSON deve conter o tipo da operação");
        assertTrue(json.contains("ORM"), "JSON deve conter a fonte");
    }
    
    @Test
    public void testCrudOperationDeserializacao() {
        // Testa se o JSON pode ser convertido de volta para CrudOperation
        Gson gson = new Gson();
        String json = gson.toJson(operacaoTeste);
        CrudOperation operacaoDeserializada = gson.fromJson(json, CrudOperation.class);
        
        assertNotNull(operacaoDeserializada, "Operação deserializada não deve ser nula");
        assertEquals(operacaoTeste.getEntity(), operacaoDeserializada.getEntity());
        assertEquals(operacaoTeste.getOperation(), operacaoDeserializada.getOperation());
        assertEquals(operacaoTeste.getSource(), operacaoDeserializada.getSource());
        assertEquals(operacaoTeste.getData(), operacaoDeserializada.getData());
        assertEquals(operacaoTeste.getTimestamp(), operacaoDeserializada.getTimestamp());
    }
    
    @Test
    public void testPublishOperationComOperacaoValida() {
        // Nota: Este teste pode falhar se o Redis não estiver rodando
        // É mais um teste de integração do que unitário
        
        // Para um teste verdadeiramente unitário, seria necessário usar mocks
        // Aqui testamos se o método não lança exceção inesperada com dados válidos
        
        try {
            redisPublisher.publishOperation(operacaoTeste);
            // Se chegou até aqui, não houve exceção de serialização
            assertTrue(true, "Operação publicada sem erro de serialização");
        } catch (Exception e) {
            // Se falhar, pode ser porque o Redis não está rodando
            // Isso é esperado em um ambiente de teste sem Redis
            assertTrue(e.getMessage().contains("Connection refused") || 
                      e.getMessage().contains("Unable to connect") ||
                      e.getMessage().contains("UnknownHostException"),
                      "Falha esperada: Redis não está disponível para conexão. Erro: " + e.getMessage());
        }
    }
    
    @Test
    public void testCrudOperationComTodosOsTiposDeOperacao() {
        // Testa serialização com todos os tipos de operação
        CrudOperation.OperationType[] operacoes = CrudOperation.OperationType.values();
        Gson gson = new Gson();
        
        for (CrudOperation.OperationType tipo : operacoes) {
            CrudOperation op = new CrudOperation(
                "TestEntity", 
                tipo, 
                CrudOperation.Source.ORM, 
                "{\"test\":\"data\"}", 
                "2025-07-25T12:00:00"
            );
            
            String json = gson.toJson(op);
            assertNotNull(json, "JSON não deve ser nulo para operação " + tipo);
            assertTrue(json.contains(tipo.toString()), "JSON deve conter o tipo de operação " + tipo);
        }
    }
    
    @Test
    public void testCrudOperationComTodosOsTiposDeFonte() {
        // Testa serialização com todos os tipos de fonte
        CrudOperation.Source[] fontes = CrudOperation.Source.values();
        Gson gson = new Gson();
        
        for (CrudOperation.Source fonte : fontes) {
            CrudOperation op = new CrudOperation(
                "TestEntity", 
                CrudOperation.OperationType.CREATE, 
                fonte, 
                "{\"test\":\"data\"}", 
                "2025-07-25T12:00:00"
            );
            
            String json = gson.toJson(op);
            assertNotNull(json, "JSON não deve ser nulo para fonte " + fonte);
            assertTrue(json.contains(fonte.toString()), "JSON deve conter o tipo de fonte " + fonte);
        }
    }
}
