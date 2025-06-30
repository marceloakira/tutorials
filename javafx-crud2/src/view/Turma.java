package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Turma {

    private SimpleIntegerProperty id;
    private SimpleStringProperty codigo;
    private SimpleIntegerProperty disciplinaId;
    private SimpleStringProperty disciplinaCodigo;
    private SimpleIntegerProperty vagasDisponiveis;
    private SimpleIntegerProperty alunosMatriculados;

    public Turma(int id, String codigo, int disciplinaId, String disciplinaCodigo, int vagasDisponiveis, int alunosMatriculados) {
        this.id = new SimpleIntegerProperty(id);
        this.codigo = new SimpleStringProperty(codigo);
        this.disciplinaId = new SimpleIntegerProperty(disciplinaId);
        this.disciplinaCodigo = new SimpleStringProperty(disciplinaCodigo);
        this.vagasDisponiveis = new SimpleIntegerProperty(vagasDisponiveis);
        this.alunosMatriculados = new SimpleIntegerProperty(alunosMatriculados);
    }

    // GET/SET ID
    public int getId() {
        return this.id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // GET/SET Código
    public String getCodigo() {
        return this.codigo.get();
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    // GET/SET Disciplina Id
    public int getDisciplinaId() {
        return this.disciplinaId.get();
    }

    public void setDisciplinaId(int disciplinaId) {
        this.disciplinaId.set(disciplinaId);
    }

    // GET/SET Disciplina Código
    public String getDisciplinaCodigo() {
        return this.disciplinaCodigo.get();
    }

    public void setDisciplinaCodigo(String disciplinaCodigo) {
        this.disciplinaCodigo.set(disciplinaCodigo);
    }

    // GET/SET Vagas Disponíveis
    public int getVagasDisponiveis() {
        return this.vagasDisponiveis.get();
    }

    public void setVagasDisponiveis(int vagasDisponiveis) {
        this.vagasDisponiveis.set(vagasDisponiveis);
    }

    // GET/SET Alunos Matriculados
    public int getAlunosMatriculados() {
        return this.alunosMatriculados.get();
    }

    public void setAlunosMatriculados(int alunosMatriculados) {
        this.alunosMatriculados.set(alunosMatriculados);
    }
}
