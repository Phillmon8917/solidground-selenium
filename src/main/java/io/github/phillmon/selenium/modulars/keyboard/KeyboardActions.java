package io.github.phillmon.selenium.modulars.keyboard;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Actions for sending keyboard input, either to a specific element or to
 * the page as a whole. Covers single keys, key combinations such as
 * Ctrl+C, common shortcuts like copy, paste, and undo, arrow key
 * navigation, and holding a modifier key down while clicking.
 */
public class KeyboardActions {
    private final WebDriver driver;
    private final Duration timeout;

    /**
     * Creates the keyboard actions using a default timeout of 10 seconds.
     * Expects the WebDriver for the current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public KeyboardActions(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    /**
     * Creates the keyboard actions with a custom timeout. Expects the
     * WebDriver for the current browser session and how long to wait for
     * the target element to become visible.
     *
     * @param driver  the WebDriver for the current browser session
     * @param timeout how long to wait for the target element to become visible
     */
    public KeyboardActions(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Sends a single key press to an element. Expects the locator for the
     * element, the key to press, a readable name for logging, and the
     * name of the calling method.
     *
     * @param locator     the locator for the element to send the key to
     * @param key         the key to press
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void pressKey(By locator, Keys key, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.sendKeys(key);
        LoggerUtil.info(methodName + " pressed key " + key + " on element: " + target(elementName, locator));
    }

    /**
     * Sends several key presses to an element, one after another in the
     * order given. Expects the locator for the element, a readable name
     * for logging, the name of the calling method, and the keys to press.
     *
     * @param locator     the locator for the element to send the keys to
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     * @param keys        the keys to press, in order
     */
    public void pressKeys(By locator, String elementName, String methodName, Keys... keys) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        for (Keys key : keys) {
            element.sendKeys(key);
        }
        LoggerUtil.info(methodName + " pressed keys " + java.util.Arrays.toString(keys) + " on element: " + target(elementName, locator));
    }

    /**
     * Sends a key combination such as Ctrl+A to an element, holding the
     * modifier key down while pressing the other key. Expects the locator
     * for the element, the modifier key to hold, the other key in the
     * combination, a readable name for logging, and the name of the
     * calling method.
     *
     * @param locator     the locator for the element to send the chord to
     * @param modifier    the modifier key to hold down
     * @param key         the other key in the combination
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void pressChord(By locator, Keys modifier, String key, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.sendKeys(Keys.chord(modifier, key));
        LoggerUtil.info(methodName + " pressed chord " + modifier + "+" + key + " on element: " + target(elementName, locator));
    }

    /**
     * Presses the Enter key on an element. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press Enter on
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void pressEnter(By locator, String elementName, String methodName) {
        pressKey(locator, Keys.ENTER, elementName, methodName);
    }

    /**
     * Presses the Tab key on an element. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press Tab on
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void pressTab(By locator, String elementName, String methodName) {
        pressKey(locator, Keys.TAB, elementName, methodName);
    }

    /**
     * Presses the Escape key on an element. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press Escape on
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void pressEscape(By locator, String elementName, String methodName) {
        pressKey(locator, Keys.ESCAPE, elementName, methodName);
    }

    /**
     * Selects all the text in an element using Ctrl+A. Expects the
     * locator for the element, a readable name for logging, and the name
     * of the calling method.
     *
     * @param locator     the locator for the element to select all text in
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectAll(By locator, String elementName, String methodName) {
        pressChord(locator, Keys.CONTROL, "a", elementName, methodName);
        LoggerUtil.info(methodName + " selected all text on element: " + target(elementName, locator));
    }

    /**
     * Copies the current selection in an element using Ctrl+C. Expects
     * the locator for the element, a readable name for logging, and the
     * name of the calling method.
     *
     * @param locator     the locator for the element to copy from
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void copy(By locator, String elementName, String methodName) {
        pressChord(locator, Keys.CONTROL, "c", elementName, methodName);
    }

    /**
     * Cuts the current selection in an element using Ctrl+X. Expects the
     * locator for the element, a readable name for logging, and the name
     * of the calling method.
     *
     * @param locator     the locator for the element to cut from
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void cut(By locator, String elementName, String methodName) {
        pressChord(locator, Keys.CONTROL, "x", elementName, methodName);
    }

    /**
     * Pastes the clipboard contents into an element using Ctrl+V. Expects
     * the locator for the element, a readable name for logging, and the
     * name of the calling method.
     *
     * @param locator     the locator for the element to paste into
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void paste(By locator, String elementName, String methodName) {
        pressChord(locator, Keys.CONTROL, "v", elementName, methodName);
    }

    /**
     * Sends the undo shortcut Ctrl+Z to an element. Expects the locator
     * for the element, a readable name for logging, and the name of the
     * calling method.
     *
     * @param locator     the locator for the element to send undo to
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void undo(By locator, String elementName, String methodName) {
        pressChord(locator, Keys.CONTROL, "z", elementName, methodName);
    }

    /**
     * Presses backspace a given number of times on an element. Expects
     * the locator for the element, how many times to press backspace, a
     * readable name for logging, and the name of the calling method.
     *
     * @param locator     the locator for the element to press backspace on
     * @param times       how many times to press backspace
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void backspace(By locator, int times, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        for (int i = 0; i < times; i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }
        LoggerUtil.info(methodName + " pressed backspace " + times + " time(s) on element: " + target(elementName, locator));
    }

    /**
     * Presses the down arrow key a given number of times on an element.
     * Expects the locator for the element, how many times to press the
     * key, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press the down arrow on
     * @param times       how many times to press the key
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void arrowDown(By locator, int times, String elementName, String methodName) {
        pressArrow(locator, Keys.ARROW_DOWN, times, elementName, methodName);
    }

    /**
     * Presses the up arrow key a given number of times on an element.
     * Expects the locator for the element, how many times to press the
     * key, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press the up arrow on
     * @param times       how many times to press the key
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void arrowUp(By locator, int times, String elementName, String methodName) {
        pressArrow(locator, Keys.ARROW_UP, times, elementName, methodName);
    }

    /**
     * Presses the left arrow key a given number of times on an element.
     * Expects the locator for the element, how many times to press the
     * key, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press the left arrow on
     * @param times       how many times to press the key
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void arrowLeft(By locator, int times, String elementName, String methodName) {
        pressArrow(locator, Keys.ARROW_LEFT, times, elementName, methodName);
    }

    /**
     * Presses the right arrow key a given number of times on an element.
     * Expects the locator for the element, how many times to press the
     * key, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to press the right arrow on
     * @param times       how many times to press the key
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void arrowRight(By locator, int times, String elementName, String methodName) {
        pressArrow(locator, Keys.ARROW_RIGHT, times, elementName, methodName);
    }

    /**
     * Sends a key press to the page as a whole rather than to a specific
     * element, useful for shortcuts that are not tied to any input.
     * Expects the key to press and the name of the calling method for
     * logging.
     *
     * @param key        the key to press
     * @param methodName the name of the calling method, for logging
     */
    public void pageGlobalKey(Keys key, String methodName) {
        new Actions(driver).sendKeys(key).perform();
        LoggerUtil.info(methodName + " pressed global key: " + key);
    }

    /**
     * Sends a key combination to the page as a whole rather than to a
     * specific element. Expects the modifier key to hold, the other key
     * in the combination, and the name of the calling method for
     * logging.
     *
     * @param modifier   the modifier key to hold down
     * @param key        the other key in the combination
     * @param methodName the name of the calling method, for logging
     */
    public void pageGlobalChord(Keys modifier, String key, String methodName) {
        new Actions(driver).keyDown(modifier).sendKeys(key).keyUp(modifier).perform();
        LoggerUtil.info(methodName + " pressed global chord: " + modifier + "+" + key);
    }

    /**
     * Holds a modifier key down, clicks an element, and then releases the
     * modifier key, useful for actions like Ctrl+click to open a link in
     * a new tab. Expects the locator for the element to click, the
     * modifier key to hold, a readable name for logging, and the name of
     * the calling method.
     *
     * @param locator     the locator for the element to click
     * @param modifier    the modifier key to hold down while clicking
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void holdKeyWhileClicking(By locator, Keys modifier, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        new Actions(driver).keyDown(modifier).click(element).keyUp(modifier).perform();
        LoggerUtil.info(methodName + " held " + modifier + " while clicking element: " + target(elementName, locator));
    }

    /**
     * Presses one of the arrow keys a given number of times on an
     * element. Expects the locator for the element, which arrow key to
     * press, how many times to press it, a readable name for logging, and
     * the name of the calling method.
     */
    private void pressArrow(By locator, Keys arrowKey, int times, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        for (int i = 0; i < times; i++) {
            element.sendKeys(arrowKey);
        }
        LoggerUtil.info(methodName + " pressed " + arrowKey + " " + times + " time(s) on element: " + target(elementName, locator));
    }

    /**
     * Waits until an element is visible on the page. Expects the locator
     * for the element, a readable name for logging, and the name of the
     * calling method. Returns the visible element. Throws a
     * KeyboardActionException if the element does not become visible
     * within the configured timeout.
     */
    private WebElement waitForVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new KeyboardActionException(
                    methodName + " - element not visible within " + timeout + " for: " + target(elementName, locator), e);
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
