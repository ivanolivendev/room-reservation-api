package com.ivanolivendev.reservation.repository;

import com.ivanolivendev.reservation.entity.Reservation;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByDateAndStatus(LocalDate date, ReservationStatus status);

    List<Reservation> findByRoomIdAndDateAndStatus(UUID roomId, LocalDate date, ReservationStatus status);
}
