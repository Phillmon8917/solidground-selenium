package io.github.phillmon.selenium.base;

import io.github.phillmon.selenium.modulars.calendar.CalendarLocators;
import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;
import io.github.phillmon.selenium.modulars.toggle.ToggleStateResolver;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Holds the settings that control how the action groups in ActionsContainer
 * behave, such as which locators the calendar uses, how a toggle's checked
 * state is read, which SoftAssert instance to share, and where downloaded
 * documents should be saved. Build one with the Builder class, or call
 * defaults() to get a plain configuration with no customisation.
 */
public class PageModularOptions {
    private final CalendarLocators calendarLocators;
    private final ToggleStateResolver toggleStateResolver;
    private final SoftAssert softAssert;
    private final Path downloadDirectory;
    private final Duration downloadTimeout;

    private PageModularOptions(Builder builder) {
        this.calendarLocators = builder.calendarLocators;
        this.toggleStateResolver = builder.toggleStateResolver;
        this.softAssert = builder.softAssert;
        this.downloadDirectory = builder.downloadDirectory;
        this.downloadTimeout = builder.downloadTimeout;
    }

    /**
     * Returns a configuration with every setting left at its default value.
     * Use this when a page object does not need any custom behaviour.
     */
    public static PageModularOptions defaults() {
        return builder().build();
    }

    /**
     * Returns a new Builder so a caller can set only the options it cares
     * about and leave the rest at their defaults.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the locators the calendar actions should use to find days,
     * months, and years in a date picker.
     */
    CalendarLocators getCalendarLocators() {
        return calendarLocators;
    }

    /**
     * Returns the strategy used to work out whether a toggle element (such
     * as a checkbox styled as a switch) is currently on or off.
     */
    ToggleStateResolver getToggleStateResolver() {
        return toggleStateResolver;
    }

    /**
     * Returns the SoftAssert instance to use for assertions. If none was
     * supplied through the builder, this creates a new one so every page
     * object still gets a working SoftAssert.
     */
    SoftAssert createSoftAssert() {
        return softAssert != null ? softAssert : new SoftAssert();
    }

    /**
     * Builds a DocumentDownloader for the given driver, using whichever
     * combination of download directory and timeout was configured.
     * When neither was set, the downloader is created with its own
     * defaults. When only a directory was set, that directory is used with
     * the downloader's default timeout. When a timeout was set but no
     * directory was chosen, the system temp folder is used instead.
     */
    DocumentDownloader createDocumentDownloader(WebDriver driver) {
        if (downloadDirectory == null && downloadTimeout == null) {
            return new DocumentDownloader(driver);
        }

        if (downloadTimeout == null) {
            return new DocumentDownloader(driver, downloadDirectory);
        }

        Path directory = downloadDirectory != null
                ? downloadDirectory
                : Path.of(System.getProperty("java.io.tmpdir"));
        return new DocumentDownloader(driver, directory, downloadTimeout);
    }

    /**
     * Fluent builder for PageModularOptions. Call the with methods for only
     * the settings that need to change, then call build() to create the
     * final, immutable PageModularOptions instance.
     */
    public static class Builder {
        private CalendarLocators calendarLocators = CalendarLocators.defaultDayPicker();
        private ToggleStateResolver toggleStateResolver = ToggleStateResolver.ariaChecked();
        private SoftAssert softAssert;
        private Path downloadDirectory;
        private Duration downloadTimeout;

        /**
         * Sets the locators the calendar actions should use instead of the
         * default day picker locators. Expects a non-null CalendarLocators
         * and throws an IllegalArgumentException if null is passed in.
         */
        public Builder withCalendarLocators(CalendarLocators calendarLocators) {
            if (calendarLocators == null) {
                throw new IllegalArgumentException("calendarLocators must not be null");
            }
            this.calendarLocators = calendarLocators;
            return this;
        }

        /**
         * Sets the strategy used to read whether a toggle element is on or
         * off, instead of the default aria-checked based strategy. Expects
         * a non-null ToggleStateResolver and throws an
         * IllegalArgumentException if null is passed in.
         */
        public Builder withToggleStateResolver(ToggleStateResolver toggleStateResolver) {
            if (toggleStateResolver == null) {
                throw new IllegalArgumentException("toggleStateResolver must not be null");
            }
            this.toggleStateResolver = toggleStateResolver;
            return this;
        }

        /**
         * Sets the SoftAssert instance the assertion actions should share,
         * instead of letting a new one be created automatically. Expects a
         * non-null SoftAssert and throws an IllegalArgumentException if
         * null is passed in.
         */
        public Builder withSoftAssert(SoftAssert softAssert) {
            if (softAssert == null) {
                throw new IllegalArgumentException("softAssert must not be null");
            }
            this.softAssert = softAssert;
            return this;
        }

        /**
         * Sets the folder that downloaded documents should be saved into.
         * Expects a non-null Path and throws an IllegalArgumentException if
         * null is passed in.
         */
        public Builder withDownloadDirectory(Path downloadDirectory) {
            if (downloadDirectory == null) {
                throw new IllegalArgumentException("downloadDirectory must not be null");
            }
            this.downloadDirectory = downloadDirectory;
            return this;
        }

        /**
         * Sets how long the reader actions should wait for a download to
         * finish before giving up. Expects a non-null Duration and throws
         * an IllegalArgumentException if null is passed in.
         */
        public Builder withDownloadTimeout(Duration downloadTimeout) {
            if (downloadTimeout == null) {
                throw new IllegalArgumentException("downloadTimeout must not be null");
            }
            this.downloadTimeout = downloadTimeout;
            return this;
        }

        /**
         * Creates the final, immutable PageModularOptions using whatever
         * settings were configured on this builder.
         */
        public PageModularOptions build() {
            return new PageModularOptions(this);
        }
    }
}
