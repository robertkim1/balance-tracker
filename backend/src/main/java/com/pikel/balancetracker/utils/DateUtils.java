package com.pikel.balancetracker.utils;

import java.time.LocalDate;

public class DateUtils {

    private DateUtils() {}

    // used for semimonthly calculation
    public static LocalDate findNext1stOr15th(LocalDate date) {
        int dayOfMonth = date.getDayOfMonth();

        if (dayOfMonth < 15) {
            // next occurrence is the 15th of current month
            return date.withDayOfMonth(15);
        } else {
            // next occurrence is the 1st of next month
            return date.plusMonths(1).withDayOfMonth(1);
        }
    }

    // TODO: check business day and adjust date
}
