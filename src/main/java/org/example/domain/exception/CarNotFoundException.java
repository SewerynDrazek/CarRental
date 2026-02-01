package org.example.domain.exception;

import lombok.Getter;

@Getter
public class CarNotFoundException extends RuntimeException {

    private static final String CAR_NOT_FOUND_MESSAGE = "Car not found";

    public CarNotFoundException() {
        super(CAR_NOT_FOUND_MESSAGE);
    }
}
