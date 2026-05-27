package com.ivanolivendev.reservation.service;

import com.ivanolivendev.reservation.entity.Reservation;
import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.enums.RoomType;
import com.ivanolivendev.reservation.repository.ReservationRepository;
import com.ivanolivendev.reservation.repository.RoomRepository;
import com.ivanolivendev.reservation.validation.ReservationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationValidator reservationValidator;

    @InjectMocks
    private RoomService roomService;

    @Test
    void shouldReturnAvailableRooms() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        Room freeRoom = room("Sala Livre");
        Room busyRoom = room("Sala Ocupada");

        Reservation busyReservation = Reservation.builder()
                .room(busyRoom)
                .date(date)
                .startTime(LocalTime.of(10, 30))
                .endTime(LocalTime.of(11, 30))
                .responsibleName("Ivan")
                .status(ReservationStatus.ACTIVE)
                .build();

        when(roomRepository.findByActiveTrue()).thenReturn(List.of(freeRoom, busyRoom));
        when(reservationRepository.findByRoomIdAndDateAndStatus(freeRoom.getId(), date, ReservationStatus.ACTIVE))
                .thenReturn(List.of());
        when(reservationRepository.findByRoomIdAndDateAndStatus(busyRoom.getId(), date, ReservationStatus.ACTIVE))
                .thenReturn(List.of(busyReservation));

        List<Room> availableRooms = roomService.findAvailableRooms(date, startTime, endTime);

        assertThat(availableRooms).containsExactly(freeRoom);
    }

    private Room room(String name) {
        return Room.builder()
                .id(UUID.randomUUID())
                .name(name)
                .type(RoomType.MEETING_ROOM)
                .capacity(8)
                .active(true)
                .build();
    }
}
