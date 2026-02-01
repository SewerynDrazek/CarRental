package org.example.api.request;

import org.example.domain.model.CarType;

import java.time.LocalDate;

public record ReservationRequest(CarType carType, LocalDate startDate, LocalDate endDate) {
}
