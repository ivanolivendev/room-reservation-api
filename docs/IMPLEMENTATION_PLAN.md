# Plano de Implementacao - Room Reservation API

Este documento define a jornada de implementacao do desafio FADESP para a API de reservas de salas e auditorios.

A ideia principal e evoluir o projeto em sprints pequenas, com entregas verificaveis a cada etapa. Assim o sistema cresce de forma organizada: primeiro o core do dominio, depois regras de negocio, API REST, tratamento de erros, documentacao, testes e revisao final.

## Objetivo do Sistema

Construir uma API REST em Java com Spring Boot para centralizar o processo de reservas de salas de reuniao, salas individuais e auditorios.

O sistema deve permitir:

- Cadastro de salas.
- Realizacao de reservas para um dia especifico.
- Validacao de conflitos de horario.
- Cancelamento de reservas.
- Consulta de agenda diaria.
- Consulta de salas livres em um determinado dia e horario.

## Tecnologias Definidas

- Java 17.
- Spring Boot 3.
- Spring Web.
- Spring Data JPA.
- Spring Validation.
- H2 Database.
- Flyway para migrations.
- Lombok para reduzir codigo repetitivo.
- Springdoc OpenAPI para Swagger.
- JUnit e Mockito para testes.

## Decisoes de Arquitetura

O projeto sera organizado por camadas:

- `controller`: entrada HTTP da aplicacao.
- `service`: regras de negocio e orquestracao.
- `repository`: acesso ao banco de dados.
- `entity`: entidades JPA.
- `dto`: objetos de entrada e saida da API.
- `mapper`: conversao entre entidade e DTO.
- `validation`: validacoes de negocio mais especificas.
- `exception`: excecoes customizadas e tratamento global.
- `config`: configuracoes da aplicacao.
- `util`: helpers simples, caso sejam realmente necessarios.

O foco e manter o projeto profissional, mas sem overengineering.

## Sprint 01 - Core do Dominio

### Objetivo

Criar a base estrutural do sistema: entidades, enums, repositories, configuracao do banco e migrations.

### Tarefas

- Criar enum `RoomType`.
- Criar enum `ReservationStatus`.
- Criar entidade base `BaseEntity`.
- Criar entidade `Room`.
- Criar entidade `Reservation`.
- Criar `RoomRepository`.
- Criar `ReservationRepository`.
- Configurar H2 em `application.yml`.
- Configurar Flyway.
- Criar migration `V1__create_tables.sql`.
- Criar migration `V2__seed_rooms.sql`.
- Garantir que o contexto Spring sobe corretamente.

### Entregaveis

- Projeto compila.
- Migrations executam com sucesso.
- Tabelas `rooms` e `reservations` sao criadas pelo Flyway.
- Repositories sao carregados pelo Spring Data JPA.
- Teste inicial de contexto passa.

### Criterio de Validacao

Executar:

```powershell
cmd.exe /c mvnw.cmd test
```

Resultado esperado:

```text
BUILD SUCCESS
```

## Sprint 02 - Regras de Negocio

### Objetivo

Implementar o comportamento principal do desafio, ainda sem focar na camada HTTP.

### Tarefas

- Criar `RoomService`.
- Criar `ReservationService`.
- Criar `ReservationValidator`.
- Implementar criacao de reserva.
- Implementar cancelamento logico de reserva.
- Implementar busca de agenda diaria.
- Implementar consulta de salas disponiveis.

### Regras de Negocio

1. Nao permitir conflito de horario.

Uma reserva entra em conflito quando:

- E para a mesma sala.
- E para o mesmo dia.
- A reserva existente esta ativa.
- Os horarios se sobrepoem.

Regra de sobreposicao:

```text
novoInicio < reservaExistenteFim
novoFim > reservaExistenteInicio
```

2. Nao permitir horario invalido.

```text
startTime >= endTime
```

3. Nao permitir reserva no passado.

Uma reserva nao pode ser criada para uma data anterior a data atual.

4. Nao permitir reserva em sala inativa.

Se `room.active = false`, a sala nao pode receber novas reservas.

5. Cancelamento logico.

Reservas nao serao removidas fisicamente do banco.

Ao cancelar:

```text
status = CANCELED
```

### Entregaveis

- Services funcionando.
- Validator centralizando regras sensiveis.
- Repositories usados apenas para persistencia.
- Nenhuma regra de negocio dentro de controller.

### Criterio de Validacao

Executar testes unitarios simples dos services.

## Sprint 03 - API REST

### Objetivo

Expor as funcionalidades do sistema por endpoints REST com payloads JSON.

### Tarefas

- Criar DTOs de request.
- Criar DTOs de response.
- Criar mappers.
- Criar `RoomController`.
- Criar `ReservationController`.
- Aplicar validacoes de entrada com Bean Validation.

### Endpoints de Salas

```http
POST   /rooms
GET    /rooms
GET    /rooms/{id}
PUT    /rooms/{id}
DELETE /rooms/{id}
GET    /rooms/available
```

### Endpoints de Reservas

```http
POST   /reservations
GET    /reservations
GET    /reservations/{id}
DELETE /reservations/{id}
GET    /reservations/daily
```

### Comportamento Esperado

- Controllers recebem requisicoes HTTP.
- Controllers validam entrada basica.
- Controllers chamam services.
- Services executam regras de negocio.
- Responses retornam JSON.

### Entregaveis

- API navegavel por Swagger/Postman.
- Payloads de entrada separados das entidades.
- Respostas padronizadas.

## Sprint 04 - Tratamento Profissional de Erros

### Objetivo

Padronizar erros da API com respostas claras e status HTTP corretos.

### Tarefas

- Criar `BusinessException`.
- Criar `ResourceNotFoundException`.
- Criar `ConflictException`.
- Criar `GlobalExceptionHandler`.
- Criar `ErrorResponse`.
- Tratar erros de validacao do Bean Validation.

### Status HTTP Esperados

- `400 Bad Request`: dados invalidos.
- `404 Not Found`: recurso nao encontrado.
- `409 Conflict`: conflito de horario ou regra conflitante.
- `500 Internal Server Error`: erro inesperado.

### Exemplo de Resposta de Erro

```json
{
  "timestamp": "2026-05-27T14:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Room already has an active reservation in this time range",
  "path": "/reservations",
  "details": []
}
```

### Entregaveis

- API nao retorna stack trace.
- Erros de negocio ficam claros para o cliente.
- Erros de validacao indicam campos invalidos.

## Sprint 05 - Swagger e Documentacao

### Objetivo

Deixar a API facil de testar, apresentar e avaliar.

### Tarefas

- Configurar OpenAPI.
- Garantir acesso ao Swagger UI.
- Adicionar descricoes basicas nos controllers.
- Criar exemplos JSON em `docs/requests`.
- Criar README completo.

### URLs Importantes

Swagger:

```text
/swagger-ui.html
```

H2 Console:

```text
/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:reservationdb
```

### README Deve Conter

- Descricao do projeto.
- Tecnologias utilizadas.
- Como executar localmente.
- Como acessar Swagger.
- Como acessar H2 Console.
- Lista de endpoints.
- Exemplos de payload JSON.
- Como rodar testes.

### Entregaveis

- README pronto para GitHub.
- Swagger funcionando.
- Exemplos de requisicao disponiveis no projeto.

## Sprint 06 - Testes Simples

### Objetivo

Garantir que as principais regras do desafio funcionam.

### Tarefas

- Criar `ReservationServiceTest`.
- Criar `RoomServiceTest`, se fizer sentido para o escopo.
- Criar teste de repository para consultas importantes, se necessario.
- Criar teste de integracao simples para fluxo principal.

### Testes Minimos

```java
shouldCreateReservationSuccessfully()
shouldThrowConflictException()
shouldCancelReservation()
shouldReturnAvailableRooms()
```

### Cenarios Importantes

- Criacao de reserva valida.
- Tentativa de reserva com conflito.
- Tentativa de reserva com horario invalido.
- Tentativa de reserva no passado.
- Tentativa de reserva em sala inativa.
- Cancelamento logico.
- Consulta de agenda diaria.
- Consulta de salas disponiveis.

### Entregaveis

- Testes objetivos.
- Regras principais protegidas.
- Build executando com sucesso.

## Sprint 07 - Revisao Final

### Objetivo

Preparar o projeto para entrega em repositorio publico.

### Tarefas

- Rodar todos os testes.
- Revisar estrutura de pacotes.
- Revisar nomes de classes e metodos.
- Revisar migrations.
- Revisar README.
- Revisar exemplos JSON.
- Conferir Swagger.
- Conferir H2 Console.
- Remover arquivos desnecessarios.
- Verificar `.gitignore`.

### Checklist Final

- Projeto compila.
- Testes passam.
- API sobe localmente.
- Swagger abre.
- H2 Console abre.
- Endpoints principais funcionam.
- README explica como executar.
- Repositorio esta limpo e organizado.

## Ordem de Implementacao Acordada

1. Sprint 01 - Core do Dominio.
2. Sprint 02 - Regras de Negocio.
3. Sprint 03 - API REST.
4. Sprint 04 - Tratamento de Erros.
5. Sprint 05 - Swagger e Documentacao.
6. Sprint 06 - Testes Simples.
7. Sprint 07 - Revisao Final.

## Principios do Projeto

- Simples antes de complexo.
- Regras de negocio fora dos controllers.
- Entidades protegidas por DTOs.
- Persistencia isolada nos repositories.
- Migrations versionadas.
- Erros claros.
- Testes focados no que importa.
- Codigo legivel para avaliacao tecnica.

## Estado Atual

A Sprint 01 foi iniciada com:

- Entidades principais.
- Enums principais.
- Repositories.
- Configuracao H2.
- Configuracao Flyway.
- Migrations iniciais.

Antes de seguir para a Sprint 02, o projeto deve estar compilando corretamente pela IDE e pelo Maven.
