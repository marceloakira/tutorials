package br.ufg.inf.sb.repository;

import br.ufg.inf.sb.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repo;

    @Test
    void testSalvarEConsultar() {
        // Cria e salva um usuário
        Usuario u = new Usuario("Silva", "Maria", "ATIVO");
        Usuario salvo = repo.save(u);

        // Verifica se o ID foi gerado
        assertNotNull(salvo.getId());

        // Busca todos os usuários
        List<Usuario> lista = repo.findAll();

        // Verifica se há pelo menos um
        assertTrue(lista.size() > 0);
    }
}