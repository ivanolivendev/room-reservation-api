package com.ivanolivendev.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanolivendev.reservation.dto.request.CreateReservationRequest;
import com.ivanolivendev.reservation.entity.Reservation;
import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.ReservationStatus;
import com.ivanolivendev.reservation.enums.RoomType;
import com.ivanolivendev.reservation.exception.BusinessException;
import com.ivanolivendev.reservation.exception.ConflictException;
import com.ivanolivendev.reservation.mapper.ReservationMapper;
import com.ivanolivendev.reservation.mapper.RoomMapper;
import com.ivanolivendev.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import({ReservationMapper.class, RoomMapper.class})
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @Test
    void shouldCreateReservation() throws Exception {
        UUID roomId = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        CreateReservationRequest request = new CreateReservationRequest(
                roomId,
                date,
                startTime,
                endTime,
                "Ivan"
        );
        Reservation reservation = reservation(roomId, date, startTime, endTime, ReservationStatus.ACTIVE);

        when(reservationService.create(roomId, date, startTime, endTime, "Ivan")).thenReturn(reservation);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(date.toString()))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("10:00:00"))
                .andExpect(jsonPath("$.responsibleName").value("Ivan"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.room.id").value(roomId.toString()));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidReservation() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                null,
                LocalDate.now().minusDays(1),
                null,
                null,
                ""
        );

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/reservations"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldReturnConflictWhenReservationOverlapsExistingReservation() throws Exception {
        UUID roomId = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        CreateReservationRequest request = new CreateReservationRequest(
                roomId,
                date,
                startTime,
                endTime,
                "Ivan"
        );

        when(reservationService.create(roomId, date, startTime, endTime, "Ivan"))
                .thenThrow(new ConflictException("Room already has an active reservation in this time range"));

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Room already has an active reservation in this time range"))
                .andExpect(jsonPath("$.path").value("/reservations"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldFindAllReservations() throws Exception {
        UUID roomId = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(1);

        when(reservationService.findAll())
                .thenReturn(List.of(reservation(roomId, date, LocalTime.of(9, 0), LocalTime.of(10, 0), ReservationStatus.ACTIVE)));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(date.toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void shouldFindReservationById() throws Exception {
        UUID id = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(1);
        Reservation reservation = reservation(id, roomId, date, LocalTime.of(9, 0), LocalTime.of(10, 0), ReservationStatus.ACTIVE);

        when(reservationService.findById(id)).thenReturn(reservation);

        mockMvc.perform(get("/reservations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.room.id").value(roomId.toString()));
    }

    @Test
    void shouldCancelReservation() throws Exception {
        UUID id = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        Reservation reservation = reservation(
                id,
                roomId,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                ReservationStatus.CANCELED
        );

        when(reservationService.cancel(id)).thenReturn(reservation);

        mockMvc.perform(delete("/reservations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    void shouldReturnBadRequestWhenCancelingAlreadyCanceledReservation() throws Exception {
        UUID id = UUID.randomUUID();

        when(reservationService.cancel(id)).thenThrow(new BusinessException("Reservation is already canceled"));

        mockMvc.perform(delete("/reservations/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Reservation is already canceled"))
                .andExpect(jsonPath("$.path").value("/reservations/" + id))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldFindDailySchedule() throws Exception {
        UUID roomId = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(1);

        when(reservationService.findDailySchedule(date))
                .thenReturn(List.of(reservation(roomId, date, LocalTime.of(9, 0), LocalTime.of(10, 0), ReservationStatus.ACTIVE)));

        mockMvc.perform(get("/reservations/daily")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(date.toString()))
                .andExpect(jsonPath("$[0].room.id").value(roomId.toString()));
    }

    private Reservation reservation(
            UUID roomId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    ) {
        return reservation(UUID.randomUUID(), roomId, date, startTime, endTime, status);
    }

    private Reservation reservation(
            UUID id,
            UUID roomId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    ) {
        return Reservation.builder()
                .id(id)
                .room(room(roomId))
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .responsibleName("Ivan")
                .status(status)
                .build();
    }

    private Room room(UUID id) {
        return Room.builder()
                .id(id)
                .name("Sala Reuniao 01")
                .type(RoomType.MEETING_ROOM)
                .capacity(8)
                .active(true)
                .build();
    }
}
