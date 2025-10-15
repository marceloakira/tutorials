# Configuração Maven para JPA/Hibernate

Este documento descreve a configuração do Maven para o projeto de Tutorial de Modelagem ORM com JPA/Hibernate.

## 📋 Estrutura do Projeto

```
modelagem-orm/
├── pom.xml                           # Configuração Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/ufg/inf/tutorial/
│   │   │       ├── Main.java         # Classe principal
│   │   │       └── util/
│   │   │           └── JPAUtil.java  # Utilitário JPA
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── persistence.xml   # Configuração JPA
│   │       └── logback.xml          # Configuração de logs
│   └── test/
│       └── java/
│           └── br/ufg/inf/tutorial/
│               └── JPAConfigurationTest.java
└── logs/                            # Logs da aplicação (criado automaticamente)
```

## 🔧 Principais Dependências

### JPA/Hibernate
- **hibernate-core**: Framework ORM principal
- **hibernate-entitymanager**: Implementação JPA
- **jakarta.persistence-api**: API padrão JPA 3.1

### Banco de Dados
- **h2**: Banco de dados em memória (ideal para testes e desenvolvimento)

### Logging
- **slf4j-api**: API de logging
- **logback-classic**: Implementação de logging

### Validação
- **hibernate-validator**: Bean Validation
- **jakarta.el**: Expression Language para validações

### Testes
- **junit-jupiter**: Framework de testes JUnit 5
- **assertj-core**: Assertions fluentes para testes

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Comandos Básicos

```bash
# Compilar o projeto
mvn compile

# Executar testes
mvn test

# Executar a aplicação
mvn exec:java

# Gerar JAR
mvn package

# Limpar build anterior
mvn clean
```

### Profiles Disponíveis

#### Profile de Desenvolvimento (padrão)
```bash
mvn exec:java -Pdev
```
- Banco H2 em memória
- Logs detalhados habilitados
- Schema criado automaticamente

#### Profile de Produção
```bash
mvn exec:java -Pprod
```
- Banco H2 em arquivo
- Logs otimizados
- Schema validado (não criado automaticamente)

## 📁 Configurações Importantes

### persistence.xml
- Define unidades de persistência para desenvolvimento e teste
- Configura driver H2 e propriedades do Hibernate
- Localizado em `src/main/resources/META-INF/`

### logback.xml
- Configuração de logs com appenders para console e arquivo
- Logs específicos para Hibernate e H2
- Localizado em `src/main/resources/`

### JPAUtil.java
- Classe utilitária para gerenciar EntityManager
- Implementa padrão Singleton para EntityManagerFactory
- Fornece métodos para operações transacionais

## 🔍 Verificação da Configuração

Execute os testes para verificar se tudo está funcionando:

```bash
mvn test
```

Os testes verificam:
- ✅ Inicialização do EntityManagerFactory
- ✅ Criação de EntityManager
- ✅ Execução de transações básicas
- ✅ Funcionamento do JPAUtil

## 📝 Próximos Passos

1. **Criar entidades JPA**: Implementar classes com anotações `@Entity`
2. **Implementar DAOs**: Criar classes de acesso a dados
3. **Adicionar relacionamentos**: Configurar `@OneToMany`, `@ManyToOne`, etc.
4. **Implementar herança**: Usar `@Inheritance` e estratégias de herança
5. **Criar testes de integração**: Testar operações CRUD completas

## 🐛 Troubleshooting

### Erro "No Persistence provider for EntityManager"
- Verifique se o `persistence.xml` está em `META-INF/`
- Confirme se as dependências do Hibernate estão no classpath

### Erro de conexão H2
- Verifique se a dependência h2 está incluída
- Confirme a URL de conexão no `persistence.xml`

### Logs não aparecem
- Verifique se o `logback.xml` está no classpath
- Confirme se as dependências de logging estão incluídas

## 📚 Recursos Adicionais

- [Documentação Hibernate](https://hibernate.org/orm/documentation/)
- [Especificação JPA](https://jakarta.ee/specifications/persistence/)
- [Banco H2 Documentation](http://www.h2database.com/html/main.html)
- [Maven Documentation](https://maven.apache.org/guides/)