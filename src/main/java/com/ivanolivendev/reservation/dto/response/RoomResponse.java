package com.ivanolivendev.reservation.dto.response;

import com.ivanolivendev.reservation.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resposta com os dados de uma sala.")
public record RoomResponse(
        @Schema(description = "Identificador unico da sala.", example = "11111111-1111-1111-1111-111111111111")
        UUID id,

        @Schema(description = "Nome da sala.", example = "Sala Reuniao 01")
        String name,

        @Schema(description = "Tipo da sala.", example = "MEETING_ROOM")
        RoomType type,

        @Schema(description = "Capacidade maxima da sala.", example = "8")
        Integer capacity,

        @Schema(description = "Indica se a sala esta ativa.", example = "true")
        Boolean active,

        @Schema(description = "Data e hora de criacao do registro.", example = "2026-05-28T11:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da ultima atualizacao do registro.", example = "2026-05-28T11:30:00")
        LocalDateTime updatedAt
) {
}
