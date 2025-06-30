package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Matricula {

    private SimpleIntegerProperty id;
    private SimpleStringProperty estudanteNome;
    private SimpleIntegerProperty estudanteId;
    private SimpleStringProperty turmaCodigo;
    private SimpleIntegerProperty turmaId;
    private SimpleStringProperty status;

    public Matricula(int id, String estudanteNome, int estudanteId, String turmaCodigo, int turmaId, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.estudanteNome = new SimpleStringProperty(estudanteNome);
        this.estudanteId = new SimpleIntegerProperty(estudanteId);
        this.turmaCodigo = new SimpleStringProperty(turmaCodigo);
        this.turmaId = new SimpleIntegerProperty(turmaId);
        this.status = new SimpleStringProperty(status);
    }

    // GET/SET ID
    public int getId() {
        return this.id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // GET/SET Estudante Nome
    public String getEstudanteNome() {
        return this.estudanteNome.get();
    }

    public void setEstudanteNome(String estudanteNome) {
        this.estudanteNome.set(estudanteNome);
    }
    
    // GET/SET Estudante Nome
    public int getEstudanteId() {
        return this.estudanteId.get();
    }

    public void setEstudanteId(int estudanteId) {
        this.estudanteId.set(estudanteId);
    }    

    // GET/SET Turma Código
    public String getTurmaCodigo() {
        return this.turmaCodigo.get();
    }

    public void setTurmaCodigo(String turmaCodigo) {
        this.turmaCodigo.set(turmaCodigo);
    }
    
    // GET/SET Turma Id
    public int getTurmaId() {
        return this.turmaId.get();
    }

    public void setTurmaId(int turmaId) {
        this.turmaId.set(turmaId);
    }

    // GET/SET Status
    public String getStatus() {
        return this.status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
