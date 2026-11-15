package com.library.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Checklist item 4: fine = Rs 5 per day late. One place only. */
@Component
public class FineCalculator {

    public static final double FINE_PER_DAY = 5.0;

    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate == null || !returnDate.isAfter(dueDate)) {
            return 0;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * FINE_PER_DAY;
    }
}
