# CadastroDeFuncionarios

![Java 21](https://img.shields.io/badge/Java-21-6366f1?style=flat-square&labelColor=1e293b&logo=openjdk&logoColor=white) ![Spring Boot 4.1.1](https://img.shields.io/badge/Spring_Boot-4.1.1-6366f1?style=flat-square&labelColor=1e293b&logo=springboot&logoColor=white) ![Flyway](https://img.shields.io/badge/Flyway-6366f1?style=flat-square&labelColor=1e293b&logo=flyway&logoColor=white) ![H2](https://img.shields.io/badge/H2-6366f1?style=flat-square&labelColor=1e293b&logo=h2database&logoColor=white) ![Swagger UI](https://img.shields.io/badge/Swagger_UI-6366f1?style=flat-square&labelColor=1e293b&logo=swagger&logoColor=white)

API REST com Spring Boot para cadastro de funcionários e de tarefas. Peguei o projeto de
[ninjas](https://github.com/Desplanches-Eduardo/cadastroDeNinjas) e refiz o mesmo recorte em
outro domínio, para praticar de novo modelagem, migração versionada, separação em camadas e
documentação das rotas — agora sem copiar as telas, porque o frontend ia ser escrito à parte.

São duas entidades, com CRUD de funcionário e de tarefa, o banco versionado com Flyway e a
documentação no Swagger. Os DTOs são records, as regras de negócio ficam no service e os erros
viram `404` e `409` num handler global. Compila, sobe e as rotas respondem. O frontend saiu
depois, em outro repositório. Aqui falta teste e validação de entrada.

## Como rodar

O `application.properties` lê a configuração do banco de variáveis de ambiente, sem valor
padrão. Sem elas o app não sobe, e o erro é `Driver org.h2.Driver claims to not accept jdbcUrl,
${DATABASE_URL}`. Com um H2 em memória:

    export DATABASE_URL='jdbc:h2:mem:testdb'
    export DATABASE_USERNAME=sa
    export DATABASE_PASSWORD=''
    ./mvnw spring-boot:run

Sobe na porta 8080 e o console do H2 fica em `http://localhost:8080/h2-console`.

O banco nasce vazio: a migração cria as tabelas, não os funcionários. Para ter dado, os `POST`
da API ou o console do H2. Como a tabela de funcionário aponta para a de tarefa, a tarefa precisa
existir antes: cadastrar funcionário com um `tarefasId` que não existe responde `404`.

    curl -X POST http://localhost:8080/tarefas/criar -H 'Content-Type: application/json' \
      -d '{"nome":"Fechar o mes","dificuldade":"media"}'

    curl -X POST http://localhost:8080/funcionarios/criar -H 'Content-Type: application/json' \
      -d '{"nome":"Ana Souza","email":"ana@empresa.com","cargo":"Analista","idade":28,"tarefasId":1}'

Para rodar o teste: `./mvnw test`, com as mesmas variáveis de ambiente exportadas.

## Documentação das rotas

O springdoc-openapi lê as anotações do controller e monta a documentação sozinho, sem arquivo à
parte para manter em sincronia. Com o app rodando:

- `http://localhost:8080/swagger-ui/index.html` é a interface, com as rotas agrupadas e o botão
  `Try it out` para disparar a requisição dali mesmo.
- `http://localhost:8080/v3/api-docs` é o JSON no formato OpenAPI, que é a fonte da interface.

Cada rota tem `@Operation` com o resumo e a explicação, `@ApiResponses` com os códigos esperados
e `@Parameter` nos parâmetros de caminho e de corpo.

## Endpoints

As duas entidades têm as mesmas cinco rotas, com os mesmos nomes: `POST /criar` cadastra,
`GET /listar` lista tudo, `GET /listar/{id}` busca por id, `PUT /alterar/{id}` altera e
`DELETE /deletar/{id}` remove. Em funcionário elas ficam sob `/funcionarios` e em tarefa sob
`/tarefas`.

| Método | Rota | O que faz |
| --- | --- | --- |
| POST | `/funcionarios/criar` | Cadastra um funcionário a partir do corpo JSON |
| GET | `/funcionarios/listar` | Lista todos os funcionários |
| GET | `/funcionarios/listar/{id}` | Busca um funcionário pelo id |
| PUT | `/funcionarios/alterar/{id}` | Altera um funcionário |
| DELETE | `/funcionarios/deletar/{id}` | Remove um funcionário |
| POST | `/tarefas/criar` | Cadastra uma tarefa |
| GET | `/tarefas/listar` | Lista todas as tarefas |
| GET | `/tarefas/listar/{id}` | Busca uma tarefa pelo id |
| PUT | `/tarefas/alterar/{id}` | Altera uma tarefa |
| DELETE | `/tarefas/deletar/{id}` | Remove uma tarefa |

Os `POST` respondem `201` com o objeto criado, os `GET` e o `PUT` respondem `200` com o objeto
e os `DELETE` respondem `204` sem corpo. O corpo de funcionário é este:

    {"id": 1, "nome": "Ana Souza", "email": "ana@empresa.com", "urlImg": null,
     "cargo": "Analista", "idade": 28, "tarefasId": 1}

e o de tarefa é `{"id": 1, "nome": "Fechar o mes", "dificuldade": "media"}`. O `id` mandado num
`POST` é ignorado, porque quem gera é o banco.

| Status | Quando |
| --- | --- |
| `404` | Funcionário ou tarefa com id que não existe, inclusive o `tarefasId` no corpo |
| `409` | Email já cadastrado em outro funcionário, ou `DELETE` de tarefa com funcionários vinculados |
| `400` | JSON malformado ou sem a `idade` |

O `404` e o `409` respondem com uma mensagem em texto dizendo o motivo.

## Banco e migrações

Duas entidades JPA: `FuncionariosModel` (tabela `tb_cadastro`, com nome, email, cargo, idade e
imagem na coluna `url_img`) e `TarefasModel` (tabela `tb_tarefas`, com nome e dificuldade). Um
funcionário aponta para uma tarefa (`@ManyToOne`, chave estrangeira `tarefa_id`) e uma tarefa
reúne vários funcionários (`@OneToMany`). O `@JsonIgnore` na lista de funcionários evita a
serialização circular entre as duas entidades.

O schema é versionado com Flyway, em `db/migrations`. A `V1` cria as duas tabelas.

Os dois `Repository` estendem `JpaRepository`, então o CRUD básico vem pronto do Spring Data,
e as consultas de regra (`existsByTarefasId`, `existsByEmail`, `existsByEmailAndIdNot`) são
derivadas do nome do método.

## Camadas

- **DTO**: `record` imutável, com `fromEntity` e `toEntity` estáticos para converter. O DTO de
  funcionário leva só o `tarefasId`, não a entidade da tarefa, e o de tarefa não leva a lista de
  funcionários.
- **Service**: busca no banco, aplica as regras e lança exceção quando algo não pode. É ele que
  transforma o `tarefasId` na tarefa, que confere email repetido e que barra o `DELETE` de tarefa
  com funcionários. O `PUT` altera a entidade que já existe, dentro de `@Transactional`, e o JPA
  grava a mudança sozinho no fim do método.
- **Controller**: recebe, chama o service e devolve. Não tem `if` decidindo status.
- **`common`**: `RecursoNaoEncontradoException` e `RegraDeNegocioException`, e o
  `GlobalExceptionHandler` (`@RestControllerAdvice`) que transforma as duas em `404` e `409`.

## Frontend

Ficou pronto, em outro repositório:
[cadastro-funcionarios-web](https://github.com/Desplanches-Eduardo/cadastro-funcionarios-web).
React com Vite e TypeScript, um quadro de turno onde cada tarefa é uma coluna e cada funcionário
é uma ficha presa nela, com arraste entre colunas e gaveta lateral para cadastrar e editar.
Escrevi com o Claude Code, em fases que revisei, e as particularidades da API são tratadas do
lado de lá: o Vite faz proxy para a porta 8080 (a API não libera CORS), o cliente converte as
respostas — o `POST` responde texto, o `PUT` grava o objeto inteiro, o `500` de email repetido e
o `409` de tarefa com gente viram mensagem na tela. O backend não mudou nada por causa dele.

A refatoração para records mudou o contrato que o frontend lê: `url_img` virou `urlImg`, o
objeto `tarefas` virou `tarefasId`, os `POST` devolvem JSON no lugar de texto, os `DELETE`
devolvem `204` sem corpo e email repetido é `409`. O frontend precisa acompanhar essas mudanças.

As telas em Thymeleaf do projeto de ninjas ficaram de fora de propósito, e a coluna `url_img` já
existia no banco e no model esperando a foto que a ficha mostra.

## O que falta

Teste: fora o `contextLoads` que o Spring Initializr gera, não há nenhum. As rotas foram testadas
à mão, pelo `curl`.

Validação de entrada: hoje entra funcionário com nome vazio, email `abc` ou idade negativa. O
próximo passo é `spring-boot-starter-validation`, com `@NotBlank`, `@Email` e `@Positive` no
record, `@Valid` no controller e um `400` com os campos errados.

Os dois `PUT` substituem todos os campos, como manda o `PUT`: um
`PUT /funcionarios/alterar/{id}` sem `urlImg` e sem `tarefasId` grava os dois nulos. E o corpo
precisa trazer a idade, que é `int` no DTO, senão responde `400`. Se um dia precisar mandar só
um campo, o caminho é um `PATCH`.

## Decisões

Lombok nas entidades (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`) para
não escrever getter e setter na mão. Era `@Data`, mas o `equals` e o `hashCode` que ele gera
passam pelo relacionamento, e funcionário chamava tarefa que chamava funcionário sem parar.

DTO como `record` com conversão estática, sem classe de mapper. O record já é imutável e tem
construtor, acessores, `equals` e `toString` prontos, e com dois DTOs pequenos uma classe só para
converter era camada a mais. A tarefa do funcionário não é convertida no record porque precisa
ser buscada no banco: quem faz isso é o service, que passa a tarefa pronta para o `toEntity`.

Exceção não checada (`RuntimeException`) para os erros de negócio. Ela atravessa o controller
sem `throws` em cada método e chega no handler, e o `@Transactional` só faz rollback sozinho
com exceção não checada.

O `ddl-auto` está em `validate`: quem cria e altera tabela é a migração, o Hibernate só confere
se as colunas mapeadas existem. Foi a troca pelo `update`, que nunca remove uma coluna nem
corrige o tipo de outra sozinho.

As anotações do Swagger ficam no controller, junto da rota que descrevem. Um arquivo OpenAPI
separado descreveria as mesmas rotas duas vezes e sairia de sincronia no primeiro método novo.

Regra de negócio no service, não no controller nem só no banco. O `DELETE` de tarefa com
funcionários e o email repetido já eram barrados pelo banco (chave estrangeira e `unique`), mas
o erro chegava como `500`. O service confere antes e lança `RegraDeNegocioException`, que vira
`409`, o código para conflito com o estado atual do recurso. O banco continua como última
barreira.

O campo da imagem é `urlImg` em Java e no JSON, seguindo o camelCase, e a coluna no banco
continua `url_img`, pelo `@Column`. Assim não precisou de migração nova.
