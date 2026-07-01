package io.github.phillmon.selenium.base;

import io.github.phillmon.selenium.modulars.assertions.AssertionActions;
import io.github.phillmon.selenium.modulars.browser.BrowserActions;
import io.github.phillmon.selenium.modulars.calendar.CalendarActions;
import io.github.phillmon.selenium.modulars.dropdown.SelectDropdownActions;
import io.github.phillmon.selenium.modulars.element.ElementActions;
import io.github.phillmon.selenium.modulars.iframe.IframeActions;
import io.github.phillmon.selenium.modulars.keyboard.KeyboardActions;
import io.github.phillmon.selenium.modulars.loading.LoadingIndicatorActions;
import io.github.phillmon.selenium.modulars.mouse.MouseActions;
import io.github.phillmon.selenium.modulars.network.NetworkValidationActions;
import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;
import io.github.phillmon.selenium.modulars.toggle.ToggleActions;
import io.github.phillmon.selenium.modulars.wait.SmartWaitActions;
import org.openqa.selenium.WebDriver;

/**
 * Builds and holds one instance of every action group that a page object can
 * use, such as clicking elements, handling dropdowns, waiting for loaders,
 * and reading documents. A page object gets one ActionsContainer through
 * BasePage and calls into these fields instead of talking to Selenium
 * directly, so the same tested action code is shared across every page.
 */
public class ActionsContainer {
    /** Assertions and soft assertions with logging built in. */
    public final AssertionActions assertionActions;
    /** Whole-browser actions: navigation, windows, tabs, screenshots. */
    public final BrowserActions browserActions;
    /** Driving date-picker calendar widgets. */
    public final CalendarActions calendarActions;
    /** Native HTML {@code <select>} dropdowns. */
    public final SelectDropdownActions dropdownActions;
    /** The main element actions: click, type, read text/attributes, wait. */
    public final ElementActions elementActions;
    /** Switching into and out of iframes. */
    public final IframeActions iframeActions;
    /** Keyboard input and shortcuts. */
    public final KeyboardActions keyboardActions;
    /** Waiting for spinners, skeletons, and page loaders to disappear. */
    public final LoadingIndicatorActions loadingIndicatorActions;
    /** Mouse gestures: hover, drag and drop, scrolling. */
    public final MouseActions mouseActions;
    /** Verifying real network requests and responses via Chrome DevTools. */
    public final NetworkValidationActions networkActions;
    /** Downloading and reading Excel, PDF, text, and Word documents. */
    public final ReaderActionsContainer reader;
    /** Waiting for a value to stop changing (stabilize). */
    public final SmartWaitActions waitActions;
    /** Switch/checkbox style toggle elements. */
    public final ToggleActions toggleActions;

    /**
     * Creates every action group using the default options. Expects a
     * non-null WebDriver that is already attached to a browser session.
     *
     * @param driver the WebDriver to build every action group with
     */
    public ActionsContainer(WebDriver driver) {
        this(driver, PageModularOptions.defaults());
    }

    /**
     * Creates every action group using the given options, so callers can
     * customise things like the calendar locators, the toggle state
     * resolver, or the download folder used by the reader actions. Expects
     * a non-null WebDriver and a non-null PageModularOptions object, and
     * throws an IllegalArgumentException when either one is missing.
     *
     * @param driver  the WebDriver to build every action group with
     * @param options the options used to customise the action groups
     */
    public ActionsContainer(WebDriver driver, PageModularOptions options) {
        if (driver == null) {
            throw new IllegalArgumentException("driver must not be null");
        }
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }

        DocumentDownloader downloader = options.createDocumentDownloader(driver);

        this.assertionActions = new AssertionActions(options.createSoftAssert());
        this.browserActions = new BrowserActions(driver);
        this.calendarActions = new CalendarActions(driver, options.getCalendarLocators());
        this.dropdownActions = new SelectDropdownActions(driver);
        this.elementActions = new ElementActions(driver);
        this.iframeActions = new IframeActions(driver);
        this.keyboardActions = new KeyboardActions(driver);
        this.loadingIndicatorActions = new LoadingIndicatorActions(driver);
        this.mouseActions = new MouseActions(driver);
        this.networkActions = new NetworkValidationActions(driver);
        this.reader = new ReaderActionsContainer(downloader);
        this.waitActions = new SmartWaitActions(driver);
        this.toggleActions = new ToggleActions(driver, options.getToggleStateResolver());
    }
}
