package com.ivanolivendev.reservation.mapper;

import com.ivanolivendev.reservation.dto.response.ReservationResponse;
import com.ivanolivendev.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final RoomMapper roomMapper;

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getResponsibleName(),
                reservation.getStatus(),
                roomMapper.toResponse(reservation.getRoom()),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
