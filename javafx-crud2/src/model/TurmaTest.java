package model;



import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A classe de teste TurmaTest.
 *
 * @author  (seu nome)
 * @version (um número da versão ou uma data)
 */
public class TurmaTest
{
    Disciplina disciplina;
    
    /**
     * Construtor default para a classe de teste TurmaTest
     */
    public TurmaTest()
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
        Disciplina disciplina = new Disciplina();
        disciplina.setTitulo("Teste de Disciplina");
        disciplina.setCodigo("COD_TEST");
        this.disciplina = Repositorios.DISCIPLINA.create(disciplina);
    }

    /**
     * Desfaz a 'fixture' do teste.
     *
     * Chamado após cada método de teste de caso.
     */
    @AfterEach
    public void tearDown()
    {
        Repositorios.DISCIPLINA.delete(this.disciplina);
    }

    @Test
    public void testCreate()
    {
        model.Turma turma1 = new model.Turma();
        turma1.setDisciplina(disciplina);
        turma1.setVagasDisponiveis(40);
        turma1.setCodigo("TEST_T1");
        model.Repositorio<model.Turma,java.lang.Integer> repoTurmas = Repositorios.TURMA;
        model.Turma turma_salva = (model.Turma)repoTurmas.create(turma1);
        assert( turma_salva.getId() > 0 ):
            "Turma salva com identificador inválido";
        assert( turma_salva.getCodigo() == "TEST_T1" ):
            "Turma com código salvo incorreto";
        assert( turma_salva.getVagasDisponiveis() == 40 ):
            "Turma com vagas disponíveis inválidas";
        repoTurmas.delete(turma_salva);
        assert( repoTurmas.loadFromId(turma_salva.getId()) == null ):
            "Turma não deletada corretamente";
    }
}


