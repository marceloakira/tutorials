# 🟢 Java ODM com Spring Data MongoDB e Docker (Explicado Passo a Passo)

## 1. O que vamos fazer

Neste tutorial, baseado no tutorial [Spring Data MongoDB](https://spring.io/guides/gs/accessing-data-mongodb), você vai:

- Criar um banco MongoDB usando Docker.
- Criar um projeto Java com [Spring Boot](https://spring.io/projects/spring-boot) e [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb).
- Modelar a entidade `Estudante`, equivalente ao seu exemplo com ORMLite.
- Configurar persistência automática dos dados.
- Realizar operações CRUD (create/read/update/delete).

**Por que usar Spring Data MongoDB?**
- Ele cuida de quase toda a infraestrutura de acesso a dados.
- Você não precisa escrever comandos MongoDB manualmente.
- É um *ODM* (Object Document Mapper): converte automaticamente objetos Java em documentos prontos para serem salvos no MongoDB.

**Outras opções de ODM para Java**
Além do Spring Data MongoDB, existem outras bibliotecas para mapeamento objeto-documento no Java:

- [Morphia](https://morphia.dev/): uma API enxuta e orientada a documentos, [apoiada pela empresa MongoDB](https://www.mongodb.com/resources/languages/morphia).
- [MongoJack](https://github.com/mongojack/mongojack): integra [Jackson JSON Mapper](https://github.com/FasterXML/jackson) diretamente com MongoDB.

Neste tutorial, vamos usar Spring Boot e Spring Data MongoDB por serem muito bem integrados e produtivos, mas vale conhecer essas alternativas.


## 2. Instalação do MongoDB via Docker

Vamos usar Docker para evitar instalação manual no sistema operacional.

> **Docker** é uma plataforma para rodar aplicativos em containers, que são ambientes isolados e reproduzíveis.

---

### Passos

1. **Baixar a imagem oficial do MongoDB**

   Este comando baixa a última versão da Community Edition do MongoDB:

   ```bash
   docker pull mongodb/mongodb-community-server:latest
   ```

   🔍 **O que faz esse comando?**
   - Faz download da imagem do MongoDB que contém tudo pronto para execução.
   - Você só precisa rodar isso uma única vez.

2. **Criar e iniciar o container**

   O comando abaixo cria o container, define nome, portas e volume persistente:

   ```bash
   docker run -d \
      --name mongodb \
      -p 27017:27017 \
      -v mongodb_data:/data/db \
      mongodb/mongodb-community-server:latest
   ```

   🔍 **Explicando cada parâmetro:**
   - `-d`: roda o container em background.
   - `--name mongodb`: dá um nome amigável ao container.
   - `-p 27017:27017`: mapeia a porta padrão do MongoDB para o host.
   - `-v mongodb_data:/data/db`: cria um volume chamado `mongodb_data` que guarda os dados.
   - A última parte indica a imagem a ser usada.

3. **Verificar se o container está ativo**

   Use o comando:

   ```bash
   docker ps
   ```

   Você deve ver algo parecido com:

   ```
   CONTAINER ID   IMAGE                                        PORTS
   abcd1234efgh   mongodb/mongodb-community-server:latest      0.0.0.0:27017->27017/tcp
   ```

4. **Acessar o shell do MongoDB (opcional)**

   Para entrar no prompt interativo do MongoDB, execute:

   ```bash
   docker exec -it mongodb mongosh
   ```

   Exemplo de comandos que você pode usar dentro do shell:

   ```mongodb
   show dbs
   ```

   ```mongodb
   use test
   ```

   ```mongodb
   db.createCollection("exemplo")
   ```


5. **Acessando o MongoDB através do VS Code (opcional)**

Se preferir, você pode gerenciar seu banco MongoDB usando a extensão **MongoDB for VS Code**.

#### Passos

1. Abra o VS Code.
2. Vá no menu lateral esquerdo, clique no ícone de **Extensions** (ou use o atalho `Ctrl + Shift + X`).
3. Pesquise por **MongoDB for VS Code** e instale a extensão oficial da MongoDB Inc.
4. Após instalar, clique no ícone do **MongoDB** no menu lateral esquerdo.
5. Clique em **Connect**.
6. Na tela de conexão, informe a string de conexão:

   ```
   mongodb://localhost:27017
   ```

   > Como estamos rodando localmente no Docker, não é necessário usuário nem senha por padrão.

   ![MongoDB Connection](mongodb-connect.png)

7. Clique em **Connect**.

Após conectar, você poderá:

- Visualizar bancos e coleções.
- Criar documentos.
- Executar queries com sintaxe MongoDB.
- Editar dados interativamente.

![](mongodb-vscode.png)

Essa é uma opção prática para explorar seus dados sem sair do VS Code.


## 3. Criação do projeto Java (Spring Boot) no VS Code

Nesta etapa, você vai criar um projeto Maven com Spring Boot que será a base da sua aplicação.

> **O que é Maven?**
> Maven é uma ferramenta de automação de builds para projetos Java.  
> Ela gerencia dependências (bibliotecas externas), compila, empacota e executa aplicações de forma padronizada.
> Saiba mais: [https://maven.apache.org/](https://maven.apache.org/)

> **Por que usar Spring Boot?**
> - Automatiza configuração do projeto.
> - Facilita dependências e inicialização.
> - Integra diretamente com MongoDB por meio do Spring Data.

---

### Passos

1. **Criar a pasta do projeto**

   No terminal, execute:

   ```bash
   mkdir java-mongodb-odm
   cd java-mongodb-odm
   ```

   🔍 *O que faz:*
   - `mkdir` cria a pasta chamada `java-mongodb-odm`.
   - `cd` entra na pasta.

2. **Gerar o projeto Maven**

   Use este comando para criar um projeto básico Maven:

   ```bash
   mvn archetype:generate \
   -DgroupId=br.ufg.inf \
   -DartifactId=java-mongodb-odm \
   -DarchetypeArtifactId=maven-archetype-quickstart \
   -DarchetypeVersion=1.4 \
   -DinteractiveMode=false
   ```

   🔍 *Explicando cada parâmetro:*
   - `groupId`: identificador base do projeto (`br.ufg.inf`).
   - `artifactId`: nome do projeto/pasta.
   - `archetypeArtifactId`: modelo de projeto Maven.
   - `interactiveMode=false`: evita perguntas interativas.

3. **Abrir no VS Code**

   ```bash
   code java-mongodb-odm
   ```

   > Se não tiver o comando `code` configurado, abra manualmente a pasta no VS Code.

4. **Editar o arquivo `pom.xml`**

   O `pom.xml` define dependências e configurações do projeto.  
   Substitua o conteúdo padrão pelo seguinte:

   ```xml
      <project xmlns="http://maven.apache.org/POM/4.0.0"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>

      <groupId>br.ufg.inf</groupId>
      <artifactId>java-mongodb-odm</artifactId>
      <version>1.0-SNAPSHOT</version>

      <parent>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-parent</artifactId>
         <version>3.3.0</version>
      </parent>

      <dependencies>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
         </dependency>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
         </dependency>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
         </dependency>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
         </dependency>
      </dependencies>
      </project>
   ```

🔍 *Por que usar essas dependências?*
- `spring-boot-starter-data-mongodb`: ODM do MongoDB (mapeamento de documentos).
- `spring-boot-starter`: base do Spring Boot (configuração e execução).
- `spring-boot-starter-test`: suporte completo a testes com Spring Boot, incluindo:
  - JUnit 5 (JUnit Jupiter)
  - AssertJ
  - Mockito
  - Infraestrutura de testes de contexto Spring

5. **Criar a classe principal do projeto**

   Altere o conteúdo do arquivo `App.java` criado automaticamente:

   ```
   src/main/java/br/ufg/inf/App.java
   ```

   Com o seguinte conteúdo:

   ```java
      package br.ufg.inf;

      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;

      @SpringBootApplication
      public class App {
         public static void main(String[] args) {
            SpringApplication.run(App.class, args);
         }
      }
   ```

   🔍 *O que essa classe faz?*
   - É o ponto de entrada da aplicação.
   - Inicia o contexto Spring Boot.

---

### ✅ Testando a configuração do projeto

Para garantir que tudo está correto antes de prosseguir, execute no terminal dentro da pasta do projeto:

```bash
mvn clean install
```

🔍 **O que esse comando faz?**
- `clean`: apaga arquivos gerados anteriormente (`target`).
- `install`: compila a aplicação, executa testes e instala o artefato no repositório local Maven.

**Resultado esperado:**
No final, você verá:

```
[INFO] BUILD SUCCESS
```

Isso confirma que:
✅ O `pom.xml` está correto.  
✅ O projeto compila normalmente.  
✅ Os testes rodam sem erro.

💡 **Dica extra:**
Você também pode executar a aplicação para ver se inicia sem erros:

```bash
mvn spring-boot:run
```

Se tudo estiver certo, verá:

```
Started Application in X seconds
```


## 4. Configuração da conexão MongoDB

Nesta etapa, você vai criar o arquivo de configuração que indica onde está rodando seu banco MongoDB.

> **Por que isso é necessário?**
> O Spring Boot precisa saber:
> - O host (endereço) onde o MongoDB está disponível.
> - A porta que ele usa.
> - O nome do banco de dados que será utilizado.

---

### Passos

1. **Criar o arquivo de configuração**

   No seu projeto, abra a pasta:

   ```
   src/main/resources
   ```

   Crie um arquivo chamado:

   ```
   application.properties
   ```

   Esse arquivo contém todas as configurações básicas do Spring Boot.

2. **Adicionar as propriedades de conexão**

   Cole o seguinte conteúdo:

   ```
   spring.data.mongodb.host=localhost
   spring.data.mongodb.port=27017
   spring.data.mongodb.database=meu_banco
   ```

   🔍 *Explicando cada linha:*
   - `host`: endereço do MongoDB (no nosso caso, o Docker publica na máquina local).
   - `port`: porta padrão do MongoDB.
   - `database`: nome do banco que será criado automaticamente ao persistir o primeiro documento.


## 5. Implementação do modelo Estudante

Agora vamos criar a classe que representa o documento que será salvo no MongoDB.

> **Por que criar um modelo?**
> No Spring Data, o modelo mapeia automaticamente os campos do Java para o MongoDB.

---

### Passos

1. **Criar o pacote do modelo**

   No seu projeto, crie a pasta:

   ```
   src/main/java/br/ufg/inf/model
   ```

2. **Criar a classe Estudante**

   Crie o arquivo:

   ```
   src/main/java/br/ufg/inf/model/Estudante.java
   ```

   Cole este conteúdo:

   ```java
   package br.ufg.inf.model;

   import org.springframework.data.annotation.Id;
   import org.springframework.data.mongodb.core.mapping.Document;

   @Document(collection = "estudante")
   public class Estudante {

       @Id
       private String id;
       private String nomeCompleto;
       private String dataDeNascimento;
       private int matricula;

       public Estudante() {}

       public Estudante(String nomeCompleto, String dataDeNascimento, int matricula) {
           this.nomeCompleto = nomeCompleto;
           this.dataDeNascimento = dataDeNascimento;
           this.matricula = matricula;
       }

       public String getId() {
           return id;
       }

       public String getNomeCompleto() {
           return nomeCompleto;
       }

       public void setNomeCompleto(String nomeCompleto) {
           this.nomeCompleto = nomeCompleto;
       }

       public String getDataDeNascimento() {
           return dataDeNascimento;
       }

       public void setDataDeNascimento(String dataDeNascimento) {
           this.dataDeNascimento = dataDeNascimento;
       }

       public int getMatricula() {
           return matricula;
       }

       public void setMatricula(int matricula) {
           this.matricula = matricula;
       }
   }
   ```

   🔍 *Explicando as anotações:*
   - `@Document`: indica que essa classe será mapeada para uma coleção MongoDB.
   - `@Id`: indica o campo que será usado como `_id`.



## 6. Implementação do repositório

Agora vamos criar a interface que será responsável por acessar o banco de dados MongoDB.

> **O que é um repositório?**
> No Spring Data, um repositório é uma interface que define operações de CRUD (criar, ler, atualizar, excluir) sobre seu modelo.
> O Spring Data MongoDB cria automaticamente a implementação em tempo de execução.

---

### Passos

1. **Criar o pacote do repositório**

   No seu projeto, crie a pasta:

   ```
   src/main/java/br/ufg/inf/repository
   ```

2. **Criar a interface EstudanteRepository**

   Crie o arquivo:

   ```
   src/main/java/br/ufg/inf/repository/EstudanteRepository.java
   ```

   Cole este conteúdo:

   ```java
   package br.ufg.inf.repository;

   import br.ufg.inf.model.Estudante;
   import org.springframework.data.mongodb.repository.MongoRepository;
   import org.springframework.stereotype.Repository;

   @Repository
   public interface EstudanteRepository extends MongoRepository<Estudante, String> {
   }
   ```

   🔍 *Explicando cada parte:*
   - `MongoRepository<Estudante, String>`: indica que esta interface gerencia documentos do tipo `Estudante` e usa `String` como o tipo do ID.
   - `@Repository`: marca esta interface como um componente Spring.

---

✅ Com isso, o Spring Boot cria automaticamente métodos como:
- `save()`: salvar um documento.
- `findAll()`: listar todos os documentos.
- `findById()`: buscar por ID.
- `delete()`: remover documentos.

Você não precisa escrever nenhuma implementação manual.


## 7. Teste de integração do repositório com JUnit 5

Nesta etapa, vamos criar um teste automatizado que valida se o repositório `EstudanteRepository` está funcionando corretamente.

> **Por que criar este teste?**
> Assim você confirma que:
> - O repositório está sendo injetado.
> - O MongoDB está conectado.
> - As operações de persistência funcionam como esperado.

---

### Passos

1. **Criar o pacote de testes**

   No seu projeto, crie a pasta:

   ```
   src/test/java/br/ufg/inf/repository
   ```

2. **Criar a classe de teste**

   Crie o arquivo:

   ```
   src/test/java/br/ufg/inf/repository/EstudanteRepositoryTest.java
   ```

   Cole este conteúdo:

   ```java
   package br.ufg.inf.repository;

   import br.ufg.inf.model.Estudante;
   import org.junit.jupiter.api.Test;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

   import java.util.List;

   import static org.junit.jupiter.api.Assertions.assertTrue;
   import static org.junit.jupiter.api.Assertions.assertNotNull;

   @DataMongoTest
   class EstudanteRepositoryTest {

       @Autowired
       private EstudanteRepository repo;

       @Test
       void testSalvarEConsultar() {
           // Cria e salva um estudante
           Estudante e = new Estudante("Maria Teste", "1990-01-01", 12345);
           Estudante salvo = repo.save(e);

           // Verifica se o ID foi gerado
           assertNotNull(salvo.getId());

           // Busca todos os estudantes
           List<Estudante> lista = repo.findAll();

           // Verifica se há pelo menos um
           assertTrue(lista.size() > 0);
       }
   }
   ```

   🔍 *Explicando cada parte:*
   - `@DataMongoTest`: sobe um contexto Spring minimal que configura apenas MongoDB.
   - `@Test`: marca o método como teste JUnit 5.
   - `assertNotNull()`: valida se o documento foi salvo.
   - `assertTrue()`: confirma que a lista retornou registros.

---

3. **Executar o teste**

   No terminal, rode:

   ```bash
   mvn test
   ```

   Se tudo estiver correto, você verá algo assim:

   ```
   -------------------------------------------------------
    T E S T S
   -------------------------------------------------------
   Running br.ufg.inf.repository.EstudanteRepositoryTest
   Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
   ```

   Isso confirma que:
   ✅ O repositório foi injetado.
   ✅ A persistência funcionou.
   ✅ O MongoDB respondeu.

---

4. **Verificar os dados no banco via VS Code**

   Se quiser checar visualmente os dados, você pode usar a extensão gratuita **MongoDB for VS Code**.

   **Como instalar e usar:**

   - Abra o **VS Code**.
   - Vá em **Extensões** (`Ctrl+Shift+X`).
   - Busque por **MongoDB for VS Code** (fornecida pela MongoDB Inc.).
   - Clique em **Install**.
   - Clique no ícone de **MongoDB** no lado esquerdo da tela.
   - Clique em **Connect**.
   - Use a string de conexão:

     ```
     mongodb://localhost:27017
     ```

   - No painel de bancos de dados, expanda:
     - O banco configurado (`meu_banco` ou `test`).
     - A coleção `estudante`.
   - Clique com o botão direito em **estudante** > **View Documents**.

   Você verá os documentos que o teste inseriu.


## 8. Executando a aplicação e acessando via REST

Agora que o projeto está configurado, o modelo foi criado e os testes passaram, você vai iniciar a aplicação e expor os endpoints REST para consultar e salvar estudantes.

---

### Passos

1. **Abrir o terminal**

   Certifique-se de estar na pasta raiz do projeto:

   ```
   java-mongodb-odm
   ```

2. **Criar o controlador REST**

   Vamos criar um controlador HTTP que permitirá enviar e consultar estudantes pelo navegador ou por ferramentas como Postman.

   Crie o arquivo:

   ```
   src/main/java/br/ufg/inf/controller/EstudanteController.java
   ```

   Com este conteúdo:

   ```java
   package br.ufg.inf.controller;

   import br.ufg.inf.model.Estudante;
   import br.ufg.inf.repository.EstudanteRepository;
   import org.springframework.web.bind.annotation.*;

   import java.util.List;

   @RestController
   @RequestMapping("/estudantes")
   public class EstudanteController {

       private final EstudanteRepository repo;

       public EstudanteController(EstudanteRepository repo) {
           this.repo = repo;
       }

       @GetMapping
       public List<Estudante> listar() {
           return repo.findAll();
       }

       @PostMapping
       public Estudante salvar(@RequestBody Estudante e) {
           return repo.save(e);
       }
   }
   ```

   🔍 *Explicando cada rota:*
   - `GET /estudantes`: lista todos os registros.
   - `POST /estudantes`: cadastra um novo estudante.

---

3. **Executar a aplicação**

   No terminal, rode:

   ```bash
   mvn spring-boot:run
   ```

   Se tudo estiver correto, você verá algo como:

   ```
   ...
   Started Application in X seconds
   ```

   ✅ O projeto está rodando na porta padrão `8080`.

---

4. **Testar requisições GET**

   Abra o navegador ou use o terminal:

   ```bash
   curl http://localhost:8080/estudantes
   ```

   Você verá uma lista (vazia ou com registros existentes).

---

5. **Cadastrar um estudante via curl**

   Use este comando:

   ```bash
   curl -X POST http://localhost:8080/estudantes \
   -H "Content-Type: application/json"      \
   -d '{
           "nomeCompleto": "José da Silva",
           "dataDeNascimento": "1990-02-15",
           "matricula": 45678
         }'
   ```

   ✅ A resposta será o JSON do documento salvo, incluindo o campo `id`.


7. **Listar os estudantes novamente**

   Depois de cadastrar, faça um `GET`:

   ```bash
   curl http://localhost:8080/estudantes
   ```

   Você verá todos os registros salvos no MongoDB.

---

✅ Parabéns! Com isso, seu projeto Java + Spring Boot + MongoDB está rodando com endpoints REST totalmente funcionais.

