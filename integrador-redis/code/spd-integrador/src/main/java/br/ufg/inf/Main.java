package br.ufg.inf;

public class Main {
    public static void main(String[] args) throws Exception {
        // Exemplo: publicar mensagem (teste manual)
        RedisPublisher publisher = new RedisPublisher();
        CrudOperation op = new CrudOperation();
        op.setEntity("User");
        op.setOperation(CrudOperation.OperationType.CREATE);
        op.setSource(CrudOperation.Source.BD1);
        op.setData("{\"id\":1,\"name\":\"João da Silva\"}");
        op.setTimestamp(java.time.Instant.now().toString());

        publisher.publishOperation(op);
    }
}
