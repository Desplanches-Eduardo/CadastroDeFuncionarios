# CadastroDeFuncionarios

![Java 21](https://img.shields.io/badge/Java-21-6366f1?style=flat-square&labelColor=1e293b&logo=openjdk&logoColor=white) ![Spring Boot 4.1.1](https://img.shields.io/badge/Spring_Boot-4.1.1-6366f1?style=flat-square&labelColor=1e293b&logo=springboot&logoColor=white) ![Flyway](https://img.shields.io/badge/Flyway-6366f1?style=flat-square&labelColor=1e293b&logo=flyway&logoColor=white) ![H2](https://img.shields.io/badge/H2-6366f1?style=flat-square&labelColor=1e293b&logo=h2database&logoColor=white) ![Swagger UI](https://img.shields.io/badge/Swagger_UI-6366f1?style=flat-square&labelColor=1e293b&logo=swagger&logoColor=white)

API REST com Spring Boot para cadastro de funcionários e de tarefas. Peguei o projeto de
[ninjas](https://github.com/Desplanches-Eduardo/cadastroDeNinjas) e refiz o mesmo recorte em
outro domínio, para praticar de novo modelagem, migração versionada, separação em camadas e
documentação das rotas — agora sem copiar as telas, porque o frontend vai ser escrito à parte.

São duas entidades, com CRUD de funcionário e de tarefa, o banco versionado com Flyway e a
documentação no Swagger. Compila, sobe e as rotas respondem. Falta o frontend e falta teste.

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
existir antes: cadastrar funcionário com uma tarefa que não existe responde `500`, de chave
estrangeira violada.

    curl -X POST http://localhost:8080/tarefas/criar -H 'Content-Type: application/json' \
      -d '{"nome":"Fechar o mes","dificuldade":"media"}'

    curl -X POST http://localhost:8080/funcionarios/criar -H 'Content-Type: application/json' \
      -d '{"nome":"Ana Souza","email":"ana@empresa.com","cargo":"Analista","idade":28,"tarefas":{"id":1}}'

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

Os `POST` respondem `201` com uma mensagem dizendo o nome e o id criado, e os `GET` por id
respondem `404` quando o id não existe. Os dois `DELETE` respondem `404` para um id inexistente,
e o de tarefa responde `409` quando ainda há funcionários apontando para ela.

## Banco e migrações

Duas entidades JPA: `FuncionariosModel` (tabela `tb_cadastro`, com nome, email, cargo, idade e
imagem na coluna `url_img`) e `TarefasModel` (tabela `tb_tarefas`, com nome e dificuldade). Um
funcionário aponta para uma tarefa (`@ManyToOne`, chave estrangeira `tarefa_id`) e uma tarefa
reúne vários funcionários (`@OneToMany`). O `@JsonIgnore` na lista de funcionários evita a
serialização circular entre as duas entidades.

O schema é versionado com Flyway, em `db/migrations`. A `V1` cria as duas tabelas.

Os dois `Repository` estendem `JpaRepository`, então o CRUD básico vem pronto do Spring Data. Os
`Service` chamam o repositório e convertem entre entidade e DTO pelo `Mapper`. Cada um dos dois
`POST` da API cria o recurso e devolve o nome e o id.

## O que falta

Frontend: não tem nenhum. As telas em Thymeleaf do projeto de ninjas ficaram de fora de
propósito, porque quero escrever o frontend separado — ainda não decidi se em React ou Angular —
e aprender a usar o Claude Code fazendo isso. A coluna `url_img` já existe no banco e no DTO
esperando o que vier dali.

Teste: fora o `contextLoads` que o Spring Initializr gera, não há nenhum. As rotas foram testadas
à mão, pelo `curl`.

Os dois `PUT` da API gravam a entidade inteira, então campo que não vai no corpo vira nulo — um
`PUT /funcionarios/alterar/{id}` com nome, email, cargo e idade devolve o `url_img` e a `tarefas`
nulos. E o corpo precisa trazer a idade, que é `int` no DTO: mandar um campo só responde `400`,
porque o Jackson do Spring Boot 4 recusa dto com primitivo ausente. Ou seja, os dois `PUT` só
aceitam o objeto completo.

Mandar um `id` no corpo do `POST /criar` cai no caminho de atualização do `save` e a resposta é
`500`, nas duas entidades. Id em criação não faz sentido, e o comportamento está igual dos dois
lados; vale fixar `id` em `null` nos dois `Service` antes de salvar, quando entrar o teste da
criação.

O `email` é `unique` no banco, então dois funcionários com o mesmo email não entram, mas o erro
chega como `500` na resposta, sem tratamento. Mesmo comportamento do projeto de ninjas.

## Decisões

Lombok nas entidades (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) para não escrever
getter e setter na mão.

O `ddl-auto` está em `validate`: quem cria e altera tabela é a migração, o Hibernate só confere
se as colunas mapeadas existem. Foi a troca pelo `update`, que nunca remove uma coluna nem
corrige o tipo de outra sozinho.

As anotações do Swagger ficam no controller, junto da rota que descrevem. Um arquivo OpenAPI
separado descreveria as mesmas rotas duas vezes e sairia de sincronia no primeiro método novo.

O `DELETE` de tarefa checa os funcionários vinculados antes de chamar o serviço. Sem essa
checagem a resposta era `500`, porque a chave estrangeira em `tb_cadastro` recusa a remoção e o
erro de integridade aparecia cru. Com a checagem sai `409`, que é o código para conflito com o
estado atual do recurso.

O nome da coluna da imagem é `url_img` no model e no DTO, e não `imgUrl` como no projeto de
ninjas. O campo veio assim e ficou, para o DTO continuar sendo o mesmo que o frontend vai
consumir depois.
