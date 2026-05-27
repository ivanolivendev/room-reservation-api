package com.ivanolivendev.reservation.validation;

import com.ivanolivendev.reservation.entity.Reservation;
import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.exception.BusinessException;
import com.ivanolivendev.reservation.exception.ConflictException;
import com.ivanolivendev.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public void validateCreation(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        validateTimeRange(startTime, endTime);
        validateDateIsNotPast(date);
        validateRoomIsActive(room);
        validateNoConflict(room.getId(), date, startTime, endTime);
    }

    public void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("Start time must be before end time");
        }
    }

    public void validateDateIsNotPast(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("Reservation date cannot be in the past");
        }
    }

    public void validateRoomIsActive(Room room) {
        if (Boolean.FALSE.equals(room.getActive())) {
            throw new BusinessException("Inactive rooms cannot be reserved");
        }
    }

    public void validateNoConflict(UUID roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        boolean hasConflict = reservationRepository.findByRoomIdAndDateAndStatus(roomId, date, ReservationStatus.ACTIVE)
                .stream()
                .anyMatch(reservation -> overlaps(reservation, startTime, endTime));

        if (hasConflict) {
            throw new ConflictException("Room already has an active reservation in this time range");
        }
    }

    private boolean overlaps(Reservation reservation, LocalTime startTime, LocalTime endTime) {
        return startTime.isBefore(reservation.getEndTime()) && endTime.isAfter(reservation.getStartTime());
    }
}
