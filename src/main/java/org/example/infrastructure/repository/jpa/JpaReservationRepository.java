package org.example.infrastructure.repository.jpa;

import org.example.domain.model.CarType;
import org.example.infrastructure.model.ReservationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaReservationRepository extends JpaRepository<ReservationEntity, Long> {
    @EntityGraph(attributePaths = "car")
    @Query("SELECT r FROM ReservationEntity r WHERE r.car.carType = :carType")
    List<ReservationEntity> findAllByCarType(@Param("carType") CarType carType);
}
