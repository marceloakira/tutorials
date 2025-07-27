# Integrador Redis 2 - Código-fonte

## Introdução

Este código-fonte é parte do tutorial disponível em:
- https://github.com/marceloakira/tutorials/tree/main/integrador-redis2

### Estrutura do Projeto

O projeto é composto por múltiplos projetos Maven independentes, onde cada pasta representa um projeto separado com seu próprio arquivo `pom.xml`. Esta estrutura modular permite o desenvolvimento e manutenção independente de cada componente do sistema.

## Ordem de Compilação dos Projetos

Para compilar os projetos na ordem correta devido às dependências, siga esta sequência:

### 1. Publicador
```bash
cd publicador
mvn clean install
```

### 2. Repositorio
```bash
cd repositorio  
mvn clean install
```

### 3. SGA
```bash
cd sga
mvn clean compile
```

### 4. SB
```bash
cd sb
mvn clean compile
```

### 5. Integrador
```bash
cd integrador
mvn clean compile
```

### Compilação completa de todos os projetos
Para compilar tudo de uma vez (a partir do diretório raiz):

```bash
# Primeiro os projetos base
cd publicador && mvn clean install test && cd ..
cd repositorio && mvn clean install test && cd ..

# Depois os projetos que dependem deles
cd sga && mvn clean compile test && cd ..
cd sb && mvn clean compile test && cd ..
cd integrador && mvn clean compile test && cd ..
```

## Dependências
- **Publicador**: Não tem dependências internas
- **Repositorio**: Depende de Publicador  
- **SGA**: Depende de Repositorio (que inclui Publicador)
- **SB**: Independente
- **Integrador**: Depende de Repositorio, SGA e SB
