package com.library.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class FineCalculator {

    public static final double FINE_PER_DAY = 5.0;

    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        LocalDate actualReturn = (returnDate == null) ? LocalDate.now() : returnDate;
        if (dueDate == null || !actualReturn.isAfter(dueDate)) {
            return 0.0;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, actualReturn);
        return daysLate * FINE_PER_DAY;
    }

    public double calculateFineForToday(LocalDate dueDate) {
        return calculateFine(dueDate, LocalDate.now());
    }
}
