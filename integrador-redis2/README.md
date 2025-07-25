# Integrador Redis II: Sincronização de Dados entre Sistemas de Persistência - Transformação de Modelos com Apache Camel

## 1. Introdução

Este documento é parte de uma série de tutoriais sobre integração de sistemas utilizando Redis como canal de mensagens. Este tutorial é a continuação do Integrador Redis I, onde abordamos a arquitetura de integração e um exemplo básico de sincronização de dados. No Integrador Redis II, expandimos essa funcionalidade para incluir a transformação de modelos de dados utilizando o Apache Camel.

Relembrando a arquitetura do Integrador Redis I:

![Arquitetura Integrador Redis I](../integrador-redis/diagrama-componentes.png)

Figura 1: Arquitetura do Integrador Redis I

* **Redis Listener**: Um componente que escuta eventos de inserção, atualização e exclusão de dados no Redis.
* **Transformador de Modelos**: Um componente responsável por transformar os dados recebidos do Redis em um formato compatível com o sistema de persistência de destino (ORM/SQLite e ODM/MongoDB).

### 1.1. Problemas

A transformação de dados é um aspecto crítico em sistemas de integração, especialmente quando diferentes sistemas de persistência utilizam modelos de dados distintos. No Integrador Redis II, enfrentamos os seguintes problemas:

1. **Incompatibilidade de Modelos**: Os dados provenientes de diferentes fontes podem ter estruturas diferentes, o que dificulta a integração.
2. **Complexidade na Transformação**: A lógica de transformação pode ser complexa, exigindo um mapeamento cuidadoso entre os modelos de origem e destino.
3. **Desempenho**: A transformação de dados em tempo real pode impactar o desempenho do sistema, especialmente com grandes volumes de dados.

Para facilitar a transformação de modelos, há diversas abordagens e ferramentas disponíveis. Neste tutorial, utilizaremos o Apache Camel, uma ferramenta de integração que facilita a implementação de diversos padrões de integração, denominados como Enterprise Integration Patterns (EIPs).

## 1.2. Outros problemas

Outros problemas de integração que não serão tratados neste tutorial, mas que são importantes considerar em um sistema de integração completo, incluem:

1. **Manutenção**: A lógica de transformação deve ser facilmente mantida e atualizada conforme os modelos evoluem.
2. **Sincronização de Dados**: Garantir que os dados transformados sejam sincronizados corretamente entre os sistemas de persistência. Ex.
   - Inserção de dados no Redis deve refletir na base de dados ORM/SQLite e ODM/MongoDB.
   - Se um dos sistemas de persistência falhar, o sistema deve ser capaz de lidar com a inconsistência de dados. Exemplos:
     - Se o Redis falhar, os dados devem ser persistidos no Redis quando ele voltar.
     - Se o ORM/SQLite falhar, os dados devem ser persistidos no Redis e sincronizados quando o ORM/SQLite voltar.
     - Se o ODM/MongoDB falhar, os dados devem ser persistidos no Redis e sincronizados quando o ODM/MongoDB voltar.
   - Se algum dado for alterado manualmente em um dos sistemas de persistência, o integrador deve ser capaz de detectar e sincronizar essas alterações. Exemplos:
     - Se um dado for alterado no ORM/SQLite, o integrador deve atualizar o Redis e o ODM/MongoDB.
     - Se um dado for alterado no ODM/MongoDB, o integrador deve atualizar o Redis e o ORM/SQLite.

Os problemas de manutenção e sincronização de dados são complexos e exigem uma abordagem cuidadosa para garantir a integridade e a consistência dos dados em todo o sistema. Neste tutorial, focaremos na transformação de modelos, mas é importante ter em mente esses outros aspectos ao projetar um sistema de integração robusto.

## 2. Padrões de Projeto de Integração

Problemas de integração de dados são recorrentes de Engenharia de Software e a comunidade desenvolvedora com frequência propõe soluções para esses problemas através de padrões de projeto. Há diversas publicações na forma de livros, artigos e blogs que abordam esses padrões. Um dos mais conhecidos é o livro ["Enterprise Integration Patterns" de Gregor Hohpe e Bobby Woolf](#hohpe2004), que descreve uma série de padrões para resolver problemas comuns de integração.

Gregor Hohpe mantém um portal com uma lista de padrões de integração, que pode ser acessado em [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/). Ele definiu [65 padrões de integração](https://www.enterpriseintegrationpatterns.com/patterns/messaging/), que são divididos em categorias como:
* Os **Padrões de Canal** descrevem como as mensagens são transportadas através de um Canal de Mensagens.
* Os **Padrões de Construção de Mensagens** descrevem a intenção, a forma e o conteúdo das mensagens que trafegam pelo sistema de mensagens.
* Os **Padrões de Roteamento** discutem como as mensagens são direcionadas de um remetente para o(s) destinatário(s) correto(s).
* Os **Padrões de Transformação** alteram o conteúdo de uma mensagem, por exemplo, para acomodar diferentes formatos de dados usados pelos sistemas de envio e recebimento. Dados podem precisar ser adicionados, removidos ou os dados existentes podem precisar ser reorganizados. O padrão base para esta seção é o Tradutor de Mensagens.
* Os **Padrões de Endpoint** descrevem como as aplicações produzem ou consomem mensagens.
* Os **Padrões de Gerenciamento de Sistemas** descrevem o que é necessário para manter um sistema complexo baseado em mensagens funcionando de forma robusta.

Padrões de Integração são uma forma de documentar soluções para problemas comuns de integração, permitindo que desenvolvedores e arquitetos de software reutilizem soluções comprovadas em seus projetos. A seguir, apresentamos os padrões de integração utilizados neste tutorial e os que serão implementados.

## 2.1. Padrões de Integração Utilizados

Há diversos livros e artigos que abordam e catalogam padrões de integração. Um dos mais conhecidos é o livro ["Enterprise Integration Patterns" de Gregor Hohpe e Bobby Woolf](#hohpe2004), que descreve uma série de padrões para resolver problemas comuns de integração. Abaixo, apresentamos uma imagem que ilustra a linguagem de padrões de integração proposta por Hohpe e Woolf:

![integration pattern language](integration-pattern-language.png)
Figura 2: Linguagem de Padrões de Integração - Fonte: [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)

No estudo de caso desta série de tutoriais, diversos padrões de integração são utilizados, incluindo:
* **[Mensagem de Evento (Event Message)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/EventMessage.html)**: Representa uma notificação de que algo aconteceu em um sistema, como a inserção, atualização ou exclusão de dados.
* **[Canal Publicador-Assinante (Publisher-Subscriber Channel)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/PublishSubscribeChannel.html)**: Um canal de mensagens onde os remetentes publicam mensagens e os destinatários se inscrevem para receber essas mensagens.
* **[Consumidor Orientado a Evento (Event-Driven Consumer)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)**: Um consumidor que reage a eventos recebidos através de um canal de mensagens.

## 2.2. Padrões de Integração a serem implementados

Além dos padrões de integração relacionados ao canal e construção de mensagem, há outros padrões descritos por Hohpe e Woolf para roteamento e transformação de mensagens. Os padrões de integração que serão utilizados neste tutorial incluem:
* **[Roteador de Mensagens (Message Router)](https://camel.apache.org/manual/latest/message-router.html)**: Um componente que direciona mensagens para diferentes destinos com base em regras definidas.
* **[Transformador de Mensagens (Message Translator)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)**: Um componente que transforma o conteúdo de uma mensagem de um formato para outro, permitindo a compatibilidade entre diferentes sistemas de persistência.
* **[Modelo de Dados Canônico (Canonical Data Model)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/CanonicalDataModel.html)**: Um modelo de dados comum que serve como intermediário entre diferentes sistemas, evitando o acoplamento de modelos entre si.

Os três padrões de integração mencionados acima serão utilizados e implementados neste estudo de caso. O Roteador de Mensagens permite direcionar as mensagens para o transformador correto, enquanto o Transformador de Mensagens realiza a transformação propriamente dita. O Modelo de Dados Canônico serve como um intermediário entre os diferentes sistemas, garantindo que as mensagens sejam compatíveis entre si.

# 3. Modelagem do Estudo de Caso

## 3.1. Diagrama de Classes

Considerando que o objetivo do integrador é transformar modelos de dados entre diferentes sistemas de persistência, são necessários dois diagramas de classes: um para o modelo baseado em ORM/SQLite e outro para o modelo baseado em ODM/MongoDB.

Como estudo de caso, considere dois sistemas com camadas de persistência distintas:

* **Sistema de Gestão Acadêmica (SGA)**: responsável pela gestão dos processos acadêmicos de uma universidade. Entre suas principais entidades estão: *Estudante*, *Disciplina*, *Turma* e *Matrícula*.

* **Sistema de Biblioteca (SB)**: responsável pela gestão dos empréstimos de livros aos estudantes. Suas principais entidades incluem: *Estudante*, *Livro* e *Empréstimo*.

Para fins de integração, a entidade *Estudante* deve ser compartilhada entre os dois sistemas. No SB, o estudante deve possuir um *status de matrícula* que determina se ele está autorizado a realizar empréstimos. Já no SGA, o estudante deve possuir um atributo que indica o *status de empréstimo de livros* (por exemplo, `QUITADO` ou `EM_ABERTO`), o qual impacta diretamente na autorização para emissão do diploma.

Abaixo estão os diagramas de classes para cada sistema:

### Modelo SGA: ORM/SQLite

![Modelo ORM/SQLite](modelo_orm_sqlite.png)

Figura 3: Diagrama de Classes do Modelo SGA, implementado com ORM/SQLite

O código-fonte do diagrama de classes do modelo ORM/SQLite pode ser encontrado em [modelo_orm_sqlite.puml](modelo_orm_sqlite.puml).

* **Estudante**: Representa um aluno da instituição. Possui um relacionamento "1:N" com a entidade *Matricula*, ou seja, um estudante pode se matricular em várias turmas. Também possui um atributo *statusEmprestimoLivros*, que indica a situação do estudante quanto a pendências com a biblioteca (ex: `QUITADO`, `EM_ABERTO`).
* **Disciplina**: Representa um componente curricular. Possui um relacionamento "1:N" com a entidade *Turma*, indicando que uma disciplina pode ser oferecida em várias turmas.
* **Turma**: Representa uma oferta específica de uma disciplina. Possui um relacionamento "N:1" com *Disciplina* e "1:N" com *Matricula*, ou seja, cada turma pertence a uma disciplina e pode ter várias matrículas.
* **Matricula**: Representa o vínculo entre um estudante e uma turma. Possui relacionamentos "N:1" com *Estudante* e *Turma*, e "1:1" com *StatusMatricula*. Ou seja, a matrícula estabelece uma relação N:M entre estudantes e turmas, com um status associado a cada vínculo.
* **StatusMatricula**: Enumeração que indica o estado de uma matrícula (ex: `ATIVA`, `TRANCADA`, `CANCELADA`). Cada matrícula possui exatamente um status associado.
* **StatusEmprestimo**: Enumeração que indica a situação dos empréstimos de livros do estudante, usada para fins administrativos (ex: impedir emissão de diploma). Os valores possíveis são `QUITADO` e `EM_ABERTO`.


### Modelo SB: ODM/MongoDB

![Modelo ODM/MongoDB](modelo_odm_mongodb.png)

Figura 4: Diagrama de Classes do Modelo SB, implementado com ODM/MongoDB

O código-fonte do diagrama de classes do modelo do Sistemas de Bibliotecas (ODM/MongoDB) pode ser encontrado em [modelo_odm_mongodb.puml](modelo_odm_mongodb.puml).

* **Usuario**: Representa um estudante cadastrado no sistema de biblioteca. O campo `id` é uma `String` que armazena o valor do `ObjectId` do MongoDB (formato hash), usado como identificador único do documento. O nome completo do usuário é representado de forma separada pelos campos `prenome` e `sobrenome`, permitindo maior flexibilidade para ordenação, buscas e formatação. O campo `situacaoMatricula` indica a situação acadêmica do usuário (por exemplo: ATIVO, INATIVO), utilizada para autorizar ou restringir empréstimos.

* **RegistroEmprestimo**: Representa o empréstimo de uma obra a um usuário. Armazena o código do empréstimo e as datas de início e previsão de devolução. Cada registro de empréstimo está associado a um único usuário e a uma única obra.

* **Obra**: Representa um livro ou material disponível para empréstimo. Contém dados bibliográficos como o código, título principal, autor principal e número ISBN. Uma mesma obra pode ser associada a vários registros de empréstimo ao longo do tempo.


## 3.2. Modelo de Dados Canônico

Para facilitar a transformação entre os modelos ORM/SQLite e ODM/MongoDB, é necessário definir um Modelo de Dados Canônico (MDC) que sirva como intermediário. O MDC deve conter os atributos comuns entre os dois modelos, permitindo que as transformações sejam realizadas de forma eficiente.

O MDC é um padrão de integração que minimiza as dependências entre os sistemas de persistência, evitando-se que diversos sistemas dependam diretamente uns dos outros. Em vez disso, todos os sistemas dependem do MDC, que atua como um intermediário entre eles. A figura abaixo ilustra o MDC proposto por [Hohpe e Woolf (2004)](#hohpe2004):

![Modelo de Dados Canônico](CanonicalDataModel.gif)

Figura 5: Modelo de Dados Canônico - Fonte: [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/patterns/messaging/CanonicalDataModel.html)

Na figura acima, os sistemas A, B e C e D dependem do MDC, mas cada um não dependem diretamente dos outros. Isso permite que os sistemas sejam desacoplados, facilitando a manutenção e evolução dos modelos de dados.

### 🧩 Tabela Comparativa: `Estudante` (SGA) × `Usuario` (SB)

| Atributo em `Estudante` (SGA)     | Atributo em `Usuario` (SB)     | Equivalência Semântica | Observações                                                                 |
|----------------------------------|--------------------------------|--------------------------|------------------------------------------------------------------------------|
| `id` (int)                       | `id` (String)                  | ✅ Sim                   | Ambos representam identificadores únicos; no SB, é um hash (ObjectId) em String |
| `nomeCompleto` (String)          | `prenome` + `sobrenome`        | ✅ Sim                   | `nomeCompleto` pode ser reconstruído a partir da concatenação dos dois campos |
| `dataDeNascimento` (String)     | *(ausente)*                    | ⚠️ Parcial               | Presente apenas no SGA                                                       |
| `matricula` (int)                | *(ausente)*                    | ⚠️ Parcial               | Pode ser derivada ou ignorada, dependendo das regras do integrador           |
| `statusEmprestimoLivros` (enum) | *(ausente)*                    | ❌ Não                   | Campo específico do SGA usado para controle de pendências com a biblioteca   |
| *(ausente)*                      | `situacaoMatricula` (String)   | ❌ Não                   | Campo específico do SB usado para liberar ou bloquear empréstimos            |

### Identificador Canônico e Entidade de Mapeamento

Para que o Modelo de Dados Canônico (MDC) cumpra seu papel de forma eficaz, é fundamental que cada entidade integrada entre os sistemas possua um **identificador universal**, denominado `idCanonico`. Esse identificador atua como chave primária no domínio canônico e deve ser **independente dos identificadores locais** usados por cada sistema de origem ou destino.

A adoção de um `idCanonico` baseado em **[UUID (Universally Unique Identifier)](https://pt.wikipedia.org/wiki/UUID)** traz benefícios importantes: promove o **desacoplamento entre os sistemas**, assegura **unicidade global** e permite que o integrador opere de forma neutra em relação às tecnologias de persistência utilizadas. Enquanto isso, os sistemas originais — como o SGA (com `id` inteiro) e o SB (com `id` em formato de hash) — continuam utilizando seus próprios identificadores.

Para permitir a correspondência entre esses identificadores locais e o canônico, define-se uma **entidade de mapeamento de IDs**, chamada `MapeamentoID`. Essa entidade relaciona o `idCanonico` aos identificadores específicos de cada sistema (`idSGA`, `idSB`) e registra o momento da última sincronização. Ela permite que o integrador **localize, atualize e reconcilie entidades** de forma segura e rastreável. Um benefício central dessa abordagem é o suporte à **[idempotência](https://www.enterpriseintegrationpatterns.com/patterns/messaging/IdempotentReceiver.html)**, ou seja, a garantia de que operações repetidas não causarão efeitos duplicados — característica essencial em cenários com reprocessamentos, mensagens duplicadas ou falhas temporárias.

Além disso, o uso do `idCanonico` junto à entidade `MapeamentoID` torna o modelo naturalmente **extensível**: caso um novo sistema venha a ser integrado, basta adicionar um novo campo de identificador ao mapeamento e os atributos relevantes ao MDC, sem necessidade de alterar os sistemas existentes. Isso torna a arquitetura preparada para evoluir de forma sustentável e desacoplada.

### Modelo de Dados Canônico Proposto

Com base na análise comparativa entre os modelos de dados dos sistemas SGA e SB, propõe-se um Modelo de Dados Canônico (MDC) que representa uma abstração comum da entidade *Estudante*. O MDC unifica os atributos compartilhados, normaliza diferenças estruturais e acomoda informações específicas de cada sistema, permitindo uma transformação eficiente e desacoplada.

A estrutura proposta inclui atributos como `idCanonico`, que é um identificador universal baseado em UUID, e os campos `prenome` e `sobrenome`, derivados da separação do `nomeCompleto` utilizado no SGA. Atributos relevantes para ambos os domínios, como `statusBiblioteca` (proveniente do SGA) e `statusAcademico` (presente no SB), também são incorporados ao modelo, assegurando uma visão unificada do estudante em diferentes contextos.

Além disso, para viabilizar a correspondência entre os identificadores locais dos sistemas e o identificador canônico, define-se uma entidade auxiliar denominada `MapeamentoID`. Essa entidade registra os vínculos entre `idCanonico`, `idSGA` e `idSB`, além de armazenar a data da última sincronização. Esse componente é essencial para garantir rastreabilidade, consistência e idempotência na integração entre os sistemas.

A seguir, apresenta-se o diagrama de classes do modelo canônico proposto, incluindo a entidade de mapeamento de IDs:

![Modelo de Dados Canônico](modelo_de_dados_canonico.png)

Figura 6: Diagrama de Classes do Modelo de Dados Canônico, código-fonte disponível em [modelo_de_dados_canonico.puml](modelo_de_dados_canonico.puml).

# 4. Implementação

## 4.1. Adaptação dos Sistemas SGA e SB

### SGA publicando eventos CRUD no Redis

### SB publicando eventos CRUD no Redis

## Verificando a Publicação de Eventos no Redis

## 4.2. Implementação dos Transformadores de Modelos

## Implementação do Redis Listener

## Implementação do Transformador ODM -> ORM

## Implementação do Transformador ORM -> ODM

## 4.3. Testes de Integração

# Referências

1. <a id="hohpe2004"></a>HOHPE, Gregor; WOOLF, Bobby. Enterprise integration patterns: Designing, building, and deploying messaging solutions. [S.l.]: Addison-Wesley Professional, 2004. 
