package model;

public class Repositorios {
    public static Database database = new Database("app.sqlite");
    public static final Repositorio<Estudante, Integer> ESTUDANTE =
        new Repositorio<>(database, Estudante.class);
    public static final Repositorio<Matricula, Integer> MATRICULA =
        new Repositorio<>(database, Matricula.class);        
    public static final Repositorio<Disciplina, Integer> DISCIPLINA =
        new Repositorio<>(database, Disciplina.class);   
    public static final Repositorio<Turma, Integer> TURMA =
        new Repositorio<>(database, Turma.class);
}