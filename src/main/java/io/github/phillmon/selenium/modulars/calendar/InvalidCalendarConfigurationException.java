package io.github.phillmon.selenium.modulars.calendar;

/**
 * Thrown when a CalendarLocators.Builder is used to build locators without
 * setting all the required parts, such as the next button, previous
 * button, caption, or day locator.
 */
public class InvalidCalendarConfigurationException extends IllegalStateException {
    /**
     * Creates the exception with a message describing which part of the
     * calendar configuration is missing or invalid.
     *
     * @param message description of which part of the calendar configuration is missing or invalid
     */
    public InvalidCalendarConfigurationException(String message) {
        super(message);
    }
}
