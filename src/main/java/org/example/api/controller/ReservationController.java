package org.example.api.controller;

import lombok.AllArgsConstructor;
import org.example.api.request.ReservationRequest;
import org.example.domain.model.DateRange;
import org.example.domain.model.Reservation;
import org.example.domain.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> reserve(@RequestBody final ReservationRequest request) {
        final Reservation reservation =
                reservationService.reserve(request.carType(), new DateRange(request.startDate(), request.endDate()));

        return ResponseEntity.ok(reservation.getCar().getId());
    }
}
