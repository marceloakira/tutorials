package br.ufg.inf.publicador;

import java.util.concurrent.CountDownLatch;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

public class RedisListenerServer {

    public void start() throws InterruptedException {
        RedisClient client = RedisClient.create("redis://localhost:6379");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();

        connection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                System.out.printf("🔔 [%s] %s%n", channel, message);
            }
        });

        connection.sync().subscribe("crud-channel");

        System.out.println("🟢 Escutando notificações Redis de forma assíncrona...");

        // Mantém a thread principal ativa indefinidamente
        new CountDownLatch(1).await();
    }

    public static void main(String[] args) throws Exception {
        RedisListenerServer listener = new RedisListenerServer();
        listener.start();
    }

}
