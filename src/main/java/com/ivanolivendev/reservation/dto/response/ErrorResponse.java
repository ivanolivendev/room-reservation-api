package com.ivanolivendev.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta padronizada para erros da API.")
public record ErrorResponse(
        @Schema(description = "Data e hora em que o erro ocorreu.", example = "2026-05-28T11:10:00")
        LocalDateTime timestamp,

        @Schema(description = "Codigo HTTP da resposta.", example = "409")
        int status,

        @Schema(description = "Descricao curta do status HTTP.", example = "Conflict")
        String error,

        @Schema(description = "Mensagem principal do erro.", example = "Room already has an active reservation in this time range")
        String message,

        @Schema(description = "Caminho da requisicao que gerou o erro.", example = "/reservations")
        String path,

        @Schema(description = "Lista de detalhes adicionais, normalmente usada em erros de validacao.", example = "[\"name: must not be blank\"]")
        List<String> details
) {
}
