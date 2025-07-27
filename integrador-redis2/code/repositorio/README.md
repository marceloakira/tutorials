# Repositório - Biblioteca de Acesso a Dados

Esta é uma biblioteca Maven reutilizável que fornece classes genéricas para acesso a dados com integração ao Redis para publicação de eventos CRUD.

## Componentes

### Database
Classe responsável por gerenciar a conexão com o banco de dados SQLite usando ORMLite.

**Características:**
- Gerenciamento automático de conexões
- Integração com ORMLite
- Suporte a SQLite

### Repositorio<T, ID>
Classe genérica que implementa o padrão Repository para operações CRUD com qualquer entidade.

**Características:**
- Operações CRUD genéricas (Create, Read, Update, Delete)
- Publicação automática de eventos no Redis via RedisPublisher
- Integração com ORMLite para persistência
- Suporte a qualquer tipo de entidade e ID

## Dependências

- **ORMLite**: Para mapeamento objeto-relacional
- **SQLite**: Banco de dados
- **Gson**: Para serialização JSON
- **Publicador**: Para publicação de eventos no Redis

## Como usar

### 1. Adicione a dependência ao seu projeto

```xml
<dependency>
    <groupId>br.ufg.inf</groupId>
    <artifactId>repositorio</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Exemplo de uso

```java
import br.ufg.inf.repositorio.Database;
import br.ufg.inf.repositorio.Repositorio;

// Criar conexão com o banco
Database database = new Database("app.sqlite");

// Criar repositório para uma entidade específica
Repositorio<MinhaEntidade, Long> repositorio = 
    new Repositorio<>(database, MinhaEntidade.class);

// Usar operações CRUD
MinhaEntidade entidade = new MinhaEntidade();
repositorio.create(entidade);

List<MinhaEntidade> todas = repositorio.loadAll();
MinhaEntidade porId = repositorio.loadFromId(1L);

repositorio.update(entidade);
repositorio.delete(entidade);

// Fechar conexão
database.close();
```

## Eventos Redis

Todas as operações CRUD (CREATE, UPDATE, DELETE) são automaticamente publicadas no Redis através do componente `publicador`, permitindo que outros sistemas sejam notificados das mudanças em tempo real.

## Compilação

```bash
mvn clean compile
mvn test
mvn package
```

## Instalação no repositório local

```bash
mvn clean install
```

Isso instalará a biblioteca no repositório Maven local, permitindo que outros projetos a utilizem como dependência.
