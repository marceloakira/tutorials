package br.ufg.inf.integrador.routes;

import br.ufg.inf.integrador.processor.CrudProcessor;
import br.ufg.inf.integrador.processor.PersistenciaCanonicoProcessor;
import br.ufg.inf.publicador.CrudOperation;
import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class CrudTransformRoute extends RouteBuilder {

    private final Gson gson = new Gson();

    @Override
    public void configure() {

        from("direct:crud")
            .routeId("crud-transformer")
            .log("📥 Mensagem recebida: ${body}")
            
            // Prepara headers e propriedades
            .process(this::setOperationHeaders)

            // Transforma o corpo JSON original para o formato necessário (ex: Usuario)
            .process(new CrudProcessor())

            .choice()
                .when(exchangeProperty("CamelSkipSendToEndpoint").isNotEqualTo(true))
                    .log("✅ JSON processado: ${body}")
                    .setHeader("Content-Type", constant("application/json;charset=UTF-8"))

                    // Envia para o endpoint determinado dinamicamente
                    .toD("${header.targetEndpoint}")

                    // Loga a resposta HTTP
                    .log("📤 Resposta HTTP: Status=${header.CamelHttpResponseCode}, Body=${body}")

                    // 🔁 Persistência local (canonico + mapeamento) apenas para entidade Estudante/CREATE
                    .filter().simple("${exchangeProperty.crudEntity.toLowerCase()} == 'estudante' && ${exchangeProperty.crudOperation} == 'CREATE'")
                        .process(new PersistenciaCanonicoProcessor())
                    .endChoice()

                .otherwise()
                    .log("⚠️ Mensagem ignorada pelo processador");
    }

    /**
     * Processa a mensagem original para extrair informações da operação CRUD
     * e configurar os headers apropriados para o método HTTP e endpoint
     */
    private void setOperationHeaders(Exchange exchange) throws Exception {
        String json = exchange.getIn().getBody(String.class);
        CrudOperation op = gson.fromJson(json, CrudOperation.class);
        
        // Mapear operação CRUD para método HTTP
        String httpMethod = mapOperationToHttpMethod(op.getOperation());
        exchange.getIn().setHeader("CamelHttpMethod", httpMethod);
        
        // Mapear entidade para endpoint
        String endpoint = mapEntityToEndpoint(op.getEntity(), op.getOperation());
        exchange.getIn().setHeader("targetEndpoint", endpoint);
        
        // Armazenar informações da operação para uso posterior
        exchange.setProperty("crudOperation", op.getOperation());
        exchange.setProperty("crudEntity", op.getEntity());
        
        System.out.println("Headers configurados - Método: " + httpMethod + ", Endpoint: " + endpoint);
    }

    private String mapOperationToHttpMethod(CrudOperation.OperationType operation) {
        switch (operation) {
            case CREATE: return "POST";
            case UPDATE: return "PUT";
            case DELETE: return "DELETE";
            default: return "POST";
        }
    }

    private String mapEntityToEndpoint(String entity, CrudOperation.OperationType operation) {
        String baseUrl = "http://localhost:8080";
        String endpoint;

        switch (entity.toLowerCase()) {
            case "estudante": endpoint = baseUrl + "/usuarios"; break;
            // case "professor": endpoint = baseUrl + "/professores"; break;
            // case "disciplina": endpoint = baseUrl + "/disciplinas"; break;
            // case "turma": endpoint = baseUrl + "/turmas"; break;
            default: endpoint = baseUrl + "/" + entity.toLowerCase() + "s";
        }

        return endpoint + "?throwExceptionOnFailure=false&bridgeEndpoint=true&charset=UTF-8";
    }
}
