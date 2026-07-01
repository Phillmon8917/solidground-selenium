package io.github.phillmon.selenium.modulars.browser;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Actions that work on the whole browser rather than a single element:
 * navigating pages, reading the current url and title, switching between
 * windows, tabs, and iframes, taking screenshots, and waiting for the page
 * to finish loading. Every action logs what it did through LoggerUtil.
 */
public class BrowserActions {
    private final WebDriver driver;

    /**
     * Creates the browser actions for the given driver. Expects the
     * WebDriver for the current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public BrowserActions(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Navigates the browser to the given url. Expects the url to open and
     * the name of the calling method for logging.
     *
     * @param url        the url to open
     * @param methodName the name of the calling method, for logging
     */
    public void navigateToUrl(String url, String methodName) {
        driver.get(url);
        LoggerUtil.info(methodName + " navigated to: " + url);
    }

    /**
     * Refreshes the current page. Expects the name of the calling method
     * for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void refreshPage(String methodName) {
        driver.navigate().refresh();
        LoggerUtil.info(methodName + " refreshed the page");
    }

    /**
     * Navigates back one page in the browser's history. Expects the name
     * of the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void navigateBack(String methodName) {
        driver.navigate().back();
        LoggerUtil.info(methodName + " navigated back");
    }

    /**
     * Navigates forward one page in the browser's history. Expects the
     * name of the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void navigateForward(String methodName) {
        driver.navigate().forward();
        LoggerUtil.info(methodName + " navigated forward");
    }

    /**
     * Returns the url of the page currently open in the browser.
     *
     * @return the url of the page currently open in the browser
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        LoggerUtil.info("Current url: " + url);
        return url;
    }

    /**
     * Returns the title of the page currently open in the browser.
     *
     * @return the title of the page currently open in the browser
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        LoggerUtil.info("Page title: " + title);
        return title;
    }

    /**
     * Waits until the page has fully finished loading, checked by reading
     * the document.readyState property from the browser. Expects the
     * maximum time to wait. Throws a timeout exception if the page has not
     * finished loading within that time.
     *
     * @param timeout the maximum time to wait for the page to finish loading
     */
    public void waitForDocumentReady(Duration timeout) {
        new WebDriverWait(driver, timeout).until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );

        LoggerUtil.info("Document is ready (state: complete)");
    }

    /**
     * Returns the handle of the window that is currently active. Expects
     * the name of the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     * @return the handle of the window that is currently active
     */
    public String getCurrentWindowHandle(String methodName) {
        String handle = driver.getWindowHandle();
        LoggerUtil.info(methodName + " found current window handle: " + handle);
        return handle;
    }

    /**
     * Returns the handles of every window or tab currently open in this
     * browser session. Expects the name of the calling method for
     * logging.
     *
     * @param methodName the name of the calling method, for logging
     * @return the handles of every window or tab currently open in this browser session
     */
    public List<String> getAllWindowHandles(String methodName) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        LoggerUtil.info(methodName + " found " + handles.size() + " window handles");
        return handles;
    }

    /**
     * Switches the browser's focus to the window with the given handle.
     * Expects the handle to switch to and the name of the calling method
     * for logging.
     *
     * @param handle     the handle of the window to switch to
     * @param methodName the name of the calling method, for logging
     */
    public void switchToWindow(String handle, String methodName) {
        driver.switchTo().window(handle);
        LoggerUtil.info(methodName + " switched to window handle: " + handle);
    }

    /**
     * Switches the browser's focus to the window at the given position in
     * the list of open windows. Expects the zero-based index of the
     * window and the name of the calling method for logging. Throws an
     * IndexOutOfBoundsException if the index does not match any open
     * window.
     *
     * @param index      the zero-based index of the window to switch to
     * @param methodName the name of the calling method, for logging
     */
    public void switchToWindowByIndex(int index, String methodName) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (index < 0 || index >= handles.size()) {
            throw new IndexOutOfBoundsException("Window index " + index + " out of range");
        }
        driver.switchTo().window(handles.get(index));
        LoggerUtil.info(methodName + " switched to window index: " + index);
    }

    /**
     * Waits for a new window or tab to appear and switches the browser's
     * focus to it. Expects how long to wait for the new window to appear
     * and the name of the calling method for logging. Throws an
     * IllegalStateException if no new window is found once the wait
     * finishes.
     */
    private void switchToNewWindow(Duration timeout, String methodName) {
        String originalHandle = driver.getWindowHandle();

        new WebDriverWait(driver, timeout).until(driver ->
                driver.getWindowHandles().size() > 1
        );

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                LoggerUtil.info(methodName + " switched to new window handle: " + handle);
                return;
            }
        }

        throw new IllegalStateException(methodName + " - no new window found to switch to");
    }

    /**
     * Opens a new, empty browser tab and switches focus to it. Expects
     * how long to wait for the tab to open and the name of the calling
     * method for logging.
     *
     * @param duration   how long to wait for the new tab to open
     * @param methodName the name of the calling method, for logging
     */
    public void openNewTab(Duration duration, String methodName) {
        ((JavascriptExecutor) driver).executeScript("window.open()");
        switchToNewWindow(duration, methodName);
        LoggerUtil.info(methodName + " opened and switched to new tab");
    }

    /**
     * Closes the tab or window that currently has focus. Expects the name
     * of the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void closeCurrentTab(String methodName) {
        driver.close();
        LoggerUtil.info(methodName + " closed current tab");
    }

    /**
     * Switches the browser's focus into an iframe identified by its name
     * or id attribute. Expects the frame's name or id, a readable name for
     * the frame for logging, and the name of the calling method.
     *
     * @param frameNameOrId the frame's name or id attribute
     * @param elementName   a readable name for the frame, for logging
     * @param methodName    the name of the calling method, for logging
     */
    public void switchToFrame(String frameNameOrId, String elementName, String methodName) {
        driver.switchTo().frame(frameNameOrId);
        LoggerUtil.info(methodName + " switched to frame: " + elementName + " (" + frameNameOrId + ")");
    }

    /**
     * Switches the browser's focus back out of any iframe to the main
     * page content. Expects the name of the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void switchToDefaultContent(String methodName) {
        driver.switchTo().defaultContent();
        LoggerUtil.info(methodName + " switched back to default content");
    }

    /**
     * Takes a screenshot of the current browser window and returns it as
     * raw image bytes. Expects the name of the calling method for
     * logging.
     *
     * @param methodName the name of the calling method, for logging
     * @return the screenshot as raw image bytes
     */
    public byte[] takeScreenshot(String methodName) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        LoggerUtil.info(methodName + " captured screenshot");
        return screenshot;
    }

    /**
     * Takes a screenshot of the current browser window and saves it to
     * the given file path. Expects the destination file path and the name
     * of the calling method for logging. Returns the same file path that
     * was passed in. Throws a RuntimeException if the screenshot cannot
     * be saved to that path.
     *
     * @param filePath   the destination file path to save the screenshot to
     * @param methodName the name of the calling method, for logging
     * @return the same file path that was passed in
     */
    public String takeScreenshot(String filePath, String methodName) {
        File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(sourceFile.toPath(), Path.of(filePath));
        } catch (Exception e) {
            throw new RuntimeException(methodName + " - failed to save screenshot to: " + filePath, e);
        }
        LoggerUtil.info(methodName + " captured screenshot and saved to: " + filePath);
        return filePath;
    }

    /**
     * Maximizes the current browser window. Expects the name of the
     * calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void maximizeWindow(String methodName) {
        driver.manage().window().maximize();
        LoggerUtil.info(methodName + " maximized window");
    }
}
