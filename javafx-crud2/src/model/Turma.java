package model;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;

@DatabaseTable(tableName = "turma")
public class Turma {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(dataType = DataType.STRING)
    private String codigo;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Disciplina disciplina;

    // Este campo é apenas informativo e não deve ser persistido
    @DatabaseField(persisted = false)
    private int alunosMatriculados;

    @DatabaseField(dataType = DataType.INTEGER)
    private int vagasDisponiveis;

    // GET/SET ID
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // GET/SET Código
    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // GET/SET Disciplina
    public Disciplina getDisciplina() {
        return this.disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    // GET/SET Alunos Matriculados
    public int getAlunosMatriculados() {
        return this.alunosMatriculados;
    }

    public void setAlunosMatriculados(int alunosMatriculados) {
        this.alunosMatriculados = alunosMatriculados;
    }

    // GET/SET Vagas Disponíveis
    public int getVagasDisponiveis() {
        return this.vagasDisponiveis;
    }

    public void setVagasDisponiveis(int vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }
}
