package com.ivanolivendev.reservation.service;

import com.ivanolivendev.reservation.entity.Reservation;
import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.exception.ConflictException;
import com.ivanolivendev.reservation.exception.ResourceNotFoundException;
import com.ivanolivendev.reservation.repository.ReservationRepository;
import com.ivanolivendev.reservation.validation.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;
    private final ReservationValidator reservationValidator;

    @Transactional
    public Reservation create(UUID roomId, LocalDate date, LocalTime startTime, LocalTime endTime, String responsibleName) {
        Room room = roomService.findById(roomId);

        reservationValidator.validateCreation(room, date, startTime, endTime);

        Reservation reservation = Reservation.builder()
                .room(room)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .responsibleName(responsibleName)
                .status(ReservationStatus.ACTIVE)
                .build();

        return reservationRepository.save(reservation);
    }

    public Reservation findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findDailySchedule(LocalDate date) {
        return reservationRepository.findByDateAndStatus(date, ReservationStatus.ACTIVE);
    }

    @Transactional
    public Reservation cancel(UUID id) {
        Reservation reservation = findById(id);

        if (ReservationStatus.CANCELED.equals(reservation.getStatus())) {
            throw new ConflictException("Reservation is already canceled");
        }

        reservation.setStatus(ReservationStatus.CANCELED);
        return reservationRepository.save(reservation);
    }
}
