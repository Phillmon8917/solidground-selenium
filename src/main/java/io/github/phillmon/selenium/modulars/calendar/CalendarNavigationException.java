package io.github.phillmon.selenium.modulars.calendar;

/**
 * Thrown when CalendarActions cannot navigate the calendar to the
 * requested month, cannot read the currently displayed month, or cannot
 * find an element it needs within the expected time.
 */
public class CalendarNavigationException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public CalendarNavigationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     *
     * @param message description of what failed
     * @param cause the original exception that triggered this failure
     */
    public CalendarNavigationException(String message, Throwable cause) {
        super(message, cause);
    }
}
