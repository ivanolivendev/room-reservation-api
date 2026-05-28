package com.ivanolivendev.reservation.controller;

import com.ivanolivendev.reservation.dto.request.CreateRoomRequest;
import com.ivanolivendev.reservation.dto.request.UpdateRoomRequest;
import com.ivanolivendev.reservation.dto.response.RoomResponse;
import com.ivanolivendev.reservation.exception.BusinessException;
import com.ivanolivendev.reservation.mapper.RoomMapper;
import com.ivanolivendev.reservation.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody CreateRoomRequest request) {
        return roomMapper.toResponse(roomService.create(request.name(), request.type(), request.capacity()));
    }

    @GetMapping
    public List<RoomResponse> findAll() {
        return roomService.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable UUID id) {
        return roomMapper.toResponse(roomService.findById(id));
    }

    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateRoomRequest request) {
        return roomMapper.toResponse(roomService.update(
                id,
                request.name(),
                request.type(),
                request.capacity(),
                request.active()
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        roomService.deactivate(id);
    }

    @GetMapping("/available")
    public List<RoomResponse> findAvailableRooms(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("Start time must be before end time");
        }

        return roomService.findAvailableRooms(date, startTime, endTime)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }
}
