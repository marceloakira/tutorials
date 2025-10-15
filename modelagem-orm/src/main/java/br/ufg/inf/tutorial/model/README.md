# Entidades JPA

Este diretório contém as classes do modelo de domínio mapeadas com JPA/Hibernate.

## Estrutura das Entidades

### Hierarquia de Herança
- **Usuario** (superclasse abstrata)
  - **Estudante** (subclasse)
  - **Professor** (subclasse)

### Entidades Principais
- **PerfilUsuario** - Relacionamento 1:1 com Usuario
- **Disciplina** - Entidade independente
- **Turma** - Relacionamento N:1 com Disciplina e Professor
- **Matricula** - Classe de associação (N:M entre Estudante e Turma)

## Relacionamentos Implementados

| Relacionamento | Cardinalidade | Anotação JPA | Fetch Strategy |
|---|---|---|---|
| Usuario ↔ PerfilUsuario | 1:1 | @OneToOne | LAZY |
| Disciplina ↔ Turma | 1:N | @OneToMany / @ManyToOne | LAZY |
| Professor ↔ Turma | N:M | @ManyToMany | LAZY |
| Estudante ↔ Matricula | 1:N | @OneToMany / @ManyToOne | LAZY |
| Turma ↔ Matricula | 1:N | @OneToMany / @ManyToOne | LAZY |

## Estratégias de Mapeamento

### Herança
- **Estratégia**: Table Per Class Concrete (JOINED)
- **Discriminator**: Não necessário (tabelas separadas)

### Identificadores
- **Estratégia**: IDENTITY (auto-increment)
- **Tipo**: Long

### Validações
- **Bean Validation**: Implementado com anotações Jakarta
- **Constraints**: @NotNull, @NotBlank, @Email, @Size, etc.

## Arquivos a serem criados:

- [ ] `Usuario.java` - Classe abstrata base
- [ ] `Estudante.java` - Subclasse de Usuario
- [ ] `Professor.java` - Subclasse de Usuario  
- [ ] `PerfilUsuario.java` - Perfil do usuário
- [ ] `Disciplina.java` - Disciplina acadêmica
- [ ] `Turma.java` - Turma de uma disciplina
- [ ] `Matricula.java` - Matrícula de estudante em turma