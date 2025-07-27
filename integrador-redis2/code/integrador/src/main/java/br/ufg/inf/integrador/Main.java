package br.ufg.inf.integrador;

import br.ufg.inf.integrador.redis.RedisListener;
import br.ufg.inf.integrador.routes.CrudTransformRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new CrudTransformRoute());
        context.start();

        // Produz mensagem para rota a partir do Redis
        new RedisListener(context.createProducerTemplate()).start();

        Thread.currentThread().join(); // mantém aplicação viva
    }
}
