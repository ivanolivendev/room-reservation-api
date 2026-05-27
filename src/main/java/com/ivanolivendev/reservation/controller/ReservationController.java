package com.ivanolivendev.reservation.controller;

import com.ivanolivendev.reservation.dto.request.CreateReservationRequest;
import com.ivanolivendev.reservation.dto.response.ReservationResponse;
import com.ivanolivendev.reservation.mapper.ReservationMapper;
import com.ivanolivendev.reservation.service.ReservationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody CreateReservationRequest request) {
        return reservationMapper.toResponse(reservationService.create(
                request.roomId(),
                request.date(),
                request.startTime(),
                request.endTime(),
                request.responsibleName()
        ));
    }

    @GetMapping
    public List<ReservationResponse> findAll() {
        return reservationService.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable UUID id) {
        return reservationMapper.toResponse(reservationService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ReservationResponse cancel(@PathVariable UUID id) {
        return reservationMapper.toResponse(reservationService.cancel(id));
    }

    @GetMapping("/daily")
    public List<ReservationResponse> findDailySchedule(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reservationService.findDailySchedule(date)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }
}
