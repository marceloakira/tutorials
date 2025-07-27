package br.ufg.inf.sb.controller;

import br.ufg.inf.sb.model.Usuario;
import br.ufg.inf.sb.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;

    public UsuarioController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Usuario salvar(@RequestBody Usuario u) {
        return repo.save(u);
    }
}