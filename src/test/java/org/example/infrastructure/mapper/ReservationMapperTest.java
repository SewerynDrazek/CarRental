package org.example.infrastructure.mapper;

import org.example.domain.model.Car;
import org.example.domain.model.CarType;
import org.example.domain.model.DateRange;
import org.example.domain.model.Reservation;
import org.example.infrastructure.model.CarEntity;
import org.example.infrastructure.model.ReservationEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationMapperTest {

    private final ReservationMapper reservationMapper = Mappers.getMapper(ReservationMapper.class);;

    @Test
     void shouldMapReservationToReservationEntity() {
        // given
        var car = new Car(1L, CarType.SUV);
        var dateRange = new DateRange(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5));
        var reservation = new Reservation(car, dateRange);

        // when
        var entity = reservationMapper.toReservationEntity(reservation);

        // then
        assertNotNull(entity);
        assertEquals(dateRange.from(), entity.getStartDate());
        assertEquals(dateRange.to(), entity.getEndDate());
        assertNull(entity.getCar(), "Car should be ignored in mapping to ReservationEntity");
    }

    @Test
    void shouldMapReservationEntityToReservation() {
        // given
        var carEntity = new CarEntity(CarType.VAN);
        carEntity.setId(2L);
        var car = new Car(2L, CarType.VAN);
        var start = LocalDate.of(2026, 3, 10);
        var end = LocalDate.of(2026, 3, 15);
        var entity = new ReservationEntity(carEntity, start, end);

        // when
        var reservation = reservationMapper.toReservation(entity);

        // then
        assertNotNull(reservation);
        assertNotNull(reservation.getDateRange());
        assertEquals(start, reservation.getDateRange().from());
        assertEquals(end, reservation.getDateRange().to());
        assertEquals(car, reservation.getCar());
    }

}