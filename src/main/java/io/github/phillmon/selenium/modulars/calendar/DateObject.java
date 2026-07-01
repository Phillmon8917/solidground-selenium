package io.github.phillmon.selenium.modulars.calendar;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a single calendar date used across the calendar actions.
 * It wraps a LocalDate but only allows years within a supported range,
 * and gives several ways to create a date: from a LocalDate, from a
 * formatted date string, or by building it up from a day, month, and
 * year one at a time. It can also format itself back to text using a
 * custom pattern or one of the DateFormat presets.
 */
public class DateObject {
    private static final DateTimeFormatter DEFAULT_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    private final LocalDate date;

    /**
     * Returns the day of the month, for example 15 for the 15th.
     */
    public int getDay() {
        return date.getDayOfMonth();
    }

    /**
     * Returns the name of the month, for example "JANUARY".
     */
    public String getMonth() {
        return date.getMonth().toString();
    }

    /**
     * Returns the month as a number, where 1 is January and 12 is
     * December.
     */
    public int getMonthValue() {
        return date.getMonthValue();
    }

    /**
     * Returns the four digit year.
     */
    public int getYear() {
        return date.getYear();
    }

    /**
     * Returns this date as a plain java.time.LocalDate.
     */
    public LocalDate toLocalDate() {
        return date;
    }

    private DateObject(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns a new Builder for constructing a DateObject one field at a
     * time from a day, month name, and year.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a DateObject from an existing LocalDate. Expects a date
     * whose year falls within the supported range. Throws an
     * InvalidDateException if the year is outside that range.
     */
    public static DateObject fromLocalDate(LocalDate date) {
        validateYear(date.getYear());
        return new DateObject(date);
    }

    /**
     * Parses a date string using the default "MMMM d, yyyy" pattern, for
     * example "January 15, 2026". Expects the date text to parse. Throws
     * an InvalidDateException if the text is blank or cannot be parsed.
     */
    public static DateObject fromString(String dateText) {
        return fromString(dateText, DEFAULT_FORMAT);
    }

    /**
     * Parses a date string using a custom pattern. Expects the date text
     * and a valid date-time pattern string. Throws an
     * InvalidDateException if the text is blank, the pattern is invalid,
     * or the text does not match the pattern.
     */
    public static DateObject fromString(String dateText, String pattern) {
        return fromString(dateText, toFormatter(pattern));
    }

    /**
     * Parses a date string using one of the DateFormat presets. Expects
     * the date text and the preset format to parse it with. Throws an
     * InvalidDateException if the text is blank or does not match the
     * chosen format.
     */
    public static DateObject fromString(String dateText, DateFormat format) {
        return fromString(dateText, format.getFormatter());
    }

    /**
     * Parses a date string using an already built DateTimeFormatter.
     * Expects the date text and the formatter to parse it with. Throws an
     * InvalidDateException if the text is null, blank, cannot be parsed
     * with the formatter, or parses to a year outside the supported
     * range.
     */
    public static DateObject fromString(String dateText, DateTimeFormatter formatter) {
        if (dateText == null || dateText.isBlank()) {
            throw new InvalidDateException("Date text must not be null or blank");
        }
        try {
            LocalDate parsed = LocalDate.parse(dateText.trim(), formatter);
            validateYear(parsed.getYear());
            return new DateObject(parsed);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(
                    "Invalid date format. Could not parse: '" + dateText + "'", e);
        }
    }

    /**
     * Formats this date using a custom pattern string. Expects a valid
     * date-time pattern. Throws an InvalidDateException if the pattern is
     * not valid.
     */
    public String format(String pattern) {
        return date.format(toFormatter(pattern));
    }

    /**
     * Formats this date using one of the DateFormat presets. Expects the
     * preset format to use.
     */
    public String format(DateFormat format) {
        return date.format(format.getFormatter());
    }

    /**
     * Builds a DateTimeFormatter from a pattern string. Expects a
     * date-time pattern. Throws an InvalidDateException if the pattern is
     * not a valid pattern.
     */
    private static DateTimeFormatter toFormatter(String pattern) {
        try {
            return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        } catch (IllegalArgumentException e) {
            throw new InvalidDateException("Invalid date pattern provided: " + pattern, e);
        }
    }

    /**
     * Builds a DateObject one field at a time from a day, a month name,
     * and a year, instead of parsing a full date string.
     */
    public static class Builder {
        private Integer day;
        private Month month;
        private Integer year;

        /**
         * Sets the day of the month. Expects a day number, which is only
         * validated once build() combines it with the month and year.
         */
        public Builder withDay(int day) {
            this.day = day;
            return this;
        }

        /**
         * Sets the month by name, for example "January" or "JANUARY".
         * Expects a month name that matches one of the twelve calendar
         * months, ignoring case. Throws an InvalidDateException if the
         * text does not match a real month.
         */
        public Builder withMonth(String month) {
            this.month = parseMonth(month);
            return this;
        }

        /**
         * Sets the year. Expects a year within the supported range.
         * Throws an InvalidDateException if the year is outside that
         * range.
         */
        public Builder withYear(int year) {
            validateYear(year);
            this.year = year;
            return this;
        }

        /**
         * Combines the day, month, and year set so far into a DateObject.
         * Throws an InvalidDateException if any of the day, month, or
         * year was never set, or if the combination does not form a real
         * calendar date, such as February 30th.
         */
        public DateObject build() {
            if (day == null || month == null || year == null) {
                throw new InvalidDateException("Day, month, and year must all be provided");
            }
            try {
                return new DateObject(LocalDate.of(year, month, day));
            } catch (DateTimeException e) {
                throw new InvalidDateException(
                        "Invalid date: " + month + " " + day + ", " + year, e);
            }
        }

        /**
         * Converts a month name into a java.time.Month value. Expects a
         * month name, ignoring case and surrounding whitespace. Throws an
         * InvalidDateException if the text does not match a real month.
         */
        private static Month parseMonth(String month) {
            try {
                return Month.valueOf(month.trim().toUpperCase(Locale.ENGLISH));
            } catch (Exception e) {
                throw new InvalidDateException("Invalid month provided: " + month, e);
            }
        }
    }

    /**
     * Checks that a year falls within the range this framework supports
     * (1940 to 2080). Throws an InvalidDateException if the year is
     * outside that range.
     */
    private static void validateYear(int year) {
        if (year < 1940 || year > 2080) {
            throw new InvalidDateException("We currently don't support dates beyond this range: " + year);
        }
    }

    /**
     * Returns true when the other object is a DateObject representing the
     * same calendar date.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateObject)) return false;
        DateObject other = (DateObject) o;
        return date.equals(other.date);
    }

    /**
     * Returns a hash code consistent with equals, based on the wrapped
     * date.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    /**
     * Returns this date formatted using the default "MMMM d, yyyy"
     * pattern, for example "January 15, 2026".
     */
    @Override
    public String toString() {
        return date.format(DEFAULT_FORMAT);
    }
}
