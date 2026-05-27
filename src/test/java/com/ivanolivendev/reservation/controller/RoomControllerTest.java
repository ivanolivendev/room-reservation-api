package com.ivanolivendev.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanolivendev.reservation.dto.request.CreateRoomRequest;
import com.ivanolivendev.reservation.dto.request.UpdateRoomRequest;
import com.ivanolivendev.reservation.entity.Room;
import com.ivanolivendev.reservation.enums.RoomType;
import com.ivanolivendev.reservation.mapper.RoomMapper;
import com.ivanolivendev.reservation.service.RoomService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@Import(RoomMapper.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @Test
    void shouldCreateRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest("Sala Reuniao 01", RoomType.MEETING_ROOM, 8);
        Room room = room("Sala Reuniao 01");

        when(roomService.create(request.name(), request.type(), request.capacity())).thenReturn(room);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sala Reuniao 01"))
                .andExpect(jsonPath("$.type").value("MEETING_ROOM"))
                .andExpect(jsonPath("$.capacity").value(8))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest("", null, 0);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindAllRooms() throws Exception {
        when(roomService.findAll()).thenReturn(List.of(room("Sala Reuniao 01"), room("Auditorio Principal")));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sala Reuniao 01"))
                .andExpect(jsonPath("$[1].name").value("Auditorio Principal"));
    }

    @Test
    void shouldFindRoomById() throws Exception {
        UUID id = UUID.randomUUID();
        Room room = room(id, "Sala Reuniao 01");

        when(roomService.findById(id)).thenReturn(room);

        mockMvc.perform(get("/rooms/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Sala Reuniao 01"));
    }

    @Test
    void shouldUpdateRoom() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateRoomRequest request = new UpdateRoomRequest("Sala Atualizada", RoomType.AUDITORIUM, 80, true);
        Room room = room(id, "Sala Atualizada", RoomType.AUDITORIUM, 80, true);

        when(roomService.update(id, request.name(), request.type(), request.capacity(), request.active()))
                .thenReturn(room);

        mockMvc.perform(put("/rooms/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sala Atualizada"))
                .andExpect(jsonPath("$.type").value("AUDITORIUM"))
                .andExpect(jsonPath("$.capacity").value(80));
    }

    @Test
    void shouldDeactivateRoom() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/rooms/{id}", id))
                .andExpect(status().isNoContent());

        verify(roomService).deactivate(id);
    }

    @Test
    void shouldFindAvailableRooms() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        when(roomService.findAvailableRooms(date, startTime, endTime))
                .thenReturn(List.of(room("Sala Livre")));

        mockMvc.perform(get("/rooms/available")
                        .param("date", date.toString())
                        .param("startTime", "09:00:00")
                        .param("endTime", "10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sala Livre"));
    }

    private Room room(String name) {
        return room(UUID.randomUUID(), name);
    }

    private Room room(UUID id, String name) {
        return room(id, name, RoomType.MEETING_ROOM, 8, true);
    }

    private Room room(UUID id, String name, RoomType type, Integer capacity, Boolean active) {
        return Room.builder()
                .id(id)
                .name(name)
                .type(type)
                .capacity(capacity)
                .active(active)
                .build();
    }
}
