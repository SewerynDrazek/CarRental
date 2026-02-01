package org.example.infrastructure.repository.jpa;

import org.example.domain.model.CarType;
import org.example.infrastructure.model.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCarRepository extends JpaRepository<CarEntity, Long> {
    List<CarEntity> findAllByCarType(CarType carType);
}
