package io.github.phillmon.selenium.modulars.mouse;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Actions for mouse movements and gestures using Selenium's Actions
 * builder: clicking, double clicking, right clicking, hovering, dragging
 * and dropping, and scrolling. These go beyond a simple click and are
 * useful for pages that react to hover states, drag targets, or precise
 * mouse positioning.
 */
public class MouseActions {
    private final WebDriver driver;
    private final Duration timeout;

    /**
     * Creates the mouse actions using a default timeout of 10 seconds.
     * Expects the WebDriver for the current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public MouseActions(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    /**
     * Creates the mouse actions with a custom timeout. Expects the
     * WebDriver for the current browser session and how long to wait for
     * elements to reach the expected state.
     *
     * @param driver  the WebDriver for the current browser session
     * @param timeout how long to wait for elements to reach the expected state
     */
    public MouseActions(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Clicks an element using the Actions builder rather than a plain
     * Selenium click. Expects the locator for the element, a readable
     * name for logging, and the name of the calling method.
     *
     * @param locator     the locator for the element to click
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void click(By locator, String elementName, String methodName) {
        WebElement element = waitForClickable(locator, elementName, methodName);
        new Actions(driver).click(element).perform();
        LoggerUtil.info(methodName + " clicked element: " + target(elementName, locator));
    }

    /**
     * Double clicks an element. Expects the locator for the element, a
     * readable name for logging, and the name of the calling method.
     *
     * @param locator     the locator for the element to double click
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void doubleClick(By locator, String elementName, String methodName) {
        WebElement element = waitForClickable(locator, elementName, methodName);
        new Actions(driver).doubleClick(element).perform();
        LoggerUtil.info(methodName + " double-clicked element: " + target(elementName, locator));
    }

    /**
     * Right clicks an element to open its context menu. Expects the
     * locator for the element, a readable name for logging, and the name
     * of the calling method.
     *
     * @param locator     the locator for the element to right click
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void rightClick(By locator, String elementName, String methodName) {
        WebElement element = waitForClickable(locator, elementName, methodName);
        new Actions(driver).contextClick(element).perform();
        LoggerUtil.info(methodName + " right-clicked element: " + target(elementName, locator));
    }

    /**
     * Presses and holds the mouse button down on an element without
     * releasing it. Expects the locator for the element, a readable name
     * for logging, and the name of the calling method. Call release
     * afterwards to let go of the mouse button.
     *
     * @param locator     the locator for the element to press and hold
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void clickAndHold(By locator, String elementName, String methodName) {
        WebElement element = waitForClickable(locator, elementName, methodName);
        new Actions(driver).clickAndHold(element).perform();
        LoggerUtil.info(methodName + " clicked and held element: " + target(elementName, locator));
    }

    /**
     * Releases the mouse button after a clickAndHold. Expects the name of
     * the calling method for logging.
     *
     * @param methodName the name of the calling method, for logging
     */
    public void release(String methodName) {
        new Actions(driver).release().perform();
        LoggerUtil.info(methodName + " released mouse button");
    }

    /**
     * Moves the mouse over an element without clicking it, useful for
     * revealing hover menus or tooltips. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to hover over
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void hover(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        new Actions(driver).moveToElement(element).perform();
        LoggerUtil.info(methodName + " hovered over element: " + target(elementName, locator));
    }

    /**
     * Hovers over one element to reveal a menu, then clicks a second
     * element, all in a single continuous mouse movement. Expects the
     * locator and readable name of the element to hover over, the locator
     * and readable name of the element to click, and the name of the
     * calling method.
     *
     * @param hoverLocator     the locator for the element to hover over
     * @param hoverElementName a readable name for the element hovered over, for logging
     * @param clickLocator     the locator for the element to click
     * @param clickElementName a readable name for the element clicked, for logging
     * @param methodName       the name of the calling method, for logging
     */
    public void hoverAndClick(By hoverLocator, String hoverElementName, By clickLocator, String clickElementName, String methodName) {
        WebElement hoverElement = waitForVisible(hoverLocator, hoverElementName, methodName);
        WebElement clickElement = waitForClickable(clickLocator, clickElementName, methodName);
        new Actions(driver)
                .moveToElement(hoverElement)
                .moveToElement(clickElement)
                .click()
                .perform();
        LoggerUtil.info(methodName + " hovered over " + target(hoverElementName, hoverLocator)
                + " then clicked " + target(clickElementName, clickLocator));
    }

    /**
     * Hovers over several elements one after another in a single
     * continuous mouse movement, useful for multi-level dropdown menus.
     * Expects the name of the calling method for logging, a list of
     * readable names matching each locator, and the locators to hover
     * over in order. Throws a MouseActionException if no locators are
     * given, or if the number of names does not match the number of
     * locators.
     *
     * @param methodName   the name of the calling method, for logging
     * @param elementNames a list of readable names matching each locator, for logging
     * @param locators     the locators to hover over, in order
     */
    public void hoverChain(String methodName, List<String> elementNames, By... locators) {
        if (locators == null || locators.length == 0) {
            throw new MouseActionException(methodName + " - at least one locator must be provided");
        }
        if (elementNames == null || elementNames.size() != locators.length) {
            throw new MouseActionException(methodName + " - elementNames must match locator count");
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < locators.length; i++) {
            WebElement element = waitForVisible(locators[i], elementNames.get(i), methodName);
            actions.moveToElement(element);
        }
        actions.perform();
        LoggerUtil.info(methodName + " hovered through " + locators.length + " element(s) in sequence: "
                + targets(elementNames, locators));
    }

    /**
     * Clicks at a specific pixel offset from the centre of an element,
     * instead of clicking its centre directly. Expects the locator for
     * the element, the horizontal and vertical offset in pixels, a
     * readable name for logging, and the name of the calling method.
     *
     * @param locator     the locator for the element to click at an offset
     * @param xOffset     the horizontal offset in pixels from the element's centre
     * @param yOffset     the vertical offset in pixels from the element's centre
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void clickAtOffset(By locator, int xOffset, int yOffset, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        new Actions(driver)
                .moveToElement(element, xOffset, yOffset)
                .click()
                .perform();
        LoggerUtil.info(methodName + " clicked element " + target(elementName, locator)
                + " at offset (" + xOffset + ", " + yOffset + ")");
    }

    /**
     * Moves the mouse pointer by a given horizontal and vertical amount
     * from its current position. Expects the horizontal and vertical
     * offset in pixels and the name of the calling method for logging.
     *
     * @param xOffset    the horizontal offset in pixels to move the mouse by
     * @param yOffset    the vertical offset in pixels to move the mouse by
     * @param methodName the name of the calling method, for logging
     */
    public void moveByOffset(int xOffset, int yOffset, String methodName) {
        new Actions(driver).moveByOffset(xOffset, yOffset).perform();
        LoggerUtil.info(methodName + " moved mouse by offset (" + xOffset + ", " + yOffset + ")");
    }

    /**
     * Drags one element and drops it onto another. Expects the locator
     * and readable name of the element being dragged, the locator and
     * readable name of the drop target, and the name of the calling
     * method.
     *
     * @param sourceLocator     the locator for the element being dragged
     * @param sourceElementName a readable name for the element being dragged, for logging
     * @param targetLocator     the locator for the drop target
     * @param targetElementName a readable name for the drop target, for logging
     * @param methodName        the name of the calling method, for logging
     */
    public void dragAndDrop(By sourceLocator, String sourceElementName, By targetLocator, String targetElementName, String methodName) {
        WebElement source = waitForVisible(sourceLocator, sourceElementName, methodName);
        WebElement target = waitForVisible(targetLocator, targetElementName, methodName);
        new Actions(driver).dragAndDrop(source, target).perform();
        LoggerUtil.info(methodName + " dragged element " + target(sourceElementName, sourceLocator)
                + " to " + target(targetElementName, targetLocator));
    }

    /**
     * Drags an element by a given pixel offset rather than to another
     * element. Expects the locator for the element being dragged, the
     * horizontal and vertical offset in pixels, a readable name for
     * logging, and the name of the calling method.
     *
     * @param sourceLocator     the locator for the element being dragged
     * @param xOffset           the horizontal offset in pixels to drag the element by
     * @param yOffset           the vertical offset in pixels to drag the element by
     * @param sourceElementName a readable name for the element being dragged, for logging
     * @param methodName        the name of the calling method, for logging
     */
    public void dragAndDropByOffset(By sourceLocator, int xOffset, int yOffset, String sourceElementName, String methodName) {
        WebElement source = waitForVisible(sourceLocator, sourceElementName, methodName);
        new Actions(driver).dragAndDropBy(source, xOffset, yOffset).perform();
        LoggerUtil.info(methodName + " dragged element " + target(sourceElementName, sourceLocator)
                + " by offset (" + xOffset + ", " + yOffset + ")");
    }

    /**
     * Drags one element to another in several small steps with a short
     * pause between each, instead of one instant movement, for widgets
     * that need gradual mouse movement to register the drag correctly.
     * Expects the locator and readable name of the element being dragged,
     * the locator and readable name of the drop target, how many steps to
     * move it in, and the name of the calling method.
     *
     * @param sourceLocator     the locator for the element being dragged
     * @param sourceElementName a readable name for the element being dragged, for logging
     * @param targetLocator     the locator for the drop target
     * @param targetElementName a readable name for the drop target, for logging
     * @param steps             how many steps to move the element in
     * @param methodName        the name of the calling method, for logging
     */
    public void dragAndDropSlowly(By sourceLocator, String sourceElementName, By targetLocator, String targetElementName, int steps, String methodName) {
        WebElement source = waitForVisible(sourceLocator, sourceElementName, methodName);
        WebElement target = waitForVisible(targetLocator, targetElementName, methodName);

        Actions actions = new Actions(driver);
        actions.clickAndHold(source).perform();

        for (int i = 1; i <= steps; i++) {
            actions.moveToElement(target, 0, 0).perform();
            sleep(Duration.ofMillis(50));
        }

        actions.release().perform();
        LoggerUtil.info(methodName + " dragged element " + target(sourceElementName, sourceLocator)
                + " to " + target(targetElementName, targetLocator) + " over " + steps + " step(s)");
    }

    /**
     * Scrolls the page until an element is in view, using the Actions
     * builder's native scrolling support. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the element to scroll to
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void scrollToElement(By locator, String elementName, String methodName) {
        WebElement element = waitForPresent(locator, elementName, methodName);
        new Actions(driver).scrollToElement(element).perform();
        LoggerUtil.info(methodName + " scrolled to element: " + target(elementName, locator));
    }

    /**
     * Scrolls the page by a given horizontal and vertical amount from
     * wherever the mouse currently is. Expects the horizontal and
     * vertical scroll amount and the name of the calling method for
     * logging.
     *
     * @param deltaX     the horizontal amount to scroll by
     * @param deltaY     the vertical amount to scroll by
     * @param methodName the name of the calling method, for logging
     */
    public void scrollByAmount(int deltaX, int deltaY, String methodName) {
        new Actions(driver).scrollByAmount(deltaX, deltaY).perform();
        LoggerUtil.info(methodName + " scrolled by amount (" + deltaX + ", " + deltaY + ")");
    }

    /**
     * Scrolls by a given amount starting from the position of a specific
     * element, rather than the current mouse position. Expects the
     * locator for the element to scroll from, the horizontal and vertical
     * scroll amount, a readable name for logging, and the name of the
     * calling method.
     *
     * @param locator     the locator for the element to scroll from
     * @param deltaX      the horizontal amount to scroll by
     * @param deltaY      the vertical amount to scroll by
     * @param elementName a readable name for the element, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void scrollFromElement(By locator, int deltaX, int deltaY, String elementName, String methodName) {
        WebElement element = waitForPresent(locator, elementName, methodName);
        new Actions(driver)
                .scrollFromOrigin(
                        org.openqa.selenium.interactions.WheelInput.ScrollOrigin.fromElement(element),
                        deltaX, deltaY)
                .perform();
        LoggerUtil.info(methodName + " scrolled from element " + target(elementName, locator)
                + " by amount (" + deltaX + ", " + deltaY + ")");
    }

    /**
     * Waits until an element is visible on the page. Expects the locator
     * for the element, a readable name for logging, and the name of the
     * calling method. Returns the visible element. Throws a
     * MouseActionException if the element does not become visible within
     * the configured timeout.
     */
    private WebElement waitForVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new MouseActionException(
                    methodName + " - element not visible within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element can be clicked. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method. Returns the clickable element. Throws a
     * MouseActionException if the element does not become clickable
     * within the configured timeout.
     */
    private WebElement waitForClickable(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.elementToBeClickable(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new MouseActionException(
                    methodName + " - element not clickable within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element exists in the page's structure, even if it
     * is not visible yet. Expects the locator for the element, a readable
     * name for logging, and the name of the calling method. Returns the
     * present element. Throws a MouseActionException if the element does
     * not appear within the configured timeout.
     */
    private WebElement waitForPresent(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new MouseActionException(
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
     * Builds a readable, comma separated description of several elements,
     * pairing each name with its matching locator in order.
     */
    private String targets(List<String> elementNames, By... locators) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < locators.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(target(elementNames.get(i), locators[i]));
        }
        return builder.toString();
    }

    /**
     * Pauses the current thread for the given duration, used between
     * steps of a slow drag and drop. Throws a MouseActionException if the
     * thread is interrupted while waiting.
     */
    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MouseActionException("Interrupted during slow drag step", e);
        }
    }
}
