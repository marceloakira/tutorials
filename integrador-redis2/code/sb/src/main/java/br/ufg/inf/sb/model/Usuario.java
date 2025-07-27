package br.ufg.inf.sb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuario")
public class Usuario {

    @Id
    private String id;
    private String sobrenome;
    private String prenome;
    private String situacaoMatricula;

    public Usuario() {}

    public Usuario(String sobrenome, String prenome, String situacaoMatricula) {
        this.sobrenome = sobrenome;
        this.prenome = prenome;
        this.situacaoMatricula = situacaoMatricula;
    }

    public String getId() {
        return id;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getPrenome() {
        return prenome;
    }

    public void setPrenome(String prenome) {
        this.prenome = prenome;
    }

    public String getSituacaoMatricula() {
        return situacaoMatricula;
    }

    public void setSituacaoMatricula(String situacaoMatricula) {
        this.situacaoMatricula = situacaoMatricula;
    }
}