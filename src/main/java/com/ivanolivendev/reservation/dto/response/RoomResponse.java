package com.ivanolivendev.reservation.dto.response;

import com.ivanolivendev.reservation.enums.RoomType;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        RoomType type,
        Integer capacity,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
