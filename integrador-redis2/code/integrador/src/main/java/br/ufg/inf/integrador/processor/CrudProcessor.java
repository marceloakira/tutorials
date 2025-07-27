package br.ufg.inf.integrador.processor;

import br.ufg.inf.publicador.CrudOperation;
import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CrudProcessor implements Processor {

    private final Gson gson = new Gson();
    private final EstudanteProcessor estudanteProcessor = new EstudanteProcessor();

    @Override
    public void process(Exchange exchange) throws Exception {
        String json = exchange.getIn().getBody(String.class);
        System.out.println("🔍 JSON recebido no processador: " + json);
        
        CrudOperation op = gson.fromJson(json, CrudOperation.class);
        System.out.println("🔍 CrudOperation parseada: " + op);

        // Armazena a operação original no Exchange para uso posterior
        exchange.setProperty("CrudOriginal", op);

        // Verifica se é uma operação CREATE do ORM
        if (op.getOperation() == CrudOperation.OperationType.CREATE
            && op.getSource() == CrudOperation.Source.ORM) {

            System.out.println("🔍 Condições atendidas, processando entidade: " + op.getEntity());
            System.out.println("🔍 Dados da entidade (raw): " + op.getData());
            
            // Processar baseado no tipo de entidade
            String resultado = processarEntidade(op);
            
            if (resultado != null) {
                exchange.getIn().setBody(resultado);
                System.out.println("🔍 Processamento concluído com sucesso");
            } else {
                System.out.println("⚠️  Entidade não suportada: " + op.getEntity());
                exchange.setProperty("CamelSkipSendToEndpoint", true);
            }

        } else {
            System.out.println("🔍 Condições não atendidas:");
            System.out.println("  - Entity: " + op.getEntity());
            System.out.println("  - Operation: " + op.getOperation() + " (esperado: CREATE)");
            System.out.println("  - Source: " + op.getSource() + " (esperado: ORM)");
            exchange.setProperty("CamelSkipSendToEndpoint", true);
        }
    }

    /**
     * Direciona o processamento de acordo com o tipo de entidade
     */
    private String processarEntidade(CrudOperation op) {
        String entidade = op.getEntity();
        
        if ("Estudante".equalsIgnoreCase(entidade)) {
            return estudanteProcessor.estudanteParaUsuario(op.getData());
        }

        // Futuro: suporte a outras entidades como Professor, Disciplina, etc.
        return null; // Entidade não suportada
    }

}
