package com.ivanolivendev.reservation.dto.response;

import com.ivanolivendev.reservation.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String responsibleName,
        ReservationStatus status,
        RoomResponse room,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
