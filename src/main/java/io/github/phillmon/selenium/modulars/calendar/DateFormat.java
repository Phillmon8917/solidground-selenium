package io.github.phillmon.selenium.modulars.calendar;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A fixed set of common date patterns that DateObject can format to and
 * parse from, so callers can pick a well known format by name instead of
 * writing out a pattern string each time.
 */
public enum DateFormat {
    /** Long form date such as "January 5, 2026". */
    LONG("MMMM d, yyyy"),
    /** Short numeric date separated by slashes, such as "01/05/2026". */
    SHORT_SLASH("MM/dd/yyyy"),
    /** Short numeric date separated by dashes, such as "01-05-2026". */
    SHORT_DASH("MM-dd-yyyy"),
    /** ISO-8601 date such as "2026-01-05". */
    ISO("yyyy-MM-dd"),
    /** Day, month name, and year, such as "5 January 2026". */
    DAY_MONTH_YEAR("dd MMMM yyyy"),
    /** Abbreviated month date such as "Jan 5, 2026". */
    ABBREVIATED("MMM d, yyyy");

    private final DateTimeFormatter formatter;

    DateFormat(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
    }

    /**
     * Returns the DateTimeFormatter that matches this date format's
     * pattern, ready to use for parsing or formatting a date.
     *
     * @return the DateTimeFormatter configured for this date format's pattern
     */
    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
