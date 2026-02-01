package org.example.infrastructure.repository.adapter;

import org.example.domain.model.Car;
import org.example.domain.model.CarType;
import org.example.domain.model.DateRange;
import org.example.domain.model.Reservation;
import org.example.infrastructure.mapper.CarMapper;
import org.example.infrastructure.mapper.ReservationMapper;
import org.example.infrastructure.model.CarEntity;
import org.example.infrastructure.model.ReservationEntity;
import org.example.infrastructure.repository.jpa.JpaCarRepository;
import org.example.infrastructure.repository.jpa.JpaReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationAdapterTest {

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private CarMapper carMapper;

    @Mock
    private JpaReservationRepository jpaReservationRepository;

    @Mock
    private JpaCarRepository jpaCarRepository;

    @InjectMocks
    private ReservationAdapter reservationAdapter;

    @Test
    void shouldFindAllReservationsByCarType() {
        //given
        var carType = CarType.SUV;
        var startDate = LocalDate.now();
        var endDate = startDate.plusDays(1);
        var endDate2 = startDate.plusDays(2);
        var reservationEntity1 = new ReservationEntity(new CarEntity(CarType.SUV), startDate, endDate);
        var reservationEntity2 = new ReservationEntity(new CarEntity(CarType.SUV), startDate, endDate2);

        when(jpaReservationRepository.findAllByCarType(carType)).thenReturn(List.of(reservationEntity1, reservationEntity2));

        var r1 = new Reservation(new Car(1L, carType), new DateRange(startDate, endDate)); // dummy Reservation
        var r2 = new Reservation(new Car(2L, carType), new DateRange(startDate, endDate2));

        when(reservationMapper.toReservation(reservationEntity1)).thenReturn(r1);
        when(reservationMapper.toReservation(reservationEntity2)).thenReturn(r2);

        //when:
        var result = reservationAdapter.findAllReservationsByCarType(carType);

        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(r1));
        assertTrue(result.contains(r2));

        verify(jpaReservationRepository).findAllByCarType(carType);
        verify(reservationMapper).toReservation(reservationEntity1);
        verify(reservationMapper).toReservation(reservationEntity2);
    }

    @Test
    void shouldSaveReservation() {
        //given:
        var car = new Car(1L, CarType.SUV);
        var reservation = new Reservation(car, new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        var entity = new ReservationEntity(new CarEntity(CarType.SUV), reservation.getDateRange().from(), reservation.getDateRange().to());
        var carEntity = new CarEntity(CarType.SUV);
        carEntity.setId(1L);
        when(reservationMapper.toReservationEntity(reservation)).thenReturn(entity);
        when(jpaCarRepository.getReferenceById(1L)).thenReturn(carEntity);

        //when
        var result = reservationAdapter.save(reservation);

        //then:
        assertEquals(reservation, result);
        assertEquals(carEntity, entity.getCar());
        verify(jpaReservationRepository).save(entity);
    }

    @Test
    void shouldFindAllCarsByCarType() {
        //given:
        var carType = CarType.VAN;
        var carEntity1 = new CarEntity(CarType.VAN);
        carEntity1.setId(1L);
        var carEntity2 = new CarEntity(CarType.VAN);
        carEntity2.setId(2L);

        when(jpaCarRepository.findAllByCarType(carType)).thenReturn(List.of(carEntity1, carEntity2));

        var car1 = new Car(1L, CarType.VAN);
        var car2 = new Car(2L, CarType.VAN);

        when(carMapper.toCar(carEntity1)).thenReturn(car1);
        when(carMapper.toCar(carEntity2)).thenReturn(car2);

        //when:
        var result = reservationAdapter.findAllCarsByCarType(carType);

        //then:
        assertEquals(2, result.size());
        assertTrue(result.contains(car1));
        assertTrue(result.contains(car2));

        verify(jpaCarRepository).findAllByCarType(carType);
        verify(carMapper).toCar(carEntity1);
        verify(carMapper).toCar(carEntity2);
    }

}