package org.example.domain.repository;

import org.example.domain.model.Car;
import org.example.domain.model.CarType;

import java.util.List;

public interface CarRepository {
    List<Car> findAllCarsByCarType(CarType carType);
}
