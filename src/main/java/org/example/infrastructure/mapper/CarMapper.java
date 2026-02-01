package org.example.infrastructure.mapper;

import org.example.domain.model.Car;
import org.example.infrastructure.model.CarEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
    Car toCar(CarEntity carEntity);
}
