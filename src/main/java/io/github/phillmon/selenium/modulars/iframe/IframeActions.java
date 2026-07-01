package io.github.phillmon.selenium.modulars.iframe;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Actions for switching the browser's focus into and out of iframes.
 * Keeps track of how many frames deep the browser currently is, so it
 * can switch back to the parent frame or refuse to do so when already at
 * the top-level page. Provides helper methods that run an action inside a
 * frame and automatically switch back out afterwards.
 */
public class IframeActions {
    private final WebDriver driver;
    private final Duration timeout;
    private final Deque<Object> frameStack = new ArrayDeque<>();

    /**
     * Creates the iframe actions using a default timeout of 10 seconds.
     * Expects the WebDriver for the current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public IframeActions(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    /**
     * Creates the iframe actions with a custom timeout. Expects the
     * WebDriver for the current browser session and how long to wait for
     * a frame to become available.
     *
     * @param driver  the WebDriver for the current browser session
     * @param timeout how long to wait for a frame to become available
     */
    public IframeActions(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Switches into a frame found by a locator. Expects the locator for
     * the frame element, a readable name for logging, and the name of the
     * calling method. Throws an IframeActionException if the frame does
     * not become available within the configured timeout.
     *
     * @param locator     the locator for the frame element
     * @param elementName a readable name for the frame element, used in logging
     * @param methodName  the name of the calling method, for logging
     */
    public void switchToFrame(By locator, String elementName, String methodName) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
            frameStack.push(target(elementName, locator));
            LoggerUtil.info(methodName + " switched into frame: " + target(elementName, locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IframeActionException(
                    methodName + " - frame not available within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Switches into a frame identified by its name or id attribute.
     * Expects the frame's name or id and the name of the calling method
     * for logging. Throws an IframeActionException if the frame does not
     * become available within the configured timeout.
     *
     * @param nameOrId   the frame's name or id attribute
     * @param methodName the name of the calling method, for logging
     */
    public void switchToFrame(String nameOrId, String methodName) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
            frameStack.push(nameOrId);
            LoggerUtil.info(methodName + " switched into frame: " + nameOrId);
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IframeActionException(
                    methodName + " - frame not available within " + timeout + " for name/id: " + nameOrId, e);
        }
    }

    /**
     * Switches into a frame by its position among the frames on the page,
     * starting at zero. Expects the zero-based index of the frame and the
     * name of the calling method for logging. Throws an
     * IframeActionException if the frame does not become available
     * within the configured timeout.
     *
     * @param index      the zero-based index of the frame among the frames on the page
     * @param methodName the name of the calling method, for logging
     */
    public void switchToFrame(int index, String methodName) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
            frameStack.push(index);
            LoggerUtil.info(methodName + " switched into frame at index: " + index);
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IframeActionException(
                    methodName + " - frame not available within " + timeout + " at index: " + index, e);
        }
    }

    /**
     * Switches into a frame inside another frame, one level at a time, in
     * the order given. Expects the name of the calling method for
     * logging, a list of readable names matching each locator, and the
     * locators for each frame from outermost to innermost. Throws an
     * IframeActionException if no locators are given, or if the number of
     * names does not match the number of locators.
     *
     * @param methodName   the name of the calling method, for logging
     * @param elementNames readable names matching each locator, in the same order
     * @param locators     the locators for each frame, from outermost to innermost
     */
    public void switchToNestedFrame(String methodName, List<String> elementNames, By... locators) {
        if (locators == null || locators.length == 0) {
            throw new IframeActionException(methodName + " - at least one frame locator must be provided");
        }
        if (elementNames == null || elementNames.size() != locators.length) {
            throw new IframeActionException(methodName + " - elementNames must match frame locator count");
        }
        for (int i = 0; i < locators.length; i++) {
            switchToFrame(locators[i], elementNames.get(i), methodName);
        }
        LoggerUtil.info(methodName + " switched through " + locators.length + " nested frame(s)");
    }

    /**
     * Switches the browser's focus back up to the frame that contains the
     * current frame. Expects the name of the calling method for logging.
     * Throws an IframeActionException if the browser is already at the
     * top-level document with no parent frame to go back to.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void switchToParentFrame(String methodName) {
        if (frameStack.isEmpty()) {
            throw new IframeActionException(
                    methodName + " - already at the top-level document, no parent frame to switch to");
        }
        driver.switchTo().parentFrame();
        frameStack.pop();
        LoggerUtil.info(methodName + " switched to parent frame");
    }

    /**
     * Switches the browser's focus all the way back out to the main page
     * content, leaving every frame. Expects the name of the calling
     * method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void switchToDefaultContent(String methodName) {
        driver.switchTo().defaultContent();
        frameStack.clear();
        LoggerUtil.info(methodName + " switched back to default content");
    }

    /**
     * Switches into a frame, runs the given action, and always switches
     * back out to the default content afterwards, even if the action
     * throws. Expects the locator for the frame, a readable name for
     * logging, the action to run while inside the frame, and the name of
     * the calling method. Returns whatever the action returns.
     *
     * @param <T>         the type of value returned by the action
     * @param locator     the locator for the frame to run the action in
     * @param elementName a readable name for the frame element, used in logging
     * @param action      the action to run while inside the frame
     * @param methodName  the name of the calling method, for logging
     * @return whatever the action returns
     */
    public <T> T doInFrame(By locator, String elementName, java.util.function.Supplier<T> action, String methodName) {
        switchToFrame(locator, elementName, methodName);
        try {
            return action.get();
        } finally {
            switchToDefaultContent(methodName);
        }
    }

    /**
     * Switches into a frame, runs the given action, and always switches
     * back out to the default content afterwards, even if the action
     * throws. Expects the locator for the frame, a readable name for
     * logging, the action to run while inside the frame, and the name of
     * the calling method. Use this version when the action does not need
     * to return a value.
     *
     * @param locator     the locator for the frame to run the action in
     * @param elementName a readable name for the frame element, used in logging
     * @param action      the action to run while inside the frame
     * @param methodName  the name of the calling method, for logging
     */
    public void doInFrame(By locator, String elementName, Runnable action, String methodName) {
        switchToFrame(locator, elementName, methodName);
        try {
            action.run();
        } finally {
            switchToDefaultContent(methodName);
        }
    }

    /**
     * Returns whether the browser is currently focused inside any frame.
     *
     * @return true if the browser is focused inside at least one frame, false if it is at the top-level page
     */
    public boolean isInsideFrame() {
        return !frameStack.isEmpty();
    }

    /**
     * Returns how many frames deep the browser is currently focused,
     * where zero means the top-level page.
     *
     * @return the number of frames deep the browser is currently focused, or zero if at the top-level page
     */
    public int getCurrentFrameDepth() {
        return frameStack.size();
    }

    /**
     * Returns whether a frame matching the given locator exists on the
     * page right now, without waiting for it. Expects the locator for the
     * frame, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the frame element
     * @param elementName a readable name for the frame element, used in logging
     * @param methodName  the name of the calling method, for logging
     * @return true if a matching frame is present on the page right now, false otherwise
     */
    public boolean isFramePresent(By locator, String elementName, String methodName) {
        boolean present = !driver.findElements(locator).isEmpty();
        LoggerUtil.info(methodName + " checked frame presence for " + target(elementName, locator) + ": " + present);
        return present;
    }

    /**
     * Returns the total number of iframe and frame elements currently on
     * the page. Expects the name of the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     * @return the total number of iframe and frame elements currently on the page
     */
    public int countFrames(String methodName) {
        int count = driver.findElements(By.tagName("iframe")).size()
                + driver.findElements(By.tagName("frame")).size();
        LoggerUtil.info(methodName + " found frame count: " + count);
        return count;
    }

    /**
     * Builds a readable description of an element combining its name and
     * its locator, used in log and exception messages.
     */
    private String target(String elementName, By locator) {
        return elementName + " (" + locator + ")";
    }
}
