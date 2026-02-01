package org.example.domain.service;

import lombok.AllArgsConstructor;
import org.example.domain.exception.CarNotFoundException;
import org.example.domain.model.Car;
import org.example.domain.model.CarType;
import org.example.domain.model.DateRange;
import org.example.domain.model.Reservation;
import org.example.domain.repository.CarRepository;
import org.example.domain.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Reservation reserve(CarType carType, DateRange dateRange) {
        final List<Car> cars = carRepository.findAllCarsByCarType(carType);

        if (cars.isEmpty()) {
            throw new CarNotFoundException();
        }

        final List<Long> reservedCarIds = reservationRepository.findAllReservationsByCarType(carType)
                .stream()
                .filter(reservation -> dateRange.overlaps(reservation.getDateRange()))
                .map(reservation -> reservation.getCar().getId()).toList();

        final Car car = cars.stream()
                .filter(c -> !reservedCarIds.contains(c.getId()))
                .findAny()
                .orElseThrow(CarNotFoundException::new);

        return reservationRepository.save(new Reservation(car, dateRange));
    }
}
