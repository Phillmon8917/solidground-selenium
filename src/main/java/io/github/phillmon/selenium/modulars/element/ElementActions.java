package io.github.phillmon.selenium.modulars.element;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The main set of actions for working with individual page elements:
 * clicking, typing, reading text and attributes, checking element state,
 * scrolling, and waiting for elements to appear, disappear, or show
 * expected text. Every locator-based action waits for the element to be
 * in the right state first, so callers do not need to add their own
 * waits.
 */
public class ElementActions {
    private final WebDriver driver;
    private final Duration timeout;

    /**
     * Creates the element actions using a default timeout of 30 seconds.
     * Expects the WebDriver for the current browser session.
     */
    public ElementActions(WebDriver driver) {
        this(driver, Duration.ofSeconds(30));
    }

    /**
     * Creates the element actions with a custom timeout. Expects the
     * WebDriver for the current browser session and how long to wait for
     * elements to reach the expected state.
     */
    public ElementActions(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Waits for the element to be clickable and clicks it using a normal
     * Selenium click. Expects the locator for the element, a readable
     * name for logging, and the name of the calling method.
     */
    public void click(By locator, String elementName, String methodName) {
        waitForClickable(locator, elementName, methodName).click();
        LoggerUtil.info(methodName + " clicked element: " + target(elementName, locator));
    }

    /**
     * Clicks an element using JavaScript instead of a normal Selenium
     * click, which can succeed even when another element is visually on
     * top of it. Expects the locator for the element, a readable name for
     * logging, and the name of the calling method.
     */
    public void jsClick(By locator, String elementName, String methodName) {
        WebElement element = waitForPresent(locator, elementName, methodName);
        executeScript("arguments[0].click();", element);
        LoggerUtil.info(methodName + " js-clicked element: " + target(elementName, locator));
    }

    /**
     * Tries a normal click first, and if the browser reports that the
     * click was intercepted by another element, retries using a
     * JavaScript click instead. Expects the locator for the element, a
     * readable name for logging, and the name of the calling method.
     */
    public void clickWithJsFallback(By locator, String elementName, String methodName) {
        try {
            click(locator, elementName, methodName);
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            LoggerUtil.warning(methodName + " click intercepted, falling back to JS click: " + target(elementName, locator));
            jsClick(locator, elementName, methodName);
        }
    }

    /**
     * Clears the element and types the given text into it in one go, not
     * treating the text as sensitive in the log. Expects the locator for
     * the element, the text to type, a readable name for logging, and the
     * name of the calling method.
     */
    public void typeText(By locator, String text, String elementName, String methodName) {
        typeText(locator, text, false, elementName, methodName);
    }

    /**
     * Clears the element and types the given text into it in one go.
     * Expects the locator for the element, the text to type, whether the
     * text is sensitive (such as a password) and should be hidden from
     * the log, a readable name for logging, and the name of the calling
     * method.
     */
    public void typeText(By locator, String text, boolean sensitive, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.clear();
        element.sendKeys(text);
        LoggerUtil.info(methodName + " typed text into element: " + target(elementName, locator)
                + " with value: " + mask(text, sensitive));
    }

    /**
     * Clears the element and types the given text one character at a
     * time, with a short default pause between key presses, not treating
     * the text as sensitive in the log. Expects the locator for the
     * element, the text to type, a readable name for logging, and the
     * name of the calling method. Use this instead of typeText when a
     * page needs each keystroke to trigger its own event, such as a live
     * search box.
     */
    public void typeSequentially(By locator, String text, String elementName, String methodName) {
        typeSequentially(locator, text, Duration.ofMillis(80), false, elementName, methodName);
    }

    /**
     * Clears the element and types the given text one character at a
     * time, with a short default pause between key presses. Expects the
     * locator for the element, the text to type, whether the text is
     * sensitive and should be hidden from the log, a readable name for
     * logging, and the name of the calling method.
     */
    public void typeSequentially(By locator, String text, boolean sensitive, String elementName, String methodName) {
        typeSequentially(locator, text, Duration.ofMillis(80), sensitive, elementName, methodName);
    }

    /**
     * Clears the element and types the given text one character at a
     * time, using a custom pause between key presses, not treating the
     * text as sensitive in the log. Expects the locator for the element,
     * the text to type, the delay to wait between each character, a
     * readable name for logging, and the name of the calling method.
     */
    public void typeSequentially(By locator, String text, Duration delayBetweenKeys, String elementName, String methodName) {
        typeSequentially(locator, text, delayBetweenKeys, false, elementName, methodName);
    }

    /**
     * Clears the element and types the given text one character at a
     * time, using a custom pause between key presses. Expects the locator
     * for the element, the text to type, the delay to wait between each
     * character, whether the text is sensitive and should be hidden from
     * the log, a readable name for logging, and the name of the calling
     * method.
     */
    public void typeSequentially(By locator, String text, Duration delayBetweenKeys, boolean sensitive, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.clear();
        for (char character : text.toCharArray()) {
            element.sendKeys(String.valueOf(character));
            sleep(delayBetweenKeys);
        }
        LoggerUtil.info(methodName + " typed text sequentially into element: " + target(elementName, locator)
                + " with value: " + mask(text, sensitive));
    }

    /**
     * Types text into the element without clearing it first, so the new
     * text is added after whatever is already there, not treating the
     * text as sensitive in the log. Expects the locator for the element,
     * the text to append, a readable name for logging, and the name of
     * the calling method.
     */
    public void appendText(By locator, String text, String elementName, String methodName) {
        appendText(locator, text, false, elementName, methodName);
    }

    /**
     * Types text into the element without clearing it first. Expects the
     * locator for the element, the text to append, whether the text is
     * sensitive and should be hidden from the log, a readable name for
     * logging, and the name of the calling method.
     */
    public void appendText(By locator, String text, boolean sensitive, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.sendKeys(text);
        LoggerUtil.info(methodName + " appended text to element: " + target(elementName, locator)
                + " with value: " + mask(text, sensitive));
    }

    /**
     * Clears the text out of an input element using its clear method.
     * Expects the locator for the element, a readable name for logging,
     * and the name of the calling method.
     */
    public void clearText(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.clear();
        LoggerUtil.info(methodName + " cleared text from element: " + target(elementName, locator));
    }

    /**
     * Clears the text out of an input element by selecting everything
     * with Ctrl+A and deleting it, for inputs where the normal clear
     * method does not work reliably. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     */
    public void clearTextWithKeys(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        element.sendKeys(org.openqa.selenium.Keys.DELETE);
        LoggerUtil.info(methodName + " cleared text via keyboard shortcut for element: " + target(elementName, locator));
    }

    /**
     * Submits the form that the given element belongs to. Expects the
     * locator for an element inside the form, a readable name for
     * logging, and the name of the calling method.
     */
    public void submit(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        element.submit();
        LoggerUtil.info(methodName + " submitted form via element: " + target(elementName, locator));
    }

    /**
     * Uploads a file through a file input element by sending the file's
     * path as keystrokes. Expects the locator for the file input, the
     * full path of the file to upload, a readable name for logging, and
     * the name of the calling method.
     */
    public void uploadFile(By locator, String filePath, String elementName, String methodName) {
        WebElement element = waitForPresent(locator, elementName, methodName);
        element.sendKeys(filePath);
        LoggerUtil.info(methodName + " uploaded file: " + filePath + " to element: " + target(elementName, locator));
    }

    /**
     * Returns the visible text of an element. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     */
    public String getText(By locator, String elementName, String methodName) {
        String text = waitForVisible(locator, elementName, methodName).getText();
        LoggerUtil.info(methodName + " found text: '" + text + "' on element: " + target(elementName, locator));
        return text;
    }

    /**
     * Returns the value of an HTML attribute on an element. Expects the
     * locator for the element, the name of the attribute to read, a
     * readable name for logging, and the name of the calling method.
     */
    public String getAttribute(By locator, String attribute, String elementName, String methodName) {
        String value = waitForVisible(locator, elementName, methodName).getAttribute(attribute);
        LoggerUtil.info(methodName + " found attribute '" + attribute + "' value: '" + value + "' on element: " + target(elementName, locator));
        return value;
    }

    /**
     * Returns the computed value of a CSS property on an element. Expects
     * the locator for the element, the name of the CSS property to read,
     * a readable name for logging, and the name of the calling method.
     */
    public String getCssValue(By locator, String property, String elementName, String methodName) {
        String value = waitForVisible(locator, elementName, methodName).getCssValue(property);
        LoggerUtil.info(methodName + " found css property '" + property + "' value: '" + value + "' on element: " + target(elementName, locator));
        return value;
    }

    /**
     * Returns the visible text of every element that matches the locator.
     * Expects the locator matching several elements, a readable name for
     * logging, and the name of the calling method.
     */
    public List<String> getAllText(By locator, String elementName, String methodName) {
        List<WebElement> elements = waitForAllVisible(locator, elementName, methodName);
        List<String> texts = elements.stream().map(WebElement::getText).collect(Collectors.toList());
        LoggerUtil.info(methodName + " found " + texts.size() + " element(s) with text: " + texts
                + " for element: " + target(elementName, locator));
        return texts;
    }

    /**
     * Returns how many elements on the page currently match the locator,
     * without waiting for them to appear. Expects the locator to count,
     * a readable name for logging, and the name of the calling method.
     */
    public int countElements(By locator, String elementName, String methodName) {
        int count = driver.findElements(locator).size();
        LoggerUtil.info(methodName + " found element count: " + count + " for: " + target(elementName, locator));
        return count;
    }

    /**
     * Returns whether an element is currently displayed on the page,
     * without waiting for it. Expects the locator for the element, a
     * readable name for logging, and the name of the calling method.
     * Returns false rather than throwing if the element is not present
     * at all.
     */
    public boolean isDisplayed(By locator, String elementName, String methodName) {
        boolean displayed;
        try {
            displayed = driver.findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            displayed = false;
        }
        LoggerUtil.info(methodName + " found element displayed state: " + displayed + " for: " + target(elementName, locator));
        return displayed;
    }

    /**
     * Waits for the element to be visible and returns whether it is
     * enabled for interaction. Expects the locator for the element, a
     * readable name for logging, and the name of the calling method.
     */
    public boolean isEnabled(By locator, String elementName, String methodName) {
        boolean enabled = waitForVisible(locator, elementName, methodName).isEnabled();
        LoggerUtil.info(methodName + " found element enabled state: " + enabled + " for: " + target(elementName, locator));
        return enabled;
    }

    /**
     * Waits for the element to be visible and returns whether it is
     * currently selected, such as a checked checkbox or a chosen radio
     * button. Expects the locator for the element, a readable name for
     * logging, and the name of the calling method.
     */
    public boolean isSelected(By locator, String elementName, String methodName) {
        boolean selected = waitForVisible(locator, elementName, methodName).isSelected();
        LoggerUtil.info(methodName + " found element selected state: " + selected + " for: " + target(elementName, locator));
        return selected;
    }

    /**
     * Returns whether at least one element matches the locator right now,
     * without waiting. Expects the locator for the element, a readable
     * name for logging, and the name of the calling method.
     */
    public boolean isPresent(By locator, String elementName, String methodName) {
        boolean present = !driver.findElements(locator).isEmpty();
        LoggerUtil.info(methodName + " found element present state: " + present + " for: " + target(elementName, locator));
        return present;
    }

    /**
     * Scrolls the page so that the element is centred in the viewport.
     * Expects the locator for the element, a readable name for logging,
     * and the name of the calling method.
     */
    public void scrollIntoView(By locator, String elementName, String methodName) {
        WebElement element = waitForPresent(locator, elementName, methodName);
        executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
        LoggerUtil.info(methodName + " scrolled element into view: " + target(elementName, locator));
    }

    /**
     * Scrolls the whole page window by a given horizontal and vertical
     * amount. Expects how far to scroll on the x axis, how far to scroll
     * on the y axis, and the name of the calling method for logging.
     */
    public void scrollBy(int deltaX, int deltaY, String methodName) {
        executeScript("window.scrollBy(arguments[0], arguments[1]);", deltaX, deltaY);
        LoggerUtil.info(methodName + " scrolled window by x=" + deltaX + ", y=" + deltaY);
    }

    /**
     * Waits until an element that used to be present becomes invisible or
     * is removed from the page. Expects the locator for the element, a
     * readable name for logging, and the name of the calling method.
     * Throws an ElementActionException if the element is still visible
     * once the configured timeout runs out.
     */
    public void waitForInvisibility(By locator, String elementName, String methodName) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
            LoggerUtil.info(methodName + " confirmed element became invisible: " + target(elementName, locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ElementActionException(
                    methodName + " - element did not become invisible within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element's text contains the expected text. Expects
     * the locator for the element, the text that should appear, a
     * readable name for logging, and the name of the calling method.
     * Throws an ElementActionException if the text does not appear within
     * the configured timeout.
     */
    public void waitForTextToBePresent(By locator, String expectedText, String elementName, String methodName) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
            LoggerUtil.info(methodName + " confirmed text '" + expectedText + "' present in element: " + target(elementName, locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ElementActionException(
                    methodName + " - text '" + expectedText + "' did not appear within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element is visible on the page. Expects the locator
     * for the element, a readable name for logging, and the name of the
     * calling method. Returns the visible element. Throws an
     * ElementActionException if the element does not become visible
     * within the configured timeout.
     */
    private WebElement waitForVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ElementActionException(
                    methodName + " - element not visible within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until every element matching the locator is visible. Expects
     * the locator matching several elements, a readable name for
     * logging, and the name of the calling method. Returns the list of
     * visible elements. Throws an ElementActionException if they do not
     * all become visible within the configured timeout.
     */
    private List<WebElement> waitForAllVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ElementActionException(
                    methodName + " - elements not visible within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element can be clicked. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method. Returns the clickable element. Throws an
     * ElementActionException if the element does not become clickable
     * within the configured timeout.
     */
    private WebElement waitForClickable(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.elementToBeClickable(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ElementActionException(
                    methodName + " - element not clickable within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element exists in the page's structure, even if it
     * is not visible yet. Expects the locator for the element, a readable
     * name for logging, and the name of the calling method. Returns the
     * present element. Throws an ElementActionException if the element
     * does not appear within the configured timeout.
     */
    private WebElement waitForPresent(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new ElementActionException(
                    methodName + " - element not present within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Builds a readable description of an element combining its name and
     * its locator, used in log and exception messages.
     */
    private String target(String elementName, By locator) {
        return elementName + " (" + locator + ")";
    }

    /**
     * Returns the value as given, or a row of asterisks if it should be
     * hidden from the log because it is sensitive, such as a password.
     */
    private String mask(String value, boolean sensitive) {
        return sensitive ? "*******" : value;
    }

    /**
     * Runs a piece of JavaScript in the browser with the given
     * arguments. Expects the script text and any arguments the script
     * refers to as arguments[0], arguments[1], and so on.
     */
    private void executeScript(String script, Object... args) {
        ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /**
     * Pauses the current thread for the given duration, used to add a
     * delay between key presses in typeSequentially. Throws an
     * ElementActionException if the thread is interrupted while waiting.
     */
    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ElementActionException("Interrupted during typeSequentially delay", e);
        }
    }
}
