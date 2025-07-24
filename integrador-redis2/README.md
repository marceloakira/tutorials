# Integrador Redis II: Sincronização de Dados entre Sistemas de Persistência - Transformação de Modelos com Apache Camel

## 1. Introdução

Este documento é parte de uma série de tutoriais sobre integração de sistemas utilizando Redis como canal de mensagens. Este tutorial é a continuação do Integrador Redis I, onde abordamos a arquitetura de integração e um exemplo básico de sincronização de dados. No Integrador Redis II, expandimos essa funcionalidade para incluir a transformação de modelos de dados utilizando o Apache Camel.

Relembrando a arquitetura do Integrador Redis I:
![Arquitetura Integrador Redis I](../integrador-redis/diagrama-componentes.png)

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
Figura 1: Linguagem de Padrões de Integração - Fonte: [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)

No estudo de caso desta série de tutoriais, diversos padrões de integração são utilizados, incluindo:
* **[Mensagem de Evento (Event Message)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/EventMessage.html)**: Representa uma notificação de que algo aconteceu em um sistema, como a inserção, atualização ou exclusão de dados.
* **[Canal Publicador-Assinante (Publisher-Subscriber Channel)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/PublishSubscribeChannel.html)**: Um canal de mensagens onde os remetentes publicam mensagens e os destinatários se inscrevem para receber essas mensagens.
* **[Consumidor Orientado a Evento (Event-Driven Consumer)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)**: Um consumidor que reage a eventos recebidos através de um canal de mensagens.

## 2.2. Padrões de Integração a serem implementados

Neste tutorial, utilizaremos o Apache Camel para implementar a transformação de modelos de dados. O Apache Camel é uma plataforma de integração que fornece uma ampla gama de componentes e padrões de integração, facilitando a implementação de soluções complexas de integração. Os padrões de integração do Apache Camel são baseados nos padrões descritos por Hohpe e Woolf, permitindo que os desenvolvedores implementem soluções de integração de forma eficiente e escalável. Os padrões de integração que serão utilizados neste tutorial incluem:
* **[Roteador de Mensagens (Message Router)](https://camel.apache.org/manual/latest/message-router.html)**: Um componente que direciona mensagens para diferentes destinos com base em regras definidas.
* **[Transformador de Mensagens (Message Translator)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)**: Um componente que transforma o conteúdo de uma mensagem de um formato para outro, permitindo a compatibilidade entre diferentes sistemas de persistência.
* **[Modelo de Dados Canônico (Canonical Data Model)](https://www.enterpriseintegrationpatterns.com/patterns/messaging/CanonicalDataModel.html)**: Um modelo de dados comum que serve como intermediário entre diferentes sistemas, evitando o acoplamento de modelos entre si.

Os três padrões de integração mencionados acima serão utilizados e implementados neste estudo de caso. O Roteador de Mensagens permite direcionar as mensagens para o transformador correto, enquanto o Transformador de Mensagens realiza a transformação propriamente dita. O Modelo de Dados Canônico serve como um intermediário entre os diferentes sistemas, garantindo que as mensagens sejam compatíveis entre si.



# Referências

1. <a id="hohpe2004"></a>HOHPE, Gregor; WOOLF, Bobby. Enterprise integration patterns: Designing, building, and deploying messaging solutions. [S.l.]: Addison-Wesley Professional, 2004. 
