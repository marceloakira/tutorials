package br.ufg.inf.integrador;

import br.ufg.inf.sga.model.Estudante;
import br.ufg.inf.sga.model.Repositorios;

public record Main() {
    public static void main(String[] args) {
        System.out.println("Running CRUD Event Test...");
        
        // Example instantiation of Estudante
        Estudante estudante = new Estudante();
        estudante.setNomeCompleto("João da Silva");
        estudante.setDataDeNascimento("01/01/2000");
        estudante.setMatricula(123456);

        Repositorios.ESTUDANTE.create(estudante);
        // Estudante criado deve aparecer na console do RedisListenerServer
    }    
}
