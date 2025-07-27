package br.ufg.inf.integrador;

import br.ufg.inf.publicador.CrudOperation;
import br.ufg.inf.repositorio.Database;
import br.ufg.inf.repositorio.Repositorio;
import br.ufg.inf.sga.model.Estudante;
import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CrudEventsTest {

    private Database database;
    private Repositorio<Estudante, Integer> estudanteRepo;
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private CountDownLatch messageLatch;
    private AtomicReference<String> receivedMessage;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        // Configurar banco de dados em memória para teste
        database = new Database(":memory:");        
        estudanteRepo = new Repositorio<>(database, Estudante.class);
        
        // Configurar cliente Redis para escutar mensagens
        redisClient = RedisClient.create("redis://localhost:6379");
        pubSubConnection = redisClient.connectPubSub();
        gson = new Gson();
        
        // Configurar listener para capturar mensagens
        messageLatch = new CountDownLatch(1);
        receivedMessage = new AtomicReference<>();
        
        pubSubConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                if ("crud-channel".equals(channel)) {
                    receivedMessage.set(message);
                    messageLatch.countDown();
                }
            }

            @Override
            public void message(String pattern, String channel, String message) {}

            @Override
            public void subscribed(String channel, long count) {}

            @Override
            public void psubscribed(String pattern, long count) {}

            @Override
            public void unsubscribed(String channel, long count) {}

            @Override
            public void punsubscribed(String pattern, long count) {}
        });
        
        // Inscrever-se no canal crud-channel
        RedisPubSubCommands<String, String> sync = pubSubConnection.sync();
        sync.subscribe("crud-channel");
    }

    @AfterEach
    public void tearDown() {
        if (pubSubConnection != null) {
            pubSubConnection.close();
        }
        if (redisClient != null) {
            redisClient.close();
        }
        if (database != null) {
            database.close();
        }
    }

    @Test
    public void testCreateOperationPublishesEvent() throws InterruptedException {
        // Criar um estudante
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("João Silva");
        estudante.setDataDeNascimento("1990-01-01");
        estudante.setMatricula(123456);
        
        estudanteRepo.create(estudante);
        
        // Aguardar a mensagem ser recebida (timeout de 5 segundos)
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Mensagem não foi recebida no tempo esperado");
        
        // Verificar se a mensagem foi recebida
        String message = receivedMessage.get();
        assertNotNull(message, "Nenhuma mensagem foi recebida");
        
        // Deserializar a mensagem JSON
        CrudOperation operation = gson.fromJson(message, CrudOperation.class);
        
        // Verificar os dados da operação
        assertEquals("Estudante", operation.getEntity());
        assertEquals(CrudOperation.OperationType.CREATE, operation.getOperation());
        assertEquals(CrudOperation.Source.ORM, operation.getSource());
        assertNotNull(operation.getData());
        assertNotNull(operation.getTimestamp());
        
        // Verificar se os dados do estudante estão corretos no JSON
        Estudante estudanteFromJson = gson.fromJson(operation.getData(), Estudante.class);
        assertEquals("João Silva", estudanteFromJson.getNomeCompleto());
        assertEquals("1990-01-01", estudanteFromJson.getDataDeNascimento());
        assertEquals(123456, estudanteFromJson.getMatricula());
    }

    @Test
    public void testUpdateOperationPublishesEvent() throws InterruptedException {
        // Primeiro criar um estudante
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("Maria Santos");
        estudante.setDataDeNascimento("1992-02-02");
        estudante.setMatricula(654321);
        
        estudanteRepo.create(estudante);
        
        // Aguardar a primeira mensagem (CREATE)
        messageLatch.await(2, TimeUnit.SECONDS);
        
        // Resetar o latch para a próxima mensagem
        messageLatch = new CountDownLatch(1);
        receivedMessage.set(null);
        
        // Atualizar o estudante
        estudante.setNomeCompleto("Maria Santos Silva");
        estudanteRepo.update(estudante);
        
        // Aguardar a mensagem de UPDATE
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Mensagem de UPDATE não foi recebida no tempo esperado");
        
        // Verificar a mensagem
        String message = receivedMessage.get();
        assertNotNull(message, "Nenhuma mensagem de UPDATE foi recebida");
        
        CrudOperation operation = gson.fromJson(message, CrudOperation.class);
        assertEquals("Estudante", operation.getEntity());
        assertEquals(CrudOperation.OperationType.UPDATE, operation.getOperation());
        assertEquals(CrudOperation.Source.ORM, operation.getSource());
        
        // Verificar se os dados atualizados estão corretos
        Estudante estudanteFromJson = gson.fromJson(operation.getData(), Estudante.class);
        assertEquals("Maria Santos Silva", estudanteFromJson.getNomeCompleto());
    }

    @Test
    public void testDeleteOperationPublishesEvent() throws InterruptedException {
        // Primeiro criar um estudante
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("Pedro Oliveira");
        estudante.setDataDeNascimento("1988-03-03");
        estudante.setMatricula(789012);
        
        estudanteRepo.create(estudante);
        
        // Aguardar a primeira mensagem (CREATE)
        messageLatch.await(2, TimeUnit.SECONDS);
        
        // Resetar o latch para a próxima mensagem
        messageLatch = new CountDownLatch(1);
        receivedMessage.set(null);
        
        // Deletar o estudante
        estudanteRepo.delete(estudante);
        
        // Aguardar a mensagem de DELETE
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Mensagem de DELETE não foi recebida no tempo esperado");
        
        // Verificar a mensagem
        String message = receivedMessage.get();
        assertNotNull(message, "Nenhuma mensagem de DELETE foi recebida");
        
        CrudOperation operation = gson.fromJson(message, CrudOperation.class);
        assertEquals("Estudante", operation.getEntity());
        assertEquals(CrudOperation.OperationType.DELETE, operation.getOperation());
        assertEquals(CrudOperation.Source.ORM, operation.getSource());
        
        // Verificar se os dados do estudante deletado estão corretos
        Estudante estudanteFromJson = gson.fromJson(operation.getData(), Estudante.class);
        assertEquals("Pedro Oliveira", estudanteFromJson.getNomeCompleto());
        assertEquals("1988-03-03", estudanteFromJson.getDataDeNascimento());
        assertEquals(789012, estudanteFromJson.getMatricula());
    }

    @Test
    public void testMessageFormat() throws InterruptedException {
        // Criar um estudante simples
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("Ana Costa");
        estudante.setDataDeNascimento("1995-04-04");
        estudante.setMatricula(456789);
        
        estudanteRepo.create(estudante);
        
        // Aguardar a mensagem
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Mensagem não foi recebida");
        
        String message = receivedMessage.get();
        assertNotNull(message);
        
        // Verificar se é um JSON válido
        assertDoesNotThrow(() -> {
            CrudOperation operation = gson.fromJson(message, CrudOperation.class);
            assertNotNull(operation);
        }, "A mensagem deve ser um JSON válido");
        
        // Verificar se contém os campos esperados
        assertTrue(message.contains("entity"), "Mensagem deve conter o campo 'entity'");
        assertTrue(message.contains("operation"), "Mensagem deve conter o campo 'operation'");
        assertTrue(message.contains("source"), "Mensagem deve conter o campo 'source'");
        assertTrue(message.contains("data"), "Mensagem deve conter o campo 'data'");
        assertTrue(message.contains("timestamp"), "Mensagem deve conter o campo 'timestamp'");
    }
}
