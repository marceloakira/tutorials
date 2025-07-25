package br.ufg.inf.sga.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A classe de teste EstudanteTest.
 *
 * @author  (seu nome)
 * @version (um número da versão ou uma data)
 */
public class EstudanteTest
{
    /**
     * Construtor default para a classe de teste EstudanteTest
     */
    public EstudanteTest()
    {
    }

    /**
     * Define a 'fixture' do teste.
     *
     * Chamado antes de cada método de caso de teste.
     */
    @BeforeEach
    public void setUp()
    {
    }

    /**
     * Desfaz a 'fixture' do teste.
     *
     * Chamado após cada método de teste de caso.
     */
    @AfterEach
    public void tearDown()
    {
    }

    @Test
    public void testCreate()
    {
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("João da Silva");
        estudante.setMatricula(123678);

        Estudante estudanteSalvo = Repositorios.ESTUDANTE.create(estudante);

        System.out.println("ID do estudante criado: " + estudanteSalvo.getId());

        assertTrue(estudanteSalvo.getId() > 0, "Identificador do estudante não foi gerado");
        assertEquals(123678, estudanteSalvo.getMatricula(), "Matricula não salva corretamente");

        int idEstudante = estudanteSalvo.getId();

        Repositorios.ESTUDANTE.delete(estudanteSalvo);

        assertNull(
            Repositorios.ESTUDANTE.loadFromId(idEstudante),
            "Estudante não deletado corretamente"
        );
    }
}
