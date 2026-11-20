# Raízes do Nordeste — API

API REST desenvolvida para o projeto multidisciplinar **Raízes do Nordeste**. A aplicação permite realizar o cadastro e a autenticação de usuários, criar e consultar pedidos, simular o processamento de pagamentos e atualizar o status dos pedidos.

O fluxo principal implementado é:

**Criação do pedido → validação dos dados e itens → pagamento simulado → atualização do status do pedido.**

> **Aplicação:** o pagamento é simulado. Funcionalidades de controle operacional de estoque, fidelidade e auditoria de negócio permanecem fora do escopo implementado nesta versão.

## 1. Tecnologias utilizadas

- Java 21
- Spring Boot 4.0.8
- Maven
- Spring Data JPA / Hibernate
- Spring Security
- JWT (JJWT 0.13.0)
- MySQL 8.0.40
- Flyway (dependência incluída no projeto)
- Springdoc OpenAPI / Swagger UI 3.0.3

## 2. Pré-requisitos

Antes de executar o projeto, instale:

- JDK 21;
- Apache Maven 3.6.3 ou superior;
- MySQL Server 8.0.40;
- Git.

O Postman é necessário para reproduzir a coleção de requisições fornecida na pasta `postman`.


## 3. Clonar o repositório

```bash
git clone https://github.com/iGuiti/raizes-do-nordeste.git
cd <PASTA_DO_REPOSITORIO>
```

## 4. Preparar o banco de dados

1. Inicie o serviço do MySQL.
2. Crie ou disponibilize um usuário MySQL com permissão para acessar o banco `raizes_do_nordeste`.
3. Crie o banco de dados vazio com o comando abaixo, executando-o no MySQL Workbench ou em um cliente MySQL:

```sql
CREATE DATABASE IF NOT EXISTS raizes_do_nordeste
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
```

A estrutura das tabelas é gerenciada pelo Flyway. Ao iniciar a aplicação pela primeira vez com o banco vazio e as variáveis de ambiente configuradas (seção 5), o Flyway executa automaticamente a migration `src/main/resources/db/migration/V1__create_initial_schema.sql`, responsável pela criação das 13 tabelas do projeto. O Flyway também mantém a tabela `flyway_schema_history` para registrar as migrations executadas.

Depois que a aplicação iniciar e a migration for concluída com sucesso, execute uma única vez o script `database/testes.sql` para inserir os dados iniciais de referência utilizados no fluxo de pedidos, como unidade, produto e vínculo com o cardápio.

A aplicação utiliza `spring.jpa.hibernate.ddl-auto=none`; portanto, o Hibernate não cria nem altera as tabelas. A criação e o versionamento da estrutura são realizados pelo Flyway.

## 5. Configurar as variáveis de ambiente

Antes de publicar o projeto, configure o `src/main/resources/application.properties` para receber as credenciais por variáveis de ambiente, em vez de manter senhas diretamente no arquivo:

```properties
spring.application.name=raizes-api

spring.datasource.url=jdbc:mysql://localhost:3306/raizes_do_nordeste?serverTimezone=America/Sao_Paulo
spring.datasource.username=${DB_USERNAME:dev}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=3600000
```

Crie suas variáveis de ambiente localmente. O arquivo `.env.example` apresenta os nomes das variáveis necessárias, mas o Spring Boot não carrega automaticamente um arquivo `.env`; os valores devem ser definidos no sistema operacional ou na configuração de execução da IDE.

### Exemplo no Windows PowerShell

```powershell
$env:DB_USERNAME="dev"
$env:DB_PASSWORD="SUA_SENHA_DO_MYSQL"
$env:JWT_SECRET="SUA_CHAVE_JWT"
```

> A implementação decodifica `JWT_SECRET` em Base64 e utiliza HMAC. Gere uma chave aleatória de pelo menos 32 bytes antes da codificação, por exemplo com o PowerShell abaixo. Não reutilize chaves públicas nem publique o valor gerado.

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)
$rng.Dispose()
```

## 6. Compilar e executar a aplicação

Na raiz do projeto, execute:

```bash
mvn clean package
```

Para iniciar a aplicação pelo Maven:

```bash
mvn spring-boot:run
```

Também é possível executar o JAR gerado após a compilação:

```bash
java -jar target/raizes-api-0.0.1-SNAPSHOT.jar
```

Com a aplicação iniciada corretamente, a API ficará disponível localmente em `http://localhost:8080`.

## 7. Documentação da API — Swagger / OpenAPI

A documentação interativa pode ser acessada pelo navegador:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Especificação OpenAPI: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Esses endereços são locais: para acessá-los, a aplicação precisa estar em execução no computador utilizado.

## 8. Testes com Postman

A coleção está disponível em:

```text
postman/Raizes_do_Nordeste_Postman.json
```

Para reproduzir as chamadas:

1. Inicie a aplicação e confirme que o banco e os dados iniciais foram preparados.
2. Abra o Postman e importe o arquivo JSON da pasta `postman/`.
3. Execute `POST /api/auth/cadastro` para cadastrar o usuário de teste `teste@gmail.com`.
4. Execute `POST /api/auth/login` com o mesmo e-mail e a senha cadastrada. Copie o JWT retornado e preencha a variável `token_cliente` nas variáveis da Collection.
5. Para testar a atualização autorizada de status, cadastre um segundo usuário pela API, utilizando um e-mail diferente, por exemplo `cozinha.teste@raizes.com`.
6. Como o cadastro público cria usuários com perfil `CLIENTE`, altere o perfil desse segundo usuário para `COZINHA` no banco de testes, executando no MySQL Workbench:

```sql
UPDATE usuario
SET perfil = 'COZINHA'
WHERE email = 'cozinha.teste@raizes.com';
```

7. Faça login com o segundo usuário e preencha a variável `token_cozinha` com o JWT retornado.
8. Execute a requisição de criação de pedido com pagamento aprovado (`simularFalha: false`). Copie o campo `idPedido` da resposta e informe seu valor na variável `id_pedido` da Collection.
9. Utilize `token_cozinha` na requisição PATCH autorizada e `token_cliente` na requisição PATCH que verifica a resposta 403. A requisição de consulta sem autenticação permanece configurada como `No Auth`.

A Collection utiliza as variáveis `token_cliente`, `token_cozinha` e `id_pedido`. Os valores devem ser preenchidos no Postman durante a execução e não devem ser exportados com tokens reais. A coleção contempla autenticação, criação e consulta de pedidos, atualização de status e cenários de validação/erro. Os resultados esperados e as evidências das execuções realizadas estão apresentados no relatório do projeto.

### Observação sobre os dados de teste

O script `database/testes.sql` prepara a unidade e o produto utilizados nos exemplos. O usuário/cliente é cadastrado pela própria API, que armazena a senha utilizando BCrypt e define inicialmente o perfil `CLIENTE`. A alteração para `COZINHA` descrita acima é uma preparação manual do ambiente de testes, necessária para reproduzir o cenário de autorização da Collection; ela não faz parte do cadastro público da aplicação.

## 9. Principais endpoints do fluxo implementado

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/api/auth/cadastro` | Cadastrar usuário |
| POST | `/api/auth/login` | Autenticar usuário e obter JWT |
| POST | `/api/pedidos` | Criar pedido e processar o pagamento simulado |
| GET | `/api/pedidos` | Consultar pedidos, com filtros e paginação |
| PATCH | `/api/pedidos/{idPedido}/status` | Atualizar o status do pedido |

As permissões e os formatos de entrada e saída devem ser consultados na documentação Swagger/OpenAPI.

## 10. Estrutura do repositório

```text
raizes-do-nordeste/
├── database/
│   └── testes.sql
├── postman/
│   └── Raizes_do_Nordeste_Postman.json
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       └── V1__create_initial_schema.sql
│   │       └── application.properties
│   └── test/
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

A estrutura acima apresenta os principais diretórios e arquivos relevantes para instalação, execução e testes. Outros pacotes e classes estão organizados dentro de `src/main/java`.

## 11. Limitações conhecidas

- O processamento de pagamento é uma simulação para demonstrar o fluxo do pedido; não existe integração com gateway ou instituição financeira.
- O controle operacional de estoque e a movimentação de estoque não fazem parte do fluxo implementado nesta versão.
- O programa de fidelidade e a auditoria de negócio não estão implementados nesta versão.

Esses itens podem ser considerados como evoluções futuras, mas não devem ser interpretados como funcionalidades disponíveis na API atual.

## 12. Repositório e materiais

- Código-fonte: `https://github.com/iGuiti/raizes-do-nordeste.git`
- Documentação da API: Swagger UI, conforme instruções da seção 7.
- Coleção de testes: arquivo JSON disponível em `postman/`.
- Modelo de dados (DER): apresentado no relatório acadêmico do projeto.
