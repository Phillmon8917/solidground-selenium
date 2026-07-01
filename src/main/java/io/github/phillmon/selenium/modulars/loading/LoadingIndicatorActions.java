package io.github.phillmon.selenium.modulars.loading;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Actions for waiting on loading indicators such as spinners, skeleton
 * placeholders, and page loaders to appear and disappear. Pages often show
 * one of these while data is loading, so tests need to wait for them to
 * go away before checking the real content underneath.
 */
public class LoadingIndicatorActions {
    private static final By DEFAULT_SPINNER = By.cssSelector("#websiteSearchLoadIcon");
    private static final By DEFAULT_SKELETON = By.cssSelector(".skeleton");

    private final WebDriver driver;
    private final java.time.Duration timeout;

    /**
     * Creates the loading indicator actions using a default timeout of 50
     * seconds. Expects the WebDriver for the current browser session.
     */
    public LoadingIndicatorActions(WebDriver driver) {
        this(driver, java.time.Duration.ofSeconds(50));
    }

    /**
     * Creates the loading indicator actions with a custom timeout.
     * Expects the WebDriver for the current browser session and how long
     * to wait for a loading indicator to disappear.
     */
    public LoadingIndicatorActions(WebDriver driver, java.time.Duration timeout) {
        this.driver = driver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Waits for the default page loader to disappear. Expects the name of
     * the calling method for logging.
     */
    public void waitForPageLoader(String methodName) {
        waitForHidden(DEFAULT_SPINNER, "page loader", methodName, "page loader");
    }

    /**
     * Waits for the default spinner element to disappear. Expects the
     * name of the calling method for logging.
     */
    public void waitForSpinner(String methodName) {
        waitForHidden(DEFAULT_SPINNER, "spinner", methodName, "spinner");
    }

    /**
     * Waits for a custom spinner element to disappear. Expects the
     * locator for the spinner, a readable name for logging, and the name
     * of the calling method.
     */
    public void waitForSpinner(By locator, String elementName, String methodName) {
        waitForHidden(locator, elementName, methodName, "spinner");
    }

    /**
     * Waits for the default skeleton placeholder to disappear. Expects
     * the name of the calling method for logging.
     */
    public void waitForSkeleton(String methodName) {
        waitForHidden(DEFAULT_SKELETON, "skeleton loader", methodName, "skeleton loader");
    }

    /**
     * Waits for a custom skeleton placeholder to disappear. Expects the
     * locator for the skeleton element, a readable name for logging, and
     * the name of the calling method.
     */
    public void waitForSkeleton(By locator, String elementName, String methodName) {
        waitForHidden(locator, elementName, methodName, "skeleton loader");
    }

    /**
     * Waits for any loading indicator to disappear. Expects the locator
     * for the indicator, a readable name for logging, and the name of the
     * calling method.
     */
    public void waitForToDisappear(By locator, String elementName, String methodName) {
        waitForHidden(locator, elementName, methodName, "loading indicator");
    }

    /**
     * Waits for a loading indicator to appear first, then waits for it to
     * disappear again. If the indicator never appears within the given
     * appearance timeout, this simply logs that fact and returns without
     * waiting for a disappearance that will never happen. Expects the
     * locator for the indicator, how long to wait for it to appear, a
     * readable name for logging, and the name of the calling method.
     */
    public void waitForToAppearThenDisappear(By locator, java.time.Duration appearanceTimeout, String elementName, String methodName) {
        java.time.Duration adjustedAppearanceTimeout = TimeoutUtil.adjust(appearanceTimeout);
        boolean appeared = waitForAppearance(locator, adjustedAppearanceTimeout, elementName, methodName);
        if (!appeared) {
            LoggerUtil.info(methodName + " loading indicator never appeared within "
                    + adjustedAppearanceTimeout + ", skipping disappearance wait for: " + target(elementName, locator));
            return;
        }
        waitForHidden(locator, elementName, methodName, "loading indicator");
    }

    /**
     * Waits for several loading indicators to disappear, one after
     * another. Expects the name of the calling method for logging, a
     * list of readable names matching each locator, and the locators for
     * each indicator. Throws a LoadingIndicatorException if no locators
     * are given, or if the number of names does not match the number of
     * locators.
     */
    public void waitForAllToDisappear(String methodName, List<String> elementNames, By... locators) {
        if (locators == null || locators.length == 0) {
            throw new LoadingIndicatorException(methodName + " - at least one locator must be provided");
        }
        if (elementNames == null || elementNames.size() != locators.length) {
            throw new LoadingIndicatorException(methodName + " - elementNames must match locator count");
        }
        for (int i = 0; i < locators.length; i++) {
            waitForHidden(locators[i], elementNames.get(i), methodName, "loading indicator");
        }
        LoggerUtil.info(methodName + " confirmed all " + locators.length + " loading indicator(s) disappeared");
    }

    /**
     * Returns whether a loading indicator is currently visible on the
     * page right now, without waiting. Expects the locator for the
     * indicator, a readable name for logging, and the name of the calling
     * method.
     */
    public boolean isLoading(By locator, String elementName, String methodName) {
        boolean loading = !driver.findElements(locator).isEmpty()
                && driver.findElement(locator).isDisplayed();
        LoggerUtil.info(methodName + " checked loading state for " + target(elementName, locator) + ": " + loading);
        return loading;
    }

    /**
     * Waits for a loading indicator to become visible, within the given
     * appearance timeout. Expects the locator for the indicator, how long
     * to wait, a readable name for logging, and the name of the calling
     * method. Returns true if the indicator appeared in time, or false if
     * the wait timed out instead of throwing, since not appearing at all
     * is a valid outcome for this check.
     */
    private boolean waitForAppearance(By locator, java.time.Duration appearanceTimeout, String elementName, String methodName) {
        try {
            new WebDriverWait(driver, appearanceTimeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            LoggerUtil.info(methodName + " observed loading indicator appear: " + target(elementName, locator));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits until an indicator element is gone from the page or no longer
     * displayed. Expects the locator for the indicator, a readable name
     * for logging, the name of the calling method, and a short
     * description of what kind of indicator this is, used in log and
     * error messages. Throws a LoadingIndicatorException if the indicator
     * is still visible once the configured timeout runs out.
     */
    private void waitForHidden(By locator, String elementName, String methodName, String description) {
        try {
            new WebDriverWait(driver, timeout).until(webDriver -> {
                java.util.List<org.openqa.selenium.WebElement> elements = driver.findElements(locator);
                return elements.isEmpty() || !elements.get(0).isDisplayed();
            });
            LoggerUtil.info(methodName + " - " + description + " disappeared: " + target(elementName, locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new LoadingIndicatorException(
                    methodName + " - " + description + " did not disappear within " + timeout
                            + " for: " + target(elementName, locator), e);
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
