package org.example.infrastructure.mapper;

import org.example.domain.model.CarType;
import org.example.infrastructure.model.CarEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CarMapperTest {

    private final CarMapper carMapper = Mappers.getMapper(CarMapper.class);;

    @Test
    void shouldMapCarEntityToCar() {
        // given
        var entity = new CarEntity(CarType.SUV);
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setRentals(Collections.emptyList());

        // when
        var car = carMapper.toCar(entity);

        // then
        assertNotNull(car);
        assertEquals(entity.getId(), car.getId());
        assertEquals(entity.getCarType(), car.getCarType());
    }

}