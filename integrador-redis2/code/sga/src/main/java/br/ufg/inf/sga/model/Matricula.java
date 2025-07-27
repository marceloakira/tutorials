package br.ufg.inf.sga.model;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;


@DatabaseTable(tableName="matricula")
public class Matricula {
    
    @DatabaseField(generatedId = true)
    private int id;
   
    @DatabaseField(columnName = "estudante_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Estudante estudante;    
    
    @DatabaseField(columnName = "turma_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Turma turma;
    
    @DatabaseField(dataType=DataType.ENUM_STRING)
    private StatusMatricula status;

//Start GetterSetterExtension Source Code

    /**GET Method Propertie id*/
    public int getId(){
        return this.id;
    }//end method getId

    /**SET Method Propertie id*/
    public void setId(int id){
        this.id = id;
    }//end method setId

    /**GET Method Propertie estudante*/
    public Estudante getEstudante(){
        return this.estudante;
    }//end method getEstudante

    /**SET Method Propertie estudante*/
    public void setEstudante(Estudante estudante){
        this.estudante = estudante;
    }//end method setEstudante

    /**GET Method Propertie turma*/
    public Turma getTurma(){
        return this.turma;
    }//end method getTurma

    /**SET Method Propertie turma*/
    public void setTurma(Turma turma){
        this.turma = turma;
    }//end method setTurma
    
    /**GET Method Property status*/
    public StatusMatricula getStatus() {
        return this.status;
    }
    
    /**SET Method Property status*/
    public void setStatus(StatusMatricula status) {
        this.status = status;
    }
//End GetterSetterExtension Source Code


}//End class