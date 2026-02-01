package org.example.domain.service;

import org.example.domain.exception.CarNotFoundException;
import org.example.domain.model.Car;
import org.example.domain.model.CarType;
import org.example.domain.model.DateRange;
import org.example.domain.model.Reservation;
import org.example.domain.repository.CarRepository;
import org.example.domain.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private final CarType carType = CarType.SUV;
    private final DateRange dateRange =
            new DateRange(LocalDate.now(),
                    LocalDate.now().plusDays(5));

    @Test
    void shouldThrowExceptionWhenNoCarsFound() {
        // given
        when(carRepository.findAllCarsByCarType(carType))
                .thenReturn(List.of());

        // when / then
        assertThrows(
                CarNotFoundException.class,
                () -> reservationService.reserve(carType, dateRange)
        );

        verify(carRepository).findAllCarsByCarType(carType);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldThrowExceptionWhenAllCarsAreReserved() {
        // given
        var car1 = new Car(1L, carType);
        var car2 = new Car(2L, carType);

        when(carRepository.findAllCarsByCarType(carType))
                .thenReturn(List.of(car1, car2));

        var r1 = mockReservation(car1, dateRange);
        var r2 = mockReservation(car2, dateRange);

        when(reservationRepository.findAllReservationsByCarType(carType))
                .thenReturn(List.of(r1, r2));

        // when / then
        assertThrows(
                CarNotFoundException.class,
                () -> reservationService.reserve(carType, dateRange)
        );

        verify(reservationRepository, never()).save(any());
    }


    @Test
    void shouldReserveFreeCarSuccessfully() {
        // given
        var car1 = new Car(1L, carType);
        var car2 = new Car(2L, carType);

        when(carRepository.findAllCarsByCarType(carType))
                .thenReturn(List.of(car1, car2));

        var reservation = mockReservation(car1, dateRange);

        when(reservationRepository.findAllReservationsByCarType(carType))
                .thenReturn(List.of(reservation));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        var result =
                reservationService.reserve(carType, dateRange);

        // then
        assertNotNull(result);
        assertEquals(car2, result.getCar());
        assertEquals(dateRange, result.getDateRange());

        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldIgnoreReservationsThatDoNotOverlap() {
        // given
        var car = new Car(1L, carType);

        when(carRepository.findAllCarsByCarType(carType))
                .thenReturn(List.of(car));

        var otherRange =
                new DateRange(
                        LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(7)
                );

        var oldReservation =
                mockReservation(car, otherRange);

        when(reservationRepository.findAllReservationsByCarType(carType))
                .thenReturn(List.of(oldReservation));

        when(reservationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        // when
        var result =
                reservationService.reserve(carType, dateRange);

        // then
        assertEquals(car, result.getCar());
        verify(reservationRepository).save(any());
    }

    private Reservation mockReservation(Car car, DateRange range) {
        return new Reservation(car, range);
    }
}