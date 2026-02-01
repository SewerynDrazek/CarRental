package org.example.infrastructure.repository.adapter;

import lombok.AllArgsConstructor;
import org.example.domain.model.Car;
import org.example.domain.model.CarType;
import org.example.domain.model.Reservation;
import org.example.domain.repository.CarRepository;
import org.example.domain.repository.ReservationRepository;
import org.example.infrastructure.mapper.CarMapper;
import org.example.infrastructure.model.CarEntity;
import org.example.infrastructure.model.ReservationEntity;
import org.example.infrastructure.mapper.ReservationMapper;
import org.example.infrastructure.repository.jpa.JpaCarRepository;
import org.example.infrastructure.repository.jpa.JpaReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ReservationAdapter implements ReservationRepository, CarRepository {

    private final ReservationMapper reservationMapper;
    private final JpaReservationRepository jpaReservationRepository;
    private final JpaCarRepository jpaCarRepository;
    private final CarMapper carMapper;

    @Override
    public List<Reservation> findAllReservationsByCarType(CarType carType) {
        final List<ReservationEntity> reservationEntities = jpaReservationRepository.findAllByCarType(carType);

        return reservationEntities.stream().map(reservationMapper::toReservation).toList();
    }

    @Override
    public Reservation save(Reservation reservation) {
        final ReservationEntity reservationEntity = reservationMapper.toReservationEntity(reservation);
        final CarEntity carEntity = jpaCarRepository.getReferenceById(reservation.getCar().getId());
        reservationEntity.setCar(carEntity);
        jpaReservationRepository.save(reservationEntity);

        return reservation;
    }

    @Override
    public List<Car> findAllCarsByCarType(CarType carType) {
        final List<CarEntity> carEntities = jpaCarRepository.findAllByCarType(carType);

        return carEntities.stream().map(carMapper::toCar).toList();
    }
}
