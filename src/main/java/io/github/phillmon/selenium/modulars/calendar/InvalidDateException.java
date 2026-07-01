package io.github.phillmon.selenium.modulars.calendar;

/**
 * Thrown when DateObject is given a date value it cannot accept, such as
 * text that cannot be parsed, a year outside the supported range, or a
 * day, month, and year combination that does not form a real date.
 */
public class InvalidDateException extends IllegalArgumentException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     */
    public InvalidDateException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     */
    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
