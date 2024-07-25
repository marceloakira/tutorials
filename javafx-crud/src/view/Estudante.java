package view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Estudante {
    
    private SimpleIntegerProperty id;
    private SimpleStringProperty nomeCompleto;
    private SimpleStringProperty dataDeNascimento;
    private SimpleIntegerProperty matricula;
    
    public Estudante(int id, String nomeCompleto, 
                    String dataDeNascimento, int matricula) {
        this.id = new SimpleIntegerProperty(id);
        this.nomeCompleto = new SimpleStringProperty(nomeCompleto);
        this.dataDeNascimento = new SimpleStringProperty(dataDeNascimento);
        this.matricula = new SimpleIntegerProperty(matricula);        
    }
    
//Start GetterSetterExtension Source Code

    /**GET Method Propertie id*/
    public int getId(){
        return this.id.get();
    }//end method getId

    /**SET Method Propertie id*/
    public void setId(int id){
        this.id.set(id);
    }//end method setNomeCompleto

    /**GET Method Propertie nomeCompleto*/
    public String getNomeCompleto(){
        return this.nomeCompleto.get();
    }//end method getNomeCompleto

    /**SET Method Propertie nomeCompleto*/
    public void setNomeCompleto(String nomeCompleto){
        this.nomeCompleto.set(nomeCompleto);
    }//end method setNomeCompleto

    /**GET Method Propertie dataDeNascimento*/
    public String getDataDeNascimento(){
        return this.dataDeNascimento.get();
    }//end method getDataDeNascimento

    /**SET Method Propertie dataDeNascimento*/
    public void setDataDeNascimento(String dataDeNascimento){
        this.dataDeNascimento.set(dataDeNascimento);
    }//end method setDataDeNascimento

    /**GET Method Propertie matricula*/
    public int getMatricula(){
        return this.matricula.get();
    }//end method getMatricula
    /**SET Method Propertie matricula*/
    public void setMatricula(int matricula){
        this.matricula.set(matricula);
    }//end method setMatricula

//End GetterSetterExtension Source Code


}//End class