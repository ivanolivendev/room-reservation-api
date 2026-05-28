package com.ivanolivendev.reservation.dto.request;

import com.ivanolivendev.reservation.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para cadastro de uma nova sala.")
public record CreateRoomRequest(
        @Schema(description = "Nome unico da sala.", example = "Sala Treinamento 03", maxLength = 120)
        @NotBlank @Size(max = 120)
        String name,

        @Schema(description = "Tipo da sala.", example = "MEETING_ROOM", allowableValues = {"MEETING_ROOM", "INDIVIDUAL_ROOM", "AUDITORIUM"})
        @NotNull
        RoomType type,

        @Schema(description = "Capacidade maxima de pessoas na sala.", example = "20", minimum = "1")
        @NotNull @Min(1)
        Integer capacity
) {
}
