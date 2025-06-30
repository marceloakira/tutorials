package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Disciplina {

    private SimpleIntegerProperty id;
    private SimpleStringProperty codigo;
    private SimpleStringProperty titulo;
    private SimpleStringProperty ementa;

    public Disciplina(int id, String codigo, String titulo, String ementa) {
        this.id = new SimpleIntegerProperty(id);
        this.codigo = new SimpleStringProperty(codigo);
        this.titulo = new SimpleStringProperty(titulo);
        this.ementa = new SimpleStringProperty(ementa);
    }

    // Start GetterSetterExtension Source Code

    /** GET Method Property id */
    public int getId() {
        return this.id.get();
    }

    /** SET Method Property id */
    public void setId(int id) {
        this.id.set(id);
    }

    /** GET Method Property codigo */
    public String getCodigo() {
        return this.codigo.get();
    }

    /** SET Method Property codigo */
    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    /** GET Method Property titulo */
    public String getTitulo() {
        return this.titulo.get();
    }

    /** SET Method Property titulo */
    public void setTitulo(String titulo) {
        this.titulo.set(titulo);
    }

    /** GET Method Property ementa */
    public String getEmenta() {
        return this.ementa.get();
    }

    /** SET Method Property ementa */
    public void setEmenta(String ementa) {
        this.ementa.set(ementa);
    }

    // End GetterSetterExtension Source Code
}
