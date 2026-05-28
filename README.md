# Room Reservation API

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![Database](https://img.shields.io/badge/Database-H2-orange)
![Docs](https://img.shields.io/badge/Docs-Swagger%20UI-green)

API REST para gerenciamento de reservas de salas de reuniao, salas individuais e auditorios.

O projeto foi desenvolvido para o desafio tecnico FADESP, com foco em uma solucao simples, organizada e funcional para centralizar reservas, reduzir conflitos de agenda e melhorar a confiabilidade do processo.

## Links

- Repositorio: https://github.com/ivanolivendev/room-reservation-api
- LinkedIn: https://www.linkedin.com/in/ivanolivendev/

## Visao Geral

A aplicacao permite:

- cadastrar salas;
- listar e consultar salas;
- atualizar e desativar salas;
- criar reservas para uma sala em um dia e horario especificos;
- impedir reservas com conflito de horario;
- cancelar reservas de forma logica;
- consultar agenda diaria;
- consultar salas disponiveis em uma data e faixa de horario.

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- Flyway
- Lombok
- Springdoc OpenAPI / Swagger UI
- JUnit 5
- Mockito
- Maven Wrapper

## Arquitetura

O projeto segue uma organizacao por camadas:

```text
controller   -> entrada HTTP da API
service      -> regras de negocio e orquestracao
validation   -> validacoes especificas de reserva
repository   -> persistencia com Spring Data JPA
entity       -> entidades JPA
dto          -> contratos de entrada e saida
mapper       -> conversao entre entidade e DTO
exception    -> excecoes e tratamento global de erros
config       -> configuracoes da aplicacao
```

## Regras de Negocio

O sistema aplica as seguintes regras:

- uma sala nao pode ter duas reservas ativas com horarios sobrepostos no mesmo dia;
- `startTime` deve ser menor que `endTime`;
- nao e permitido criar reserva no passado;
- salas inativas nao podem receber novas reservas;
- cancelamento de reserva e logico: o status muda para `CANCELED`;
- exclusao de sala e tratada como desativacao: `active = false`;
- nomes de salas nao podem ser duplicados.

Tipos de sala aceitos:

```text
MEETING_ROOM
INDIVIDUAL_ROOM
AUDITORIUM
```

Status de reserva:

```text
ACTIVE
CANCELED
```

## Pre-requisitos

- Java 17 instalado.
- Git instalado, caso deseje clonar o repositorio.
- Maven nao precisa estar instalado globalmente, pois o projeto utiliza Maven Wrapper.

## Como Executar

Na raiz do projeto, execute o comando conforme o seu sistema operacional.

Windows:

```powershell
cmd.exe /c mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

## Swagger

Com a aplicacao rodando, acesse:

```text
http://localhost:8080/swagger-ui.html
```

O Swagger documenta:

- endpoints;
- parametros;
- payloads;
- exemplos;
- status HTTP esperados;
- respostas de erro padronizadas.

## H2 Console

Com a aplicacao rodando, acesse:

```text
http://localhost:8080/h2-console
```

Use as credenciais:

```text
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:mem:reservationdb
User Name: sa
Password:
```

O campo `Password` deve ficar vazio.

Consultas uteis:

```sql
SELECT * FROM rooms;
SELECT * FROM reservations;
```

Como o H2 esta em memoria, os dados sao recriados sempre que a aplicacao reinicia.

## Migrations

O banco e versionado com Flyway.

Arquivos principais:

```text
src/main/resources/db/migration/V1__create_tables.sql
src/main/resources/db/migration/V2__seed_rooms.sql
```

A migration inicial cria:

- `rooms`
- `reservations`

A segunda migration insere salas iniciais para facilitar os testes.

Salas iniciais disponiveis:

| ID | Nome | Tipo | Capacidade |
| --- | --- | --- | --- |
| `11111111-1111-1111-1111-111111111111` | Sala Reuniao 01 | `MEETING_ROOM` | 8 |
| `22222222-2222-2222-2222-222222222222` | Sala Individual 01 | `INDIVIDUAL_ROOM` | 1 |
| `33333333-3333-3333-3333-333333333333` | Auditorio Principal | `AUDITORIUM` | 80 |

## Endpoints

### Salas

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| POST | `/rooms` | Cadastra uma nova sala |
| GET | `/rooms` | Lista todas as salas |
| GET | `/rooms/{id}` | Busca uma sala por ID |
| PUT | `/rooms/{id}` | Atualiza uma sala |
| DELETE | `/rooms/{id}` | Desativa uma sala |
| GET | `/rooms/available` | Consulta salas disponiveis |

### Reservas

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| POST | `/reservations` | Cria uma reserva |
| GET | `/reservations` | Lista todas as reservas |
| GET | `/reservations/{id}` | Busca uma reserva por ID |
| DELETE | `/reservations/{id}` | Cancela uma reserva |
| GET | `/reservations/daily` | Consulta agenda diaria |

## Exemplos de Uso

### Criar sala

```http
POST /rooms
Content-Type: application/json
```

```json
{
  "name": "Sala Treinamento 03",
  "type": "MEETING_ROOM",
  "capacity": 20
}
```

### Atualizar sala

```http
PUT /rooms/{id}
Content-Type: application/json
```

```json
{
  "name": "Sala Treinamento 03 Atualizada",
  "type": "MEETING_ROOM",
  "capacity": 24,
  "active": true
}
```

### Consultar salas disponiveis

```http
GET /rooms/available?date=2030-01-15&startTime=14:00:00&endTime=15:00:00
```

### Criar reserva

```http
POST /reservations
Content-Type: application/json
```

```json
{
  "roomId": "11111111-1111-1111-1111-111111111111",
  "date": "2030-01-15",
  "startTime": "14:00:00",
  "endTime": "15:00:00",
  "responsibleName": "Ivan Oliveira"
}
```

### Consultar agenda diaria

```http
GET /reservations/daily?date=2030-01-15
```

### Cancelar reserva

```http
DELETE /reservations/{id}
```

## Respostas de Erro

Todas as respostas de erro seguem um formato padronizado:

```json
{
  "timestamp": "2026-05-28T11:10:00",
  "status": 409,
  "error": "Conflict",
  "message": "Room already has an active reservation in this time range",
  "path": "/reservations",
  "details": []
}
```

Principais status:

| Status | Quando ocorre |
| --- | --- |
| 400 | Payload invalido, parametro invalido ou regra de negocio violada |
| 404 | Sala ou reserva nao encontrada |
| 409 | Conflito de horario ou nome de sala duplicado |
| 500 | Erro inesperado |

## Testes

Para rodar todos os testes, execute:

Windows:

```powershell
cmd.exe /c mvnw.cmd test
```

macOS/Linux:

```bash
./mvnw test
```

Para rodar testes especificos, execute:

Windows:

```powershell
cmd.exe /c mvnw.cmd test "-Dtest=ReservationServiceTest,RoomServiceTest"
```

macOS/Linux:

```bash
./mvnw test -Dtest=ReservationServiceTest,RoomServiceTest
```

Os testes cobrem:

- contexto Spring;
- regras de negocio dos services;
- criacao de reserva;
- conflito de horario;
- cancelamento logico;
- salas disponiveis;
- controllers;
- validacoes de payload;
- respostas de erro padronizadas.

## Exemplos Prontos

Payloads de exemplo estao disponiveis para facilitar os testes manuais pelo Swagger, Insomnia ou Postman:

```text
docs/requests/create-room.json
docs/requests/update-room.json
docs/requests/create-reservation.json
docs/requests/error-response.json
```

Com esses exemplos, e possivel testar os principais fluxos da API:

- criar sala;
- atualizar sala;
- consultar salas disponiveis;
- criar reserva;
- consultar agenda diaria;
- cancelar reserva;
- validar respostas de erro.

Os endpoints de consulta e cancelamento nao exigem payload no corpo da requisicao. Para rotas com `{id}`, use um identificador retornado nas respostas de `GET /rooms`, `POST /rooms`, `GET /reservations` ou `POST /reservations`.

Tambem existe uma collection do Insomnia em:

```text
docs/insomnia-room-reservation-api.json
```

## Deploy opcional

Como complemento a execucao local, o projeto tambem possui uma versao publicada na Railway:

```text
https://room-reservation-api-production.up.railway.app
```

Um teste rapido pode ser feito pelo endpoint:

```http
GET https://room-reservation-api-production.up.railway.app/rooms
```

A mesma URL base pode ser usada no Insomnia ou Postman, substituindo a variavel `baseUrl` por:

```text
https://room-reservation-api-production.up.railway.app
```

Na Railway, a aplicacao foi publicada usando Java 17, mantendo o ambiente de hospedagem alinhado com a versao configurada no repositorio e com o requisito do desafio. Como o deploy utiliza Railpack, essa versao e definida pela variavel:

```text
RAILPACK_JDK_VERSION=17
```

Importante: o banco utilizado e H2 em memoria para facilitar a avaliacao do desafio. Por isso, na versao publicada, os dados podem ser recriados quando a aplicacao reiniciar.

## Observacoes

Este projeto prioriza clareza, separacao de responsabilidades e simplicidade. A solucao evita complexidade desnecessaria, mas mantem boas praticas esperadas em uma API corporativa: DTOs, services, validadores, migrations, tratamento global de erros, Swagger e testes.
