package io.github.phillmon.selenium.modulars.toggle;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Actions for working with toggle-style elements, such as switches and
 * checkboxes, that can be turned on or off. Uses a ToggleStateResolver to
 * decide whether an element currently counts as on, since different
 * widgets show that state in different ways.
 */
public class ToggleActions {
    private final WebDriver driver;
    private final Duration timeout;
    private final ToggleStateResolver resolver;

    /**
     * Creates the toggle actions using a default timeout of 10 seconds.
     * Expects the WebDriver for the current browser session and the
     * resolver used to read whether a toggle is on.
     *
     * @param driver   the WebDriver for the current browser session
     * @param resolver the resolver used to read whether a toggle is on
     */
    public ToggleActions(WebDriver driver, ToggleStateResolver resolver) {
        this(driver, resolver, Duration.ofSeconds(10));
    }

    /**
     * Creates the toggle actions with a custom timeout. Expects the
     * WebDriver for the current browser session, the resolver used to
     * read whether a toggle is on, and how long to wait for the toggle to
     * become visible.
     *
     * @param driver   the WebDriver for the current browser session
     * @param resolver the resolver used to read whether a toggle is on
     * @param timeout  how long to wait for the toggle to become visible
     */
    public ToggleActions(WebDriver driver, ToggleStateResolver resolver, Duration timeout) {
        this.driver = driver;
        this.resolver = resolver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Makes sure a toggle ends up on, clicking it only if it is currently
     * off. Expects the locator for the toggle, a readable name for
     * logging, and the name of the calling method. Throws a
     * ToggleActionException if the toggle still reads as off after being
     * clicked.
     *
     * @param locator     the locator for the toggle
     * @param elementName a readable name for the toggle, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void ensureToggledOn(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        boolean isOn = resolver.isOn(element);

        if (!isOn) {
            element.click();
            WebElement afterClick = waitForVisible(locator, elementName, methodName);
            boolean confirmedOn = resolver.isOn(afterClick);
            if (!confirmedOn) {
                throw new ToggleActionException(methodName + " - " + target(elementName, locator) + " did not turn on after clicking");
            }
        }

        LoggerUtil.info(methodName + " - " + target(elementName, locator) + " is on");
    }

    /**
     * Makes sure a toggle ends up off, clicking it only if it is
     * currently on. Expects the locator for the toggle, a readable name
     * for logging, and the name of the calling method. Throws a
     * ToggleActionException if the toggle still reads as on after being
     * clicked.
     *
     * @param locator     the locator for the toggle
     * @param elementName a readable name for the toggle, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void ensureToggledOff(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        boolean isOn = resolver.isOn(element);

        if (isOn) {
            element.click();
            WebElement afterClick = waitForVisible(locator, elementName, methodName);
            boolean stillOn = resolver.isOn(afterClick);
            if (stillOn) {
                throw new ToggleActionException(methodName + " - " + target(elementName, locator) + " did not turn off after clicking");
            }
        }

        LoggerUtil.info(methodName + " - " + target(elementName, locator) + " is off");
    }

    /**
     * Asserts that a toggle is currently on, without clicking it. Expects
     * the locator for the toggle, a readable name for logging, and the
     * name of the calling method. Throws a ToggleActionException if the
     * toggle is off.
     *
     * @param locator     the locator for the toggle
     * @param elementName a readable name for the toggle, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void assertToggledOn(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        if (!resolver.isOn(element)) {
            throw new ToggleActionException(methodName + " - " + target(elementName, locator) + " is expected to be on but is off");
        }
        LoggerUtil.info(methodName + " - " + target(elementName, locator) + " is confirmed on");
    }

    /**
     * Asserts that a toggle is currently off, without clicking it.
     * Expects the locator for the toggle, a readable name for logging,
     * and the name of the calling method. Throws a ToggleActionException
     * if the toggle is on.
     *
     * @param locator     the locator for the toggle
     * @param elementName a readable name for the toggle, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void assertToggledOff(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        if (resolver.isOn(element)) {
            throw new ToggleActionException(methodName + " - " + target(elementName, locator) + " is expected to be off but is on");
        }
        LoggerUtil.info(methodName + " - " + target(elementName, locator) + " is confirmed off");
    }

    /**
     * Returns whether a toggle is currently on, without changing it or
     * failing the test either way. Expects the locator for the toggle, a
     * readable name for logging, and the name of the calling method.
     *
     * @param locator     the locator for the toggle
     * @param elementName a readable name for the toggle, for logging
     * @param methodName  the name of the calling method, for logging
     * @return true if the toggle currently reads as on, false otherwise
     */
    public boolean isOn(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        boolean isOn = resolver.isOn(element);
        LoggerUtil.info(methodName + " found toggle state for " + target(elementName, locator) + ", isOn: " + isOn);
        return isOn;
    }

    /**
     * Waits until the toggle element is visible on the page. Expects the
     * locator for the toggle, a readable name for logging, and the name
     * of the calling method. Returns the visible element. Throws a
     * ToggleActionException if the toggle does not become visible within
     * the configured timeout.
     */
    private WebElement waitForVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ToggleActionException(
                    methodName + " - toggle not visible within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Builds a readable description of an element combining its name and
     * its locator, used in log and exception messages.
     */
    private String target(String elementName, By locator) {
        return elementName + " (" + locator + ")";
    }
}
