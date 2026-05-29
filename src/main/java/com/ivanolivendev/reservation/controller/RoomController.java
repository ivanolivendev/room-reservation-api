package com.ivanolivendev.reservation.controller;

import com.ivanolivendev.reservation.dto.request.CreateRoomRequest;
import com.ivanolivendev.reservation.dto.request.UpdateRoomRequest;
import com.ivanolivendev.reservation.dto.response.ErrorResponse;
import com.ivanolivendev.reservation.dto.response.RoomResponse;
import com.ivanolivendev.reservation.exception.BusinessException;
import com.ivanolivendev.reservation.mapper.RoomMapper;
import com.ivanolivendev.reservation.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/rooms")
@Tag(name = "Rooms", description = "Cadastro, consulta, atualizacao e disponibilidade de salas.")
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @PostMapping
    @Operation(
            operationId = "createRoom",
            summary = "Cadastrar sala",
            description = "Cria uma nova sala ativa. O nome deve ser unico e a capacidade deve ser maior que zero.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sala criada com sucesso",
                            headers = @Header(
                                    name = "Location",
                                    description = "URI do recurso criado.",
                                    schema = @Schema(type = "string", example = "/rooms/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                            ),
                            content = @Content(
                                    schema = @Schema(implementation = RoomResponse.class),
                                    examples = @ExampleObject(name = "Sala criada", value = """
                                            {
                                              "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                                              "name": "Sala Treinamento 03",
                                              "type": "MEETING_ROOM",
                                              "capacity": 20,
                                              "active": true,
                                              "createdAt": "2026-05-28T11:00:00",
                                              "updatedAt": null
                                            }
                                            """)
                            )),
                    @ApiResponse(responseCode = "400", description = "Payload invalido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Nome de sala ja cadastrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<RoomResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da sala que sera cadastrada.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateRoomRequest.class),
                            examples = @ExampleObject(name = "Sala de reuniao", value = """
                                    {
                                      "name": "Sala Treinamento 03",
                                      "type": "MEETING_ROOM",
                                      "capacity": 20
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody CreateRoomRequest request
    ) {
        RoomResponse response = roomMapper.toResponse(roomService.create(request.name(), request.type(), request.capacity()));
        return ResponseEntity.created(URI.create("/rooms/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(
            operationId = "listRooms",
            summary = "Listar salas",
            description = "Retorna todas as salas cadastradas.",
            responses = @ApiResponse(responseCode = "200", description = "Salas retornadas com sucesso",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RoomResponse.class)),
                            examples = @ExampleObject(name = "Lista de salas", value = """
                                    [
                                      {
                                        "id": "11111111-1111-1111-1111-111111111111",
                                        "name": "Sala Reuniao 01",
                                        "type": "MEETING_ROOM",
                                        "capacity": 8,
                                        "active": true,
                                        "createdAt": "2026-05-28T11:00:00",
                                        "updatedAt": null
                                      }
                                    ]
                                    """)
                    ))
    )
    public List<RoomResponse> findAll() {
        return roomService.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            operationId = "getRoom",
            summary = "Buscar sala por ID",
            description = "Retorna os detalhes de uma sala especifica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sala encontrada",
                            content = @Content(schema = @Schema(implementation = RoomResponse.class))),
                    @ApiResponse(responseCode = "400", description = "UUID invalido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Sala nao encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public RoomResponse findById(
            @Parameter(description = "Identificador unico da sala no formato UUID.", example = "11111111-1111-1111-1111-111111111111")
            @PathVariable UUID id
    ) {
        return roomMapper.toResponse(roomService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            operationId = "updateRoom",
            summary = "Atualizar sala",
            description = "Atualiza os dados completos de uma sala existente, incluindo o status ativo/inativo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sala atualizada com sucesso",
                            content = @Content(schema = @Schema(implementation = RoomResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Payload invalido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Sala nao encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Nome de sala ja cadastrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public RoomResponse update(
            @Parameter(description = "Identificador unico da sala no formato UUID.", example = "11111111-1111-1111-1111-111111111111")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualizacao da sala.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateRoomRequest.class),
                                    examples = @ExampleObject(name = "Atualizacao de sala", value = """
                                            {
                                              "name": "Sala Treinamento 03 Atualizada",
                                              "type": "MEETING_ROOM",
                                              "capacity": 24,
                                              "active": true
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody UpdateRoomRequest request
    ) {
        return roomMapper.toResponse(roomService.update(
                id,
                request.name(),
                request.type(),
                request.capacity(),
                request.active()
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            operationId = "deactivateRoom",
            summary = "Desativar sala",
            description = "Desativa uma sala sem remover o registro do banco. Salas inativas nao recebem novas reservas.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Sala desativada com sucesso",
                            content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Sala nao encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public void delete(
            @Parameter(description = "Identificador unico da sala no formato UUID.", example = "11111111-1111-1111-1111-111111111111")
            @PathVariable UUID id
    ) {
        roomService.deactivate(id);
    }

    @GetMapping("/available")
    @Operation(
            operationId = "getAvailableRooms",
            summary = "Consultar salas disponiveis",
            description = "Retorna salas ativas sem reserva conflitante para a data e faixa de horario informadas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Salas disponiveis retornadas com sucesso",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = RoomResponse.class)),
                                    examples = @ExampleObject(name = "Salas disponiveis", value = """
                                            [
                                              {
                                                "id": "11111111-1111-1111-1111-111111111111",
                                                "name": "Sala Reuniao 01",
                                                "type": "MEETING_ROOM",
                                                "capacity": 8,
                                                "active": true,
                                                "createdAt": "2026-05-28T11:00:00",
                                                "updatedAt": null
                                              }
                                            ]
                                            """)
                            )),
                    @ApiResponse(responseCode = "400", description = "Parametros invalidos ou faixa de horario invalida",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-05-28T11:10:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Start time must be before end time",
                                              "path": "/rooms/available",
                                              "details": []
                                            }
                                            """)
                            ))
            }
    )
    public List<RoomResponse> findAvailableRooms(
            @Parameter(description = "Data desejada para a reserva.", example = "2030-01-15")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Horario inicial desejado.", schema = @Schema(type = "string", format = "time", example = "14:00:00"))
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @Parameter(description = "Horario final desejado.", schema = @Schema(type = "string", format = "time", example = "15:00:00"))
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("Start time must be before end time");
        }

        return roomService.findAvailableRooms(date, startTime, endTime)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }
}
