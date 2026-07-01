package io.github.phillmon.selenium.modulars.calendar;

import java.time.LocalDate;

/**
 * Small helper for getting a DateObject relative to today's date, so
 * tests can select dates like "today" or "seven days from now" without
 * building a LocalDate by hand.
 */
public final class DateOffset {

    private DateOffset() {
    }

    /**
     * Returns the date that is the given number of days away from today.
     * Expects the number of days to add, which can be negative to get a
     * date in the past.
     */
    public static DateObject resolve(int offsetDays) {
        LocalDate target = LocalDate.now().plusDays(offsetDays);
        return DateObject.fromLocalDate(target);
    }

    /**
     * Returns today's date as a DateObject.
     */
    public static DateObject today() {
        return resolve(0);
    }
}
