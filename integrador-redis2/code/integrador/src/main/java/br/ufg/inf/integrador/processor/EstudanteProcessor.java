package br.ufg.inf.integrador.processor;

import br.ufg.inf.integrador.model.EstudanteCanonico;
import br.ufg.inf.sb.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;

/**
 * Classe responsável pelas transformações de dados relacionadas ao Estudante.
 * Contém métodos reutilizáveis para converter dados de Estudante para diferentes formatos.
 */
public class EstudanteProcessor {

    private final Gson gson = new Gson();

    /**
     * Transforma os dados JSON de um estudante em um objeto Usuario (compatível com o SB).
     * 
     * @param dadosJson JSON contendo os dados do estudante
     * @return JSON do objeto Usuario serializado, ou null em caso de erro
     */
    public String estudanteParaUsuario(String dadosJson) {
        try {
            JsonObject estudanteJson = JsonParser.parseString(dadosJson).getAsJsonObject();

            String nomeCompleto = estudanteJson.has("nomeCompleto") ? 
                estudanteJson.get("nomeCompleto").getAsString() : "";
            String prenome = "";
            String sobrenome = "";

            // Extrai prenome e sobrenome do nome completo
            if (!nomeCompleto.isEmpty()) {
                String[] partes = nomeCompleto.trim().split("\\s+", 2);
                prenome = partes[0];
                sobrenome = partes.length > 1 ? partes[1] : "";
            }

            String situacaoMatricula = "ATIVO"; // Valor padrão

            System.out.println("🔍 [EstudanteProcessor] Dados extraídos para Usuario: nomeCompleto=" + nomeCompleto + 
                             ", prenome=" + prenome + ", sobrenome=" + sobrenome + 
                             ", situacaoMatricula=" + situacaoMatricula);

            Usuario usuario = new Usuario();
            usuario.setPrenome(prenome);
            usuario.setSobrenome(sobrenome);
            usuario.setSituacaoMatricula(situacaoMatricula);

            String usuarioJson = gson.toJson(usuario);
            System.out.println("🔍 [EstudanteProcessor] JSON do Usuario gerado: " + usuarioJson);

            return usuarioJson;

        } catch (Exception e) {
            System.err.println("❌ [EstudanteProcessor] Erro ao transformar estudante para usuario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Transforma os dados JSON de um estudante em um objeto EstudanteCanonico.
     * 
     * @param dadosJson JSON contendo os dados do estudante
     * @param idCanonico ID canônico a ser atribuído ao estudante
     * @return Objeto EstudanteCanonico populado, ou null em caso de erro
     */
    public EstudanteCanonico estudanteParaEstudanteCanonico(String dadosJson, String idCanonico) {
        try {
            JsonObject estudanteJson = JsonParser.parseString(dadosJson).getAsJsonObject();

            String nomeCompleto = estudanteJson.has("nomeCompleto") ? 
                estudanteJson.get("nomeCompleto").getAsString() : "";
            String prenome = "";
            String sobrenome = "";

            // Extrai prenome e sobrenome do nome completo
            if (!nomeCompleto.isEmpty()) {
                String[] partes = nomeCompleto.trim().split("\\s+", 2);
                prenome = partes[0];
                sobrenome = partes.length > 1 ? partes[1] : "";
            }

            EstudanteCanonico ec = new EstudanteCanonico();
            ec.setIdCanonico(idCanonico != null ? idCanonico : UUID.randomUUID().toString());
            ec.setPrenome(prenome);
            ec.setSobrenome(sobrenome);
            ec.setNomeCompleto(nomeCompleto);
            ec.setDataDeNascimento(estudanteJson.has("dataDeNascimento") ? 
                estudanteJson.get("dataDeNascimento").getAsString() : "");
            ec.setMatricula(estudanteJson.has("matricula") ? 
                estudanteJson.get("matricula").getAsString() : "");
            ec.setStatusAcademico(estudanteJson.has("statusAcademico") ? 
                estudanteJson.get("statusAcademico").getAsString() : "ATIVO");
            ec.setStatusBiblioteca(estudanteJson.has("statusBiblioteca") ? 
                estudanteJson.get("statusBiblioteca").getAsString() : "");

            System.out.println("🔍 [EstudanteProcessor] EstudanteCanonico criado: idCanonico=" + ec.getIdCanonico() + 
                             ", prenome=" + ec.getPrenome() + ", sobrenome=" + ec.getSobrenome() + 
                             ", nomeCompleto=" + ec.getNomeCompleto());

            return ec;

        } catch (Exception e) {
            System.err.println("❌ [EstudanteProcessor] Erro ao transformar estudante para EstudanteCanonico: " + e.getMessage());
            return null;
        }
    }

    /**
     * Método de conveniência para gerar EstudanteCanonico com ID gerado automaticamente.
     * 
     * @param dadosJson JSON contendo os dados do estudante
     * @return Objeto EstudanteCanonico populado, ou null em caso de erro
     */
    public EstudanteCanonico estudanteParaEstudanteCanonico(String dadosJson) {
        return estudanteParaEstudanteCanonico(dadosJson, null);
    }

    /**
     * Extrai o nome completo de um JSON de estudante.
     * 
     * @param dadosJson JSON contendo os dados do estudante
     * @return Nome completo ou string vazia se não encontrado
     */
    public String extrairNomeCompleto(String dadosJson) {
        try {
            JsonObject estudanteJson = JsonParser.parseString(dadosJson).getAsJsonObject();
            return estudanteJson.has("nomeCompleto") ? 
                estudanteJson.get("nomeCompleto").getAsString() : "";
        } catch (Exception e) {
            System.err.println("❌ [EstudanteProcessor] Erro ao extrair nome completo: " + e.getMessage());
            return "";
        }
    }

    /**
     * Extrai prenome e sobrenome de um nome completo.
     * 
     * @param nomeCompleto Nome completo a ser processado
     * @return Array com [prenome, sobrenome]. Se houver apenas um nome, sobrenome será vazio.
     */
    public String[] extrairPrenomeESobrenome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return new String[]{"", ""};
        }

        String[] partes = nomeCompleto.trim().split("\\s+", 2);
        String prenome = partes[0];
        String sobrenome = partes.length > 1 ? partes[1] : "";
        
        return new String[]{prenome, sobrenome};
    }
}
