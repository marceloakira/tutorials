package model;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;

@DatabaseTable(tableName="disciplina")
public class Disciplina {
    
    @DatabaseField(generatedId = true)
    private int id;
        
    @DatabaseField(dataType=DataType.STRING)    
    private String codigo;
    
    @DatabaseField(dataType=DataType.STRING)        
    private String titulo;
    
    @DatabaseField(dataType=DataType.STRING)        
    private String ementa;
    
    @Override
    public String toString() {
        return this.codigo;
    }    

//Start GetterSetterExtension Source Code

    /**GET Method Propertie id*/
    public int getId(){
        return this.id;
    }//end method getId

    /**SET Method Propertie id*/
    public void setId(int id){
        this.id = id;
    }//end method setId

    /**GET Method Propertie codigo*/
    public String getCodigo(){
        return this.codigo;
    }//end method getCodigo

    /**SET Method Propertie codigo*/
    public void setCodigo(String codigo){
        this.codigo = codigo;
    }//end method setCodigo

    /**GET Method Propertie titulo*/
    public String getTitulo(){
        return this.titulo;
    }//end method getTitulo

    /**SET Method Propertie titulo*/
    public void setTitulo(String titulo){
        this.titulo = titulo;
    }//end method setTitulo

    /**GET Method Propertie ementa*/
    public String getEmenta(){
        return this.ementa;
    }//end method getEmenta

    /**SET Method Propertie ementa*/
    public void setEmenta(String ementa){
        this.ementa = ementa;
    }//end method setEmenta

//End GetterSetterExtension Source Code


}//End class