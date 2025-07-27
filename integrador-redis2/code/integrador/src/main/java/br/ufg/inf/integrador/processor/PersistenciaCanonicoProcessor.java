package br.ufg.inf.integrador.processor;

import br.ufg.inf.integrador.model.EstudanteCanonico;
import br.ufg.inf.integrador.model.EstudanteIdMapping;
import br.ufg.inf.publicador.CrudOperation;
import br.ufg.inf.repositorio.Database;
import br.ufg.inf.repositorio.Repositorio;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.Instant;
import java.util.UUID;

public class PersistenciaCanonicoProcessor implements Processor {

    private final Gson gson = new Gson();
    private final EstudanteProcessor estudanteProcessor = new EstudanteProcessor();
    private final Repositorio<EstudanteCanonico, String> estudanteRepo;
    private final Repositorio<EstudanteIdMapping, String> mappingRepo;

    public PersistenciaCanonicoProcessor() {
        Database db = new Database("integrador.db");
        this.estudanteRepo = new Repositorio<>(db, EstudanteCanonico.class);
        this.estudanteRepo.setEnableCrudPublishing(false); // Desabilita publicação de CRUD para evitar loops
        this.mappingRepo = new Repositorio<>(db, EstudanteIdMapping.class);
        this.mappingRepo.setEnableCrudPublishing(false); // Desabilita publicação de CRUD para evitar loops
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String entidade = ((String) exchange.getProperty("crudEntity")).toLowerCase();
        CrudOperation.OperationType operacao = (CrudOperation.OperationType) exchange.getProperty("crudOperation");

        if (!"estudante".equals(entidade) || operacao != CrudOperation.OperationType.CREATE) {
            return;
        }

        String resposta = exchange.getMessage().getBody(String.class);
        JsonObject jsonResposta = JsonParser.parseString(resposta).getAsJsonObject();
        String idSB = jsonResposta.has("id") ? jsonResposta.get("id").getAsString() : null;

        CrudOperation op = exchange.getProperty("CrudOriginal", CrudOperation.class);
        String idSGA = null;
        JsonObject json = JsonParser.parseString(op.getData()).getAsJsonObject();
        if (json.has("id")) {
            idSGA = json.get("id").getAsString();
        }

        String idCanonico = UUID.randomUUID().toString();

        // Usa o EstudanteProcessor para criar o EstudanteCanonico
        EstudanteCanonico ec = estudanteProcessor.estudanteParaEstudanteCanonico(op.getData(), idCanonico);
        
        if (ec == null) {
            System.err.println("❌ Erro ao criar EstudanteCanonico");
            return;
        }

        EstudanteIdMapping map = new EstudanteIdMapping();
        map.setIdCanonico(idCanonico);
        map.setIdSGA(idSGA);
        map.setIdSB(idSB);
        map.setUltimaAtualizacao(Instant.now().toString());

        estudanteRepo.create(ec);
        mappingRepo.create(map);

        System.out.println("✅ EstudanteCanonico persistido no SQLite");
        System.out.println("✅ EstudanteIdMapping persistido no SQLite");
    }
}
