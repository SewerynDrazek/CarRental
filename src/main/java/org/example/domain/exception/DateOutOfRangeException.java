package org.example.domain.exception;

public class DateOutOfRangeException extends RuntimeException {
    private static final String MESSAGE = "Date is out of range";

    public DateOutOfRangeException() {
        super(MESSAGE);
    }
}