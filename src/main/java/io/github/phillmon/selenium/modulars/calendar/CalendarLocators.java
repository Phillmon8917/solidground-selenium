package io.github.phillmon.selenium.modulars.calendar;

import org.openqa.selenium.By;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

/**
 * Describes where to find the parts of a calendar widget on the page: the
 * next and previous month buttons, the caption that shows the current
 * month and year, and a way to build a locator for any given day. Build
 * one with the Builder class, or use defaultDayPicker for a calendar that
 * follows a common day-picker layout.
 */
public class CalendarLocators {
    private final By nextButton;
    private final By previousButton;
    private final By caption;
    private final DateTimeFormatter captionFormatter;
    private final Function<DateObject, By> dayLocator;

    private CalendarLocators(Builder builder) {
        this.nextButton = builder.nextButton;
        this.previousButton = builder.previousButton;
        this.caption = builder.caption;
        this.captionFormatter = builder.captionFormatter;
        this.dayLocator = builder.dayLocator;
    }

    /**
     * Returns the locator for the button that moves the calendar forward
     * one month.
     */
    public By getNextButton() {
        return nextButton;
    }

    /**
     * Returns the locator for the button that moves the calendar back one
     * month.
     */
    public By getPreviousButton() {
        return previousButton;
    }

    /**
     * Returns the locator for the caption element that displays the month
     * and year currently shown on the calendar.
     */
    public By getCaption() {
        return caption;
    }

    /**
     * Returns the formatter used to read the month and year out of the
     * caption's text.
     */
    public DateTimeFormatter getCaptionFormatter() {
        return captionFormatter;
    }

    /**
     * Builds the locator for the button that represents the given date on
     * the calendar. Expects the date to build a locator for. Returns the
     * locator that CalendarActions should click to select that day.
     */
    public By dayLocatorFor(DateObject date) {
        return dayLocator.apply(date);
    }

    /**
     * Returns a new Builder for creating a custom CalendarLocators.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a ready-made CalendarLocators for a common day-picker
     * layout, where the next and previous buttons are identified by their
     * aria-label, the caption is a status element, and each day button
     * has a data-day attribute formatted as month/day/year.
     */
    public static CalendarLocators defaultDayPicker() {
        DateTimeFormatter dayAttributeFormat = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
        return builder()
                .withNextButton(By.xpath("//button[contains(@aria-label,'Next Month')]"))
                .withPreviousButton(By.xpath("//button[contains(@aria-label,'Previous Month')]"))
                .withCaption(By.xpath("//span[@role='status']"))
                .withCaptionFormat("MMMM yyyy")
                .withDayLocator(date -> By.cssSelector(
                        "button[data-day='" + date.toLocalDate().format(dayAttributeFormat) + "']"))
                .build();
    }

    /**
     * Fluent builder for CalendarLocators. Call the with methods to
     * describe each part of the calendar widget, then call build() to
     * create the final CalendarLocators instance.
     */
    public static class Builder {
        private By nextButton;
        private By previousButton;
        private By caption;
        private DateTimeFormatter captionFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        private Function<DateObject, By> dayLocator;

        /**
         * Sets the locator for the next month button. Expects a locator
         * that finds that button on the page.
         */
        public Builder withNextButton(By nextButton) {
            this.nextButton = nextButton;
            return this;
        }

        /**
         * Sets the locator for the previous month button. Expects a
         * locator that finds that button on the page.
         */
        public Builder withPreviousButton(By previousButton) {
            this.previousButton = previousButton;
            return this;
        }

        /**
         * Sets the locator for the caption element showing the currently
         * displayed month and year. Expects a locator that finds that
         * element on the page.
         */
        public Builder withCaption(By caption) {
            this.caption = caption;
            return this;
        }

        /**
         * Sets the date pattern used to read the month and year from the
         * caption text, instead of the default "MMMM yyyy" pattern.
         * Expects a valid date-time pattern string.
         */
        public Builder withCaptionFormat(String pattern) {
            this.captionFormatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
            return this;
        }

        /**
         * Sets the function used to build a locator for any given day.
         * Expects a function that takes a DateObject and returns the
         * locator for the button representing that day on the calendar.
         */
        public Builder withDayLocator(Function<DateObject, By> dayLocator) {
            this.dayLocator = dayLocator;
            return this;
        }

        /**
         * Creates the final CalendarLocators instance. Throws an
         * InvalidCalendarConfigurationException if the next button,
         * previous button, caption, or day locator was never set, since
         * CalendarActions cannot work without all of them.
         */
        public CalendarLocators build() {
            if (nextButton == null || previousButton == null || caption == null || dayLocator == null) {
                throw new InvalidCalendarConfigurationException(
                        "nextButton, previousButton, caption, and dayLocator must all be provided");
            }
            return new CalendarLocators(this);
        }
    }
}
