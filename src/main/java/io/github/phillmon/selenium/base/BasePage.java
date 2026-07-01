package io.github.phillmon.selenium.base;

import org.openqa.selenium.WebDriver;

/**
 * Base class that every page object in the project should extend.
 * It holds the WebDriver instance for the page and gives the page object
 * access to every action group (buttons, dropdowns, waits, and so on) through
 * the modulars field, so a page object does not need to create these itself.
 */
public abstract class BasePage {
    /** The WebDriver instance controlling the browser session for this page. */
    protected final WebDriver driver;
    /** The container giving access to every action group available to this page. */
    protected final ActionsContainer modulars;

    /**
     * Creates the page object using the default set of options for every
     * action group. Use this constructor unless the page needs custom
     * behaviour such as a different calendar layout or download folder.
     * Expects a non-null WebDriver that is already pointed at the browser
     * session the page object will control.
     *
     * @param driver the WebDriver instance for the browser session this page object will control
     */
    protected BasePage(WebDriver driver) {
        this(driver, PageModularOptions.defaults());
    }

    /**
     * Creates the page object with custom options for the action groups.
     * Expects a non-null WebDriver and a non-null PageModularOptions object.
     * Throws an IllegalArgumentException if the driver is null, since a page
     * object cannot do anything useful without a browser session.
     *
     * @param driver  the WebDriver instance for the browser session this page object will control
     * @param options the custom options to apply to every action group
     */
    protected BasePage(WebDriver driver, PageModularOptions options) {
        if (driver == null) {
            throw new IllegalArgumentException("driver must not be null");
        }

        this.driver = driver;
        this.modulars = new ActionsContainer(driver, options);
    }
}
