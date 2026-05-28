package com.ivanolivendev.reservation.controller;

import com.ivanolivendev.reservation.dto.request.CreateReservationRequest;
import com.ivanolivendev.reservation.dto.response.ErrorResponse;
import com.ivanolivendev.reservation.dto.response.ReservationResponse;
import com.ivanolivendev.reservation.mapper.ReservationMapper;
import com.ivanolivendev.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/reservations")
@Tag(name = "Reservations", description = "Criacao, consulta de agenda e cancelamento logico de reservas.")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar reserva",
            description = "Cria uma reserva ativa. Valida sala existente, sala ativa, data futura, horario valido e conflito de agenda.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = ReservationResponse.class),
                                    examples = @ExampleObject(name = "Reserva criada", value = """
                                            {
                                              "id": "44444444-4444-4444-4444-444444444444",
                                              "date": "2030-01-15",
                                              "startTime": "14:00:00",
                                              "endTime": "15:00:00",
                                              "responsibleName": "Ivan Oliveira",
                                              "status": "ACTIVE",
                                              "room": {
                                                "id": "11111111-1111-1111-1111-111111111111",
                                                "name": "Sala Reuniao 01",
                                                "type": "MEETING_ROOM",
                                                "capacity": 8,
                                                "active": true,
                                                "createdAt": "2026-05-28T11:00:00",
                                                "updatedAt": null
                                              },
                                              "createdAt": "2026-05-28T11:10:00",
                                              "updatedAt": null
                                            }
                                            """)
                            )),
                    @ApiResponse(responseCode = "400", description = "Payload invalido ou regra de negocio violada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Sala nao encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Conflito de horario",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-05-28T11:10:00",
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Room already has an active reservation in this time range",
                                              "path": "/reservations",
                                              "details": []
                                            }
                                            """)
                            ))
            }
    )
    public ReservationResponse create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da reserva que sera criada.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateReservationRequest.class),
                            examples = @ExampleObject(name = "Reserva valida", value = """
                                    {
                                      "roomId": "11111111-1111-1111-1111-111111111111",
                                      "date": "2030-01-15",
                                      "startTime": "14:00:00",
                                      "endTime": "15:00:00",
                                      "responsibleName": "Ivan Oliveira"
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return reservationMapper.toResponse(reservationService.create(
                request.roomId(),
                request.date(),
                request.startTime(),
                request.endTime(),
                request.responsibleName()
        ));
    }

    @GetMapping
    @Operation(
            summary = "Listar reservas",
            description = "Retorna todas as reservas cadastradas, incluindo reservas ativas e canceladas.",
            responses = @ApiResponse(responseCode = "200", description = "Reservas retornadas com sucesso",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class)),
                            examples = @ExampleObject(name = "Lista de reservas", value = """
                                    [
                                      {
                                        "id": "44444444-4444-4444-4444-444444444444",
                                        "date": "2030-01-15",
                                        "startTime": "14:00:00",
                                        "endTime": "15:00:00",
                                        "responsibleName": "Ivan Oliveira",
                                        "status": "ACTIVE",
                                        "room": {
                                          "id": "11111111-1111-1111-1111-111111111111",
                                          "name": "Sala Reuniao 01",
                                          "type": "MEETING_ROOM",
                                          "capacity": 8,
                                          "active": true,
                                          "createdAt": "2026-05-28T11:00:00",
                                          "updatedAt": null
                                        },
                                        "createdAt": "2026-05-28T11:10:00",
                                        "updatedAt": null
                                      }
                                    ]
                                    """)
                    ))
    )
    public List<ReservationResponse> findAll() {
        return reservationService.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar reserva por ID",
            description = "Retorna os detalhes de uma reserva especifica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reserva encontrada",
                            content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "UUID invalido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Reserva nao encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ReservationResponse findById(
            @Parameter(description = "UUID da reserva. Use um ID retornado por POST /reservations ou GET /reservations.", example = "44444444-4444-4444-4444-444444444444")
            @PathVariable UUID id
    ) {
        return reservationMapper.toResponse(reservationService.findById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancelar reserva",
            description = "Cancela uma reserva de forma logica, alterando o status para CANCELED.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = ReservationResponse.class),
                                    examples = @ExampleObject(name = "Reserva cancelada", value = """
                                            {
                                              "id": "44444444-4444-4444-4444-444444444444",
                                              "date": "2030-01-15",
                                              "startTime": "14:00:00",
                                              "endTime": "15:00:00",
                                              "responsibleName": "Ivan Oliveira",
                                              "status": "CANCELED",
                                              "room": {
                                                "id": "11111111-1111-1111-1111-111111111111",
                                                "name": "Sala Reuniao 01",
                                                "type": "MEETING_ROOM",
                                                "capacity": 8,
                                                "active": true,
                                                "createdAt": "2026-05-28T11:00:00",
                                                "updatedAt": null
                                              },
                                              "createdAt": "2026-05-28T11:10:00",
                                              "updatedAt": "2026-05-28T11:20:00"
                                            }
                                            """)
                            )),
                    @ApiResponse(responseCode = "400", description = "Reserva ja cancelada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Reserva nao encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ReservationResponse cancel(
            @Parameter(description = "UUID da reserva. Use um ID retornado por POST /reservations ou GET /reservations.", example = "44444444-4444-4444-4444-444444444444")
            @PathVariable UUID id
    ) {
        return reservationMapper.toResponse(reservationService.cancel(id));
    }

    @GetMapping("/daily")
    @Operation(
            summary = "Consultar agenda diaria",
            description = "Retorna as reservas ativas de uma data especifica. Reservas canceladas nao aparecem na agenda diaria.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Agenda diaria retornada com sucesso",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class)),
                                    examples = @ExampleObject(name = "Agenda diaria", value = """
                                            [
                                              {
                                                "id": "44444444-4444-4444-4444-444444444444",
                                                "date": "2030-01-15",
                                                "startTime": "14:00:00",
                                                "endTime": "15:00:00",
                                                "responsibleName": "Ivan Oliveira",
                                                "status": "ACTIVE",
                                                "room": {
                                                  "id": "11111111-1111-1111-1111-111111111111",
                                                  "name": "Sala Reuniao 01",
                                                  "type": "MEETING_ROOM",
                                                  "capacity": 8,
                                                  "active": true,
                                                  "createdAt": "2026-05-28T11:00:00",
                                                  "updatedAt": null
                                                },
                                                "createdAt": "2026-05-28T11:10:00",
                                                "updatedAt": null
                                              }
                                            ]
                                            """)
                            )),
                    @ApiResponse(responseCode = "400", description = "Parametro de data ausente ou invalido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public List<ReservationResponse> findDailySchedule(
            @Parameter(description = "Data da agenda.", example = "2030-01-15")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reservationService.findDailySchedule(date)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }
}
