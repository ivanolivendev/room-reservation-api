package com.ivanolivendev.reservation.service;

import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.exception.ResourceNotFoundException;
import com.ivanolivendev.reservation.repository.ReservationRepository;
import com.ivanolivendev.reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public Room findById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return roomRepository.findByActiveTrue()
                .stream()
                .filter(room -> hasNoConflict(room, date, startTime, endTime))
                .toList();
    }

    private boolean hasNoConflict(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return reservationRepository
                .findByRoomIdAndDateAndStatus(room.getId(), date, ReservationStatus.ACTIVE)
                .stream()
                .noneMatch(reservation -> startTime.isBefore(reservation.getEndTime())
                        && endTime.isAfter(reservation.getStartTime()));
    }
}
