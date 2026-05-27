package com.ivanolivendev.reservation.dto.request;

import com.ivanolivendev.reservation.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull RoomType type,
        @NotNull @Min(1) Integer capacity
) {
}
