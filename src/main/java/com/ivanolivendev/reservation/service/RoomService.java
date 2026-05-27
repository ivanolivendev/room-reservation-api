package com.ivanolivendev.reservation.service;

import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.enums.RoomType;
import com.ivanolivendev.reservation.exception.ConflictException;
import com.ivanolivendev.reservation.exception.ResourceNotFoundException;
import com.ivanolivendev.reservation.repository.ReservationRepository;
import com.ivanolivendev.reservation.repository.RoomRepository;
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
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;

    @Transactional
    public Room create(String name, RoomType type, Integer capacity) {
        validateNameAvailable(name);

        Room room = Room.builder()
                .name(name)
                .type(type)
                .capacity(capacity)
                .active(true)
                .build();

        return roomRepository.save(room);
    }

    public Room findById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Transactional
    public Room update(UUID id, String name, RoomType type, Integer capacity, Boolean active) {
        Room room = findById(id);
        validateNameAvailableForUpdate(id, name);

        room.setName(name);
        room.setType(type);
        room.setCapacity(capacity);
        room.setActive(active);

        return roomRepository.save(room);
    }

    @Transactional
    public void deactivate(UUID id) {
        Room room = findById(id);
        room.setActive(false);
        roomRepository.save(room);
    }

    public List<Room> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        reservationValidator.validateTimeRange(startTime, endTime);
        reservationValidator.validateDateIsNotPast(date);

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

    private void validateNameAvailable(String name) {
        if (roomRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Room name already exists");
        }
    }

    private void validateNameAvailableForUpdate(UUID id, String name) {
        boolean duplicatedName = roomRepository.findAll()
                .stream()
                .anyMatch(room -> !room.getId().equals(id) && room.getName().equalsIgnoreCase(name));

        if (duplicatedName) {
            throw new ConflictException("Room name already exists");
        }
    }
}
