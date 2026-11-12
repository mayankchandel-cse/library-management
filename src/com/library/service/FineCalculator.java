package com.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * FineCalculator — late-return fines.
 *
 * <p>Rule: <b>Rs 5 per day late</b>. Returning on or before the due date
 * costs nothing. There is no maximum fine in this version (a cap would be
 * one line: {@code Math.min(fine, MAX_FINE)}).</p>
 */
public final class FineCalculator {

    /** Fine charged per day late, in rupees. */
    public static final double FINE_PER_DAY = 5.0;

    private FineCalculator() { /* utility class — no instances */ }

    /**
     * Fine for returning on {@code returned} when the book was due on
     * {@code due}. Returns 0 if the book is on time (or early), or if
     * either date is missing.
     */
    public static double calculateFine(LocalDate due, LocalDate returned) {
        if (due == null || returned == null) return 0.0;
        long daysLate = ChronoUnit.DAYS.between(due, returned);
        if (daysLate <= 0) return 0.0;
        return daysLate * FINE_PER_DAY;
    }

    /** True if the book is overdue on the given date. */
    public static boolean isOverdue(LocalDate due, LocalDate today) {
        return due != null && today != null && today.isAfter(due);
    }
}
