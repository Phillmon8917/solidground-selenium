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
    public final AssertionActions assertionActions;
    public final BrowserActions browserActions;
    public final CalendarActions calendarActions;
    public final SelectDropdownActions dropdownActions;
    public final ElementActions elementActions;
    public final IframeActions iframeActions;
    public final KeyboardActions keyboardActions;
    public final LoadingIndicatorActions loadingIndicatorActions;
    public final MouseActions mouseActions;
    public final NetworkValidationActions networkActions;
    public final ReaderActionsContainer reader;
    public final SmartWaitActions waitActions;
    public final ToggleActions toggleActions;

    /**
     * Creates every action group using the default options. Expects a
     * non-null WebDriver that is already attached to a browser session.
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
