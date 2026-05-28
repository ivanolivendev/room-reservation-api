package com.ivanolivendev.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "Payload para criacao de uma reserva.")
public record CreateReservationRequest(
        @Schema(description = "UUID da sala. Use um ID retornado por GET /rooms.", example = "11111111-1111-1111-1111-111111111111")
        @NotNull
        UUID roomId,

        @Schema(description = "Data da reserva. Deve ser hoje ou uma data futura.", example = "2030-01-15", type = "string", format = "date")
        @NotNull @FutureOrPresent
        LocalDate date,

        @Schema(description = "Horario inicial da reserva.", example = "14:00:00", type = "string", format = "time")
        @NotNull
        LocalTime startTime,

        @Schema(description = "Horario final da reserva. Deve ser maior que startTime.", example = "15:00:00", type = "string", format = "time")
        @NotNull
        LocalTime endTime,

        @Schema(description = "Nome do responsavel pela reserva.", example = "Ivan Oliveira", maxLength = 120)
        @NotBlank @Size(max = 120)
        String responsibleName
) {
}
