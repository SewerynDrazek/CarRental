package org.example.domain.repository;

import org.example.domain.model.CarType;
import org.example.domain.model.Reservation;

import java.util.List;

public interface ReservationRepository {
    List<Reservation> findAllReservationsByCarType(CarType carType);
    Reservation save(Reservation reservation);
}
