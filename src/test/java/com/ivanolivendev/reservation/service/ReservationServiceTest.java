package com.ivanolivendev.reservation.service;

import com.ivanolivendev.reservation.entity.Reservation;
import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.enums.RoomType;
import com.ivanolivendev.reservation.exception.BusinessException;
import com.ivanolivendev.reservation.exception.ConflictException;
import com.ivanolivendev.reservation.repository.ReservationRepository;
import com.ivanolivendev.reservation.validation.ReservationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private ReservationValidator reservationValidator;

    @InjectMocks
    private ReservationService reservationService;

    private UUID roomId;
    private Room room;

    @BeforeEach
    void setUp() {
        roomId = UUID.randomUUID();
        room = Room.builder()
                .id(roomId)
                .name("Sala Reuniao 01")
                .type(RoomType.MEETING_ROOM)
                .capacity(8)
                .active(true)
                .build();
    }

    @Test
    void shouldCreateReservationSuccessfully() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        when(roomService.findById(roomId)).thenReturn(room);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation reservation = reservationService.create(roomId, date, startTime, endTime, "Ivan");

        assertThat(reservation.getRoom()).isEqualTo(room);
        assertThat(reservation.getDate()).isEqualTo(date);
        assertThat(reservation.getStartTime()).isEqualTo(startTime);
        assertThat(reservation.getEndTime()).isEqualTo(endTime);
        assertThat(reservation.getResponsibleName()).isEqualTo("Ivan");
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACTIVE);

        verify(reservationValidator).validateCreation(room, date, startTime, endTime);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldCancelReservation() {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.builder()
                .room(room)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .responsibleName("Ivan")
                .status(ReservationStatus.ACTIVE)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation canceled = reservationService.cancel(reservationId);

        assertThat(canceled.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCancelingAlreadyCanceledReservation() {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.builder()
                .room(room)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .responsibleName("Ivan")
                .status(ReservationStatus.CANCELED)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancel(reservationId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Reservation is already canceled");
    }

    @Test
    void shouldReturnDailySchedule() {
        LocalDate date = LocalDate.now().plusDays(1);
        Reservation reservation = Reservation.builder()
                .room(room)
                .date(date)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .responsibleName("Ivan")
                .status(ReservationStatus.ACTIVE)
                .build();

        when(reservationRepository.findByDateAndStatus(date, ReservationStatus.ACTIVE))
                .thenReturn(List.of(reservation));

        List<Reservation> schedule = reservationService.findDailySchedule(date);

        assertThat(schedule).containsExactly(reservation);
    }

    @Test
    void shouldThrowConflictException() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        when(roomService.findById(roomId)).thenReturn(room);
        org.mockito.Mockito.doThrow(new ConflictException("Room already has an active reservation in this time range"))
                .when(reservationValidator)
                .validateCreation(room, date, startTime, endTime);

        assertThatThrownBy(() -> reservationService.create(roomId, date, startTime, endTime, "Ivan"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Room already has an active reservation in this time range");
    }
}
