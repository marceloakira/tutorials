package br.ufg.inf.integrador.model;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;

@DatabaseTable(tableName = "estudante_id_mapping")
public class EstudanteIdMapping {

    @DatabaseField(id = true)
    private String idCanonico;

    @DatabaseField
    private String idSGA;

    @DatabaseField
    private String idSB;

    @DatabaseField
    private String ultimaAtualizacao;

    public String getIdCanonico() {
        return idCanonico;
    }
    public void setIdCanonico(String idCanonico) {
        this.idCanonico = idCanonico;
    }
    public String getIdSGA() {
        return idSGA;
    }
    public void setIdSGA(String idSGA) {
        this.idSGA = idSGA;
    }
    public String getIdSB() {
        return idSB;
    }
    public void setIdSB(String idSB) {
        this.idSB = idSB;
    }
    public String getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }
    public void setUltimaAtualizacao(String ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}
