# Bank as a service para transferência entre contas

O presente projeto visa disponibilizar REST APIs para realizar transferências entre contas bancárias. Ao fazer uma
transferência, valido se as contas existem e estão ativas, atualizo o saldo da conta de origem em outro microsserviço e
notifico o Bacen da transferência realizada.

## Tecnologias

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Data Redis
- Spring Validation
- Spring Kafka
- PostgreSQL
- Okhttp3
- Resilience4j
- Schema Registry
- Avro
- JUnit 5
- Lombok

## Setup

Para utilizar o projeto, realize os passos a seguir:

1. Clone o repositório que mocka os serviços terceiros e siga as orientações presentes no README do projeto;
2. Clone o presente repositório seguindo os comandos:
```bash
  git clone bla bla

  cd blabla
```
3. Depois de clonar este repositório, certifique-se de que está configurado e em uso na sua máquina a versão 21 do Java e rode o projeto com os seguintes comandos:
```bash
  docker-compose up -d

  ./gradlew bootRun
```
4. Uma vez que a aplicação está rodando, requisite o endpoint `http://localhost:8080/transferencia` com o body:
```bash
  {
    "idCliente": "2ceb26e9-7b5c-417e-bf75-ffaa66e3a76f",
    "valor": 500.00,
    "conta": {
        "idOrigem": "d0d32142-74b7-4aca-9c68-838aeacef96b",
        "idDestino": "41313d7b-bd75-4c75-9dea-1f4be434007f"
    }
  }
```
5. Você receberá um retorno similar a esse:
```bash
    {
      "id_transferencia": "07b327f0-eea9-49ce-b693-12f096396cbe"
    }
```

## Principais decisões
### Design Patterns
#### Strategy
Neste projeto, utilizei o padrão Strategy em razão das diferentes necessidades de transferência entre contas de pessoas
física e jurídica.

Embora os dados retornados pelo serviço externo de clientes sugira que essa aplicação realizaria transferências
apenas entre contas de pessoas físicas, não necessariamente as regras de validação, os dados a serem processados e os serviços
terceiros invocados sejam os mesmos para diferentes tipos de contas bancárias.

Pensando, assim, na implementação de transferência entre contas de pessoas jurídicas, entendo que cada tipo de transferência
poderia manter suas peculiaridades sem que a base de código ficasse duplicada e de difícil manutenibilidade.
Por exemplo, durante uma transferência entre pessoas júridicas, seria preciso verificar se o cliente que está executando
a transferência pertence ao Quadro Societário da empresa titular dessa conta. Esse tipo de validação não caberia a uma
conta de pessoa física e tornaria o código confuso e nada assertivo.

#### Builder
Além do Strategy, usei o padrão Builder na entidade `Transfer` a fim de facilitar a instanciação desse objeto, sem precisar
criar inúmeros construtores para atender cada caso de uso.

### Comunicação com serviços terceiros
Ao longo do fluxo criado para realizar uma transferência, dependemos de vários serviços externos a nossa aplicação.
Por conta disso, precisei criar um client com Okhttp3 para realizar as requisições e um interceptador para transformar
as exceções recebidas em exceções conhecidas do sistema.

Por disponibilidade de tempo, optei por mapear todas as exceções em uma exceção genérica chamada `ClientException`.
Apenas um caso foge a essa generalidade: exceções com status 429. Quando a exceção retornada tiver status 429, a transformo
em um `TooManyRequestClientException`. Fiz isso para demonstrar que o retry configurado com Resiliense4j não iria seguir
requisitando o serviço terceiro, comento sobre isso melhor abaixo. Nesse sentido, outros mapeamentos poderiam ser feitos,
como respostas com 404 se transformariam em uma exceção específica e o retry também iria parar de fazer retentativas,
uma vez que o recurso buscado não existe e não há o que retentar.

### Bacen indisponível e retorno da requisição
Outra importante decisão que tomei foi retentar comunicação com o Bacen quando este estivesse fora do ar e, caso a
exceção retornada fosse 429, nenhuma tentativa seria refeita, dado que o rate limit desse serviço teria sido atingido.
Para cenários como esse ou quando todas as retentativas foram realizadas sem sucesso, não deixo o cliente que requisitou
esperando por uma resposta. Simplesmente, retorno 200 e jogo o id dessa transferência em um tópico do Kafka que irá
tentar uma nova comunicação com o Bacen. Essa estratégia só é possível porque já realizei a comunicação com serviço de
saldo e obtive sucesso, ou seja, do nosso lado do banco, a transação foi realizada com sucesso e podemos comunicar com o
Bacen posteriormente.

### Resiliência
Pensando em resiliência com os serviços terceiros, decidi adotar três estratégias: retry com backoff exponencial, timelimiter
para derrubar comunicações demoradas e circuit breaker para impedir que novas requisições sejam feitas e retentadas
em serviços que demonstraram estar fora do ar.
Tais estratégias nos permite ser resilientes a falhas das dependências ao passo que não nos deixa esperando tempo demais
por uma resposta.

### Cache
Por fim, adotei o uso de cache para acelerar determinadas consultas a dados que foram obtidos e, caso nossa aplicação caia
e o cliente realize uma transferência em sequência, a aplicação não demorará a responder, pois os dados sobre o cliente recebedor
e a transferência criada - quando houver falha no Bacen - estarão disponíveis por 30 minutos no Redis. Isso deixa a aplicação
stateless e não sobrecarrega serviços externos e nem o banco de dados.
