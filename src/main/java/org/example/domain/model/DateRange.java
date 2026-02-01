package org.example.domain.model;

import org.example.domain.exception.DateOutOfRangeException;

import java.time.LocalDate;

public record DateRange(LocalDate from, LocalDate to) {

    public DateRange {
        if (from.isBefore(LocalDate.now()) || !from.isBefore(to))
            throw new DateOutOfRangeException();
    }

    public boolean overlaps(DateRange other) {
        return !(to.isBefore(other.from) || from.isAfter(other.to));
    }
}
