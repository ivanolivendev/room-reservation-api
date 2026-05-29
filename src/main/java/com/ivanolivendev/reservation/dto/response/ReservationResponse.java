package com.ivanolivendev.reservation.dto.response;

import com.ivanolivendev.reservation.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "Resposta com os dados de uma reserva.")
public record ReservationResponse(
        @Schema(description = "Identificador unico da reserva.", example = "44444444-4444-4444-4444-444444444444")
        UUID id,

        @Schema(description = "Data da reserva.", example = "2030-01-15")
        LocalDate date,

        @Schema(description = "Horario inicial da reserva.", example = "14:00:00")
        LocalTime startTime,

        @Schema(description = "Horario final da reserva.", example = "15:00:00")
        LocalTime endTime,

        @Schema(description = "Nome do responsavel pela reserva.", example = "Ivan Oliveira")
        String responsibleName,

        @Schema(description = "Status da reserva.", example = "ACTIVE")
        ReservationStatus status,

        @Schema(description = "Sala associada a reserva.")
        RoomResponse room,

        @Schema(description = "Data e hora de criacao do registro.", example = "2026-05-28T11:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da ultima atualizacao do registro.", example = "2026-05-28T11:30:00", nullable = true)
        LocalDateTime updatedAt
) {
}
