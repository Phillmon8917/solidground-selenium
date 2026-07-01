package io.github.phillmon.selenium.modulars.calendar;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A fixed set of common date patterns that DateObject can format to and
 * parse from, so callers can pick a well known format by name instead of
 * writing out a pattern string each time.
 */
public enum DateFormat {
    LONG("MMMM d, yyyy"),
    SHORT_SLASH("MM/dd/yyyy"),
    SHORT_DASH("MM-dd-yyyy"),
    ISO("yyyy-MM-dd"),
    DAY_MONTH_YEAR("dd MMMM yyyy"),
    ABBREVIATED("MMM d, yyyy");

    private final DateTimeFormatter formatter;

    DateFormat(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
    }

    /**
     * Returns the DateTimeFormatter that matches this date format's
     * pattern, ready to use for parsing or formatting a date.
     */
    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
