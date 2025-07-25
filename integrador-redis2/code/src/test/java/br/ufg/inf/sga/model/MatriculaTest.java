package br.ufg.inf.sga.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de teste para Matricula.
 * Testa criação e remoção de matrículas.
 */
public class MatriculaTest
{
    private Estudante estudanteCriado;
    private Disciplina disciplinaCriada;

    @BeforeEach
    public void setUp()
    {
        // Criar um estudante
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("Maria Teste");
        estudante.setMatricula(999123);
        estudanteCriado = Repositorios.ESTUDANTE.create(estudante);

        // Criar uma disciplina
        Disciplina disciplina = new Disciplina();
        disciplina.setTitulo("Disciplina de Teste");
        disciplina.setCodigo("DISC999");
        disciplinaCriada = Repositorios.DISCIPLINA.create(disciplina);
    }

    @AfterEach
    public void tearDown()
    {
        if (disciplinaCriada != null) {
            Repositorios.DISCIPLINA.delete(disciplinaCriada);
        }
        if (estudanteCriado != null) {
            Repositorios.ESTUDANTE.delete(estudanteCriado);
        }
    }

    @Test
    public void testCreateAndDeleteMatricula()
    {
        // Criar uma turma associada à disciplina
        Turma turma = new Turma();
        turma.setCodigo("TURMA999");
        turma.setDisciplina(disciplinaCriada);
        turma.setVagasDisponiveis(30);
        turma.setAlunosMatriculados(0); // persisted=false, não vai ao banco
        Turma turmaCriada = Repositorios.TURMA.create(turma);

        // Criar matrícula
        Matricula matricula = new Matricula();
        matricula.setEstudante(estudanteCriado);
        matricula.setTurma(turmaCriada);
        matricula.setStatus(StatusMatricula.CONFIRMADA);

        // Salvar matrícula
        Matricula matriculaSalva = Repositorios.MATRICULA.create(matricula);

        // Verificar se a matrícula foi salva corretamente
        assertTrue(matriculaSalva.getId() > 0, "ID da matrícula não foi gerado");
        assertEquals(StatusMatricula.CONFIRMADA, matriculaSalva.getStatus(), "Status não salvo corretamente");
        assertEquals(estudanteCriado.getId(), matriculaSalva.getEstudante().getId(), "Estudante não associado corretamente");
        assertEquals(turmaCriada.getId(), matriculaSalva.getTurma().getId(), "Turma não associada corretamente");

        int idMatricula = matriculaSalva.getId();

        // Deletar matrícula
        Repositorios.MATRICULA.delete(matriculaSalva);

        // Verificar se foi removida
        assertNull(
            Repositorios.MATRICULA.loadFromId(idMatricula),
            "Matrícula não deletada corretamente"
        );

        // Deletar turma criada
        Repositorios.TURMA.delete(turmaCriada);
    }
}
