package br.ufg.inf.sga.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ufg.inf.sga.model.Repositorios;
import br.ufg.inf.sga.model.Disciplina;

/**
 * A classe de teste DisciplinaTest.
 *
 * @author  (seu nome)
 * @version (um número da versão ou uma data)
 */
public class DisciplinaTest
{
    /**
     * Construtor default para a classe de teste DisciplinaTest
     */
    public DisciplinaTest()
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
        Disciplina disciplina = new Disciplina();
        disciplina.setTitulo("Teste de Disciplina");
        disciplina.setCodigo("COD_TEST");
        Disciplina disciplina_salva = Repositorios.DISCIPLINA.create(disciplina);
        System.out.println("código da disciplina: "+disciplina_salva.getId());
        assert( disciplina_salva.getId() > 0):
            "Identificador da disciplina não foi gerado";
        assert( disciplina_salva.getCodigo() == "COD_TEST"):
            "Disciplina não salva corretamente";
        int id_disciplina = disciplina_salva.getId();
        Repositorios.DISCIPLINA.delete(disciplina_salva);
        assert (Repositorios.DISCIPLINA.loadFromId(id_disciplina) == null):
            "Disciplina não deletada";
    }
}

