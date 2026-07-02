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
    private static final ThreadLocal<SoftAssert> THREAD_SOFT_ASSERT = ThreadLocal.withInitial(SoftAssert::new);

    private final CalendarLocators calendarLocators;
    private final ToggleStateResolver toggleStateResolver;
    private final SoftAssert softAssert;
    private final Path downloadDirectory;
    private final Duration downloadTimeout;
    private final Duration elementTimeout;
    private final Duration iframeTimeout;
    private final Duration mouseTimeout;
    private final Duration keyboardTimeout;
    private final Duration dropdownTimeout;
    private final Duration waitTimeout;

    private PageModularOptions(Builder builder) {
        this.calendarLocators = builder.calendarLocators;
        this.toggleStateResolver = builder.toggleStateResolver;
        this.softAssert = builder.softAssert;
        this.downloadDirectory = builder.downloadDirectory;
        this.downloadTimeout = builder.downloadTimeout;
        this.elementTimeout = builder.elementTimeout;
        this.iframeTimeout = builder.iframeTimeout;
        this.mouseTimeout = builder.mouseTimeout;
        this.keyboardTimeout = builder.keyboardTimeout;
        this.dropdownTimeout = builder.dropdownTimeout;
        this.waitTimeout = builder.waitTimeout;
    }

    /**
     * Returns the SoftAssert instance shared by every page object on the
     * current thread that was not given an explicit SoftAssert through
     * the builder. Exposed so a test itself (which typically does not
     * hold a page object directly) can call assertAll() on the same
     * SoftAssert instance its page objects have been recording into.
     *
     * @return the SoftAssert instance shared by default on the current thread
     */
    public static SoftAssert currentThreadSoftAssert() {
        return THREAD_SOFT_ASSERT.get();
    }

    /**
     * Replaces the SoftAssert instance shared by default on the current
     * thread with a brand new one. Test runners commonly reuse the same
     * pool of threads across many test methods, so without calling this
     * at the start of each test (for example from a TestNG
     * {@code @BeforeMethod}), soft assertion failures recorded by one
     * test could still be sitting unflushed when the next test on that
     * same thread starts. Has no effect on any PageModularOptions built
     * with an explicit SoftAssert supplied through the builder.
     */
    public static void resetCurrentThreadSoftAssert() {
        THREAD_SOFT_ASSERT.set(new SoftAssert());
    }

    /**
     * Returns a configuration with every setting left at its default value.
     * Use this when a page object does not need any custom behaviour.
     *
     * @return a PageModularOptions instance with every setting at its default value
     */
    public static PageModularOptions defaults() {
        return builder().build();
    }

    /**
     * Returns a new Builder so a caller can set only the options it cares
     * about and leave the rest at their defaults.
     *
     * @return a new Builder for configuring a PageModularOptions instance
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
     * Returns the timeout the element actions should use, or null to use
     * that module's own default.
     */
    Duration getElementTimeout() {
        return elementTimeout;
    }

    /**
     * Returns the timeout the iframe actions should use, or null to use
     * that module's own default.
     */
    Duration getIframeTimeout() {
        return iframeTimeout;
    }

    /**
     * Returns the timeout the mouse actions should use, or null to use
     * that module's own default.
     */
    Duration getMouseTimeout() {
        return mouseTimeout;
    }

    /**
     * Returns the timeout the keyboard actions should use, or null to use
     * that module's own default.
     */
    Duration getKeyboardTimeout() {
        return keyboardTimeout;
    }

    /**
     * Returns the timeout the dropdown actions should use, or null to use
     * that module's own default.
     */
    Duration getDropdownTimeout() {
        return dropdownTimeout;
    }

    /**
     * Returns the timeout the smart wait actions should use, or null to
     * use that module's own default.
     */
    Duration getWaitTimeout() {
        return waitTimeout;
    }

    /**
     * Returns the SoftAssert instance to use for assertions. If none was
     * supplied through the builder, this returns the SoftAssert shared by
     * default on the current thread (see currentThreadSoftAssert()), so
     * every page object built with default options on the same thread
     * records into the same SoftAssert instance, and a single assertAll()
     * call sees soft failures recorded through any of them.
     */
    SoftAssert createSoftAssert() {
        return softAssert != null ? softAssert : currentThreadSoftAssert();
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
        private Duration elementTimeout;
        private Duration iframeTimeout;
        private Duration mouseTimeout;
        private Duration keyboardTimeout;
        private Duration dropdownTimeout;
        private Duration waitTimeout;

        /**
         * Creates a builder pre-populated with the default calendar
         * locators and toggle state resolver, and no download directory,
         * download timeout, or SoftAssert configured yet.
         */
        public Builder() {
        }

        /**
         * Sets the locators the calendar actions should use instead of the
         * default day picker locators. Expects a non-null CalendarLocators
         * and throws an IllegalArgumentException if null is passed in.
         *
         * @param calendarLocators the locators the calendar actions should use
         * @return this builder, so calls can be chained
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
         *
         * @param toggleStateResolver the strategy used to read a toggle element's on/off state
         * @return this builder, so calls can be chained
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
         *
         * @param softAssert the SoftAssert instance the assertion actions should share
         * @return this builder, so calls can be chained
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
         *
         * @param downloadDirectory the folder that downloaded documents should be saved into
         * @return this builder, so calls can be chained
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
         *
         * @param downloadTimeout how long to wait for a download to finish before giving up
         * @return this builder, so calls can be chained
         */
        public Builder withDownloadTimeout(Duration downloadTimeout) {
            if (downloadTimeout == null) {
                throw new IllegalArgumentException("downloadTimeout must not be null");
            }
            this.downloadTimeout = downloadTimeout;
            return this;
        }

        /**
         * Sets how long the element actions should wait for an element to
         * reach the expected state, instead of that module's own default.
         * Expects a non-null Duration and throws an
         * IllegalArgumentException if null is passed in.
         *
         * @param elementTimeout how long the element actions should wait
         * @return this builder, so calls can be chained
         */
        public Builder withElementTimeout(Duration elementTimeout) {
            if (elementTimeout == null) {
                throw new IllegalArgumentException("elementTimeout must not be null");
            }
            this.elementTimeout = elementTimeout;
            return this;
        }

        /**
         * Sets how long the iframe actions should wait, instead of that
         * module's own default. Expects a non-null Duration and throws an
         * IllegalArgumentException if null is passed in.
         *
         * @param iframeTimeout how long the iframe actions should wait
         * @return this builder, so calls can be chained
         */
        public Builder withIframeTimeout(Duration iframeTimeout) {
            if (iframeTimeout == null) {
                throw new IllegalArgumentException("iframeTimeout must not be null");
            }
            this.iframeTimeout = iframeTimeout;
            return this;
        }

        /**
         * Sets how long the mouse actions should wait, instead of that
         * module's own default. Expects a non-null Duration and throws an
         * IllegalArgumentException if null is passed in.
         *
         * @param mouseTimeout how long the mouse actions should wait
         * @return this builder, so calls can be chained
         */
        public Builder withMouseTimeout(Duration mouseTimeout) {
            if (mouseTimeout == null) {
                throw new IllegalArgumentException("mouseTimeout must not be null");
            }
            this.mouseTimeout = mouseTimeout;
            return this;
        }

        /**
         * Sets how long the keyboard actions should wait, instead of that
         * module's own default. Expects a non-null Duration and throws an
         * IllegalArgumentException if null is passed in.
         *
         * @param keyboardTimeout how long the keyboard actions should wait
         * @return this builder, so calls can be chained
         */
        public Builder withKeyboardTimeout(Duration keyboardTimeout) {
            if (keyboardTimeout == null) {
                throw new IllegalArgumentException("keyboardTimeout must not be null");
            }
            this.keyboardTimeout = keyboardTimeout;
            return this;
        }

        /**
         * Sets how long the dropdown actions should wait, instead of that
         * module's own default. Expects a non-null Duration and throws an
         * IllegalArgumentException if null is passed in.
         *
         * @param dropdownTimeout how long the dropdown actions should wait
         * @return this builder, so calls can be chained
         */
        public Builder withDropdownTimeout(Duration dropdownTimeout) {
            if (dropdownTimeout == null) {
                throw new IllegalArgumentException("dropdownTimeout must not be null");
            }
            this.dropdownTimeout = dropdownTimeout;
            return this;
        }

        /**
         * Sets how long the smart wait actions should wait, instead of
         * that module's own default. Expects a non-null Duration and
         * throws an IllegalArgumentException if null is passed in.
         *
         * @param waitTimeout how long the smart wait actions should wait
         * @return this builder, so calls can be chained
         */
        public Builder withWaitTimeout(Duration waitTimeout) {
            if (waitTimeout == null) {
                throw new IllegalArgumentException("waitTimeout must not be null");
            }
            this.waitTimeout = waitTimeout;
            return this;
        }

        /**
         * Creates the final, immutable PageModularOptions using whatever
         * settings were configured on this builder.
         *
         * @return the immutable PageModularOptions built from this builder's settings
         */
        public PageModularOptions build() {
            return new PageModularOptions(this);
        }
    }
}
