package br.ufg.inf.integrador.model;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;


@DatabaseTable(tableName = "estudante_canonico")
public class EstudanteCanonico {

    @DatabaseField(id = true)
    private String idCanonico;

    @DatabaseField
    private String prenome;

    @DatabaseField
    private String sobrenome;

    @DatabaseField
    private String nomeCompleto;

    @DatabaseField
    private String dataDeNascimento;

    @DatabaseField
    private String matricula;

    @DatabaseField
    private String statusAcademico;

    @DatabaseField
    private String statusBiblioteca;

    public String getIdCanonico() {
        return idCanonico;
    }
    public void setIdCanonico(String idCanonico) {
        this.idCanonico = idCanonico;
    }
    public String getPrenome() {
        return prenome;
    }
    public void setPrenome(String prenome) {
        this.prenome = prenome;
    }
    public String getSobrenome() {
        return sobrenome;
    }
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }
    public String getNomeCompleto() {
        return nomeCompleto;
    }
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }
    public String getDataDeNascimento() {
        return dataDeNascimento;
    }
    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public String getStatusAcademico() {
        return statusAcademico;
    }
    public void setStatusAcademico(String statusAcademico) {
        this.statusAcademico = statusAcademico;
    }
    public String getStatusBiblioteca() {
        return statusBiblioteca;
    }
    public void setStatusBiblioteca(String statusBiblioteca) {
        this.statusBiblioteca = statusBiblioteca;
    }
}
