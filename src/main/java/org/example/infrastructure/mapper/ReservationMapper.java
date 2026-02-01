package org.example.infrastructure.mapper;

import org.example.domain.model.Reservation;
import org.example.infrastructure.model.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "car", ignore = true)
    @Mapping(target = "startDate", source = "dateRange.from")
    @Mapping(target = "endDate", source = "dateRange.to")
    ReservationEntity toReservationEntity(Reservation reservation);

    @Mapping(target = "dateRange", expression = "java(new DateRange(reservationEntity.getStartDate(), reservationEntity.getEndDate()))")
    Reservation toReservation(ReservationEntity reservationEntity);
}
