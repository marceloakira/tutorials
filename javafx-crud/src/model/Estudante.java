package model;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;

@DatabaseTable(tableName="estudante")
public class Estudante {
    
    @DatabaseField(generatedId = true)
    private int id;
        
    @DatabaseField(dataType=DataType.STRING)    
    private String nomeCompleto;
    
    @DatabaseField(dataType=DataType.STRING)        
    private String dataDeNascimento;
    
    @DatabaseField(dataType=DataType.INTEGER)        
    private int matricula;
    

//Start GetterSetterExtension Source Code

    /**GET Method Propertie id*/
    public int getId(){
        return this.id;
    }//end method getId

    /**SET Method Propertie id*/
    public void setId(int id){
        this.id = id;
    }//end method setId

    /**GET Method Propertie nomeCompleto*/
    public String getNomeCompleto(){
        return this.nomeCompleto;
    }//end method getNomeCompleto

    /**SET Method Propertie nomeCompleto*/
    public void setNomeCompleto(String nomeCompleto){
        this.nomeCompleto = nomeCompleto;
    }//end method setNomeCompleto

    /**GET Method Propertie dataDeNascimento*/
    public String getDataDeNascimento(){
        return this.dataDeNascimento;
    }//end method getDataDeNascimento

    /**SET Method Propertie dataDeNascimento*/
    public void setDataDeNascimento(String dataDeNascimento){
        this.dataDeNascimento = dataDeNascimento;
    }//end method setDataDeNascimento

    /**GET Method Propertie matricula*/
    public int getMatricula(){
        return this.matricula;
    }//end method getMatricula

    /**SET Method Propertie matricula*/
    public void setMatricula(int matricula){
        this.matricula = matricula;
    }//end method setMatricula

//End GetterSetterExtension Source Code


}//End class