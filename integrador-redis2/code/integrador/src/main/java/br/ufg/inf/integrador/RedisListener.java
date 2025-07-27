package br.ufg.inf.integrador.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.apache.camel.ProducerTemplate;

public class RedisListener {

    private final ProducerTemplate producer;

    public RedisListener(ProducerTemplate producer) {
        this.producer = producer;
    }

    public void start() {
        RedisClient client = RedisClient.create("redis://localhost:6379");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();

        connection.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                System.out.printf("🔔 [%s] %s%n", channel, message);

                // Envia a mensagem para o Camel
                producer.sendBody("direct:crud", message);
            }
        });

        connection.sync().subscribe("crud-channel");

        System.out.println("🟢 Escutando canal Redis: crud-channel");
    }
}
