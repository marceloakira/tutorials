package br.ufg.inf.integrador;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;

public class RedisPublisher {

    private final RedisClient client = RedisClient.create("redis://localhost:6379");

    public void publishOperation(CrudOperation op) throws Exception {
        try (var connection = client.connect()) {

            Gson gson = new Gson();
            String json = gson.toJson(op);
            connection.sync().publish("crud-channel", json);
        }
    }
}
