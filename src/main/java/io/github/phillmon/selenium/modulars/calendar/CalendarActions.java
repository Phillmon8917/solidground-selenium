package io.github.phillmon.selenium.modulars.calendar;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * Drives a date picker calendar widget: reads which month is currently
 * shown, clicks the next or previous month buttons until the right month
 * is reached, and clicks the day that matches the requested date. Uses
 * the locators supplied through CalendarLocators so it can work with
 * different calendar widgets.
 */
public class CalendarActions {
    private static final int MAX_NAVIGATION_STEPS = 240;

    private final WebDriver driver;
    private final CalendarLocators locators;
    private final Duration timeout;

    /**
     * Creates the calendar actions with a custom timeout. Expects the
     * WebDriver for the current browser session, the locators describing
     * the calendar widget, and how long to wait for calendar elements to
     * appear or become clickable.
     *
     * @param driver   the WebDriver for the current browser session
     * @param locators the locators describing the calendar widget
     * @param timeout  how long to wait for calendar elements to appear or become clickable
     */
    public CalendarActions(WebDriver driver, CalendarLocators locators, Duration timeout) {
        this.driver = driver;
        this.locators = locators;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Creates the calendar actions using a default timeout of 30 seconds.
     * Expects the WebDriver for the current browser session and the
     * locators describing the calendar widget.
     *
     * @param driver   the WebDriver for the current browser session
     * @param locators the locators describing the calendar widget
     */
    public CalendarActions(WebDriver driver, CalendarLocators locators) {
        this(driver, locators, Duration.ofSeconds(30));
    }

    /**
     * Navigates the calendar to the month of the target date and clicks
     * that day. Expects the date to select, a readable name for the
     * calendar for logging, and the name of the calling method. Throws a
     * CalendarNavigationException if the calendar cannot reach the target
     * month or the day cannot be found.
     *
     * @param targetDate  the date to select
     * @param elementName a readable name for the calendar, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectDate(DateObject targetDate, String elementName, String methodName) {
        YearMonth targetMonth = YearMonth.of(targetDate.getYear(), targetDate.getMonthValue());
        navigateToMonth(targetMonth, elementName, methodName);

        By dayLocator = locators.dayLocatorFor(targetDate);
        WebElement dayElement = waitForClickable(dayLocator, elementName + " day " + targetDate, methodName);
        dayElement.click();
        LoggerUtil.info(methodName + " selected date: " + targetDate + " on calendar: " + elementName);
    }

    /**
     * Selects the date that is the given number of days away from today.
     * Expects the number of days to offset from today, which can be
     * negative for a date in the past, a readable name for the calendar
     * for logging, and the name of the calling method.
     *
     * @param offsetDays  the number of days to offset from today, which can be negative for a date in the past
     * @param elementName a readable name for the calendar, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectDate(int offsetDays, String elementName, String methodName) {
        selectDate(DateOffset.resolve(offsetDays), elementName, methodName);
    }

    /**
     * Clicks the next or previous month button repeatedly until the
     * calendar shows the target month. Expects the month to reach, a
     * readable name for the calendar for logging, and the name of the
     * calling method. Throws a CalendarNavigationException if the number
     * of clicks needed goes beyond the maximum allowed navigation steps,
     * which protects against an infinite loop if the calendar is stuck.
     */
    private void navigateToMonth(YearMonth targetMonth, String elementName, String methodName) {
        int steps = 0;
        YearMonth currentMonth = readCurrentMonth(elementName, methodName);

        while (!currentMonth.equals(targetMonth)) {
            if (steps++ >= MAX_NAVIGATION_STEPS) {
                throw new CalendarNavigationException(
                        methodName + " - exceeded max navigation steps trying to reach: " + targetMonth
                                + " on calendar: " + elementName);
            }
            if (targetMonth.isAfter(currentMonth)) {
                waitForClickable(locators.getNextButton(), elementName + " next month button", methodName).click();
            } else {
                waitForClickable(locators.getPreviousButton(), elementName + " previous month button", methodName).click();
            }
            currentMonth = readCurrentMonth(elementName, methodName);
        }
    }

    /**
     * Reads the month and year currently displayed on the calendar's
     * caption. Expects a readable name for the calendar for logging and
     * the name of the calling method. Throws a CalendarNavigationException
     * if the caption text cannot be parsed as a month and year.
     */
    private YearMonth readCurrentMonth(String elementName, String methodName) {
        String captionText = waitForVisible(locators.getCaption(), elementName + " caption", methodName).getText();
        try {
            return YearMonth.parse(captionText.trim(), locators.getCaptionFormatter());
        } catch (DateTimeParseException e) {
            throw new CalendarNavigationException(
                    methodName + " - could not parse calendar caption for " + elementName + ": '" + captionText + "'", e);
        }
    }

    /**
     * Waits until an element is visible on the page. Expects the locator
     * for the element, a readable name for logging, and the name of the
     * calling method. Returns the visible element. Throws a
     * CalendarNavigationException if the element does not become visible
     * within the configured timeout.
     */
    private WebElement waitForVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new CalendarNavigationException(
                    methodName + " - calendar element not visible within " + timeout + " for: " + target(elementName, locator), e);
        }
    }

    /**
     * Waits until an element can be clicked. Expects the locator for the
     * element, a readable name for logging, and the name of the calling
     * method. Returns the clickable element. Throws a
     * CalendarNavigationException if the element does not become
     * clickable within the configured timeout.
     */
    private WebElement waitForClickable(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.elementToBeClickable(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new CalendarNavigationException(
                    methodName + " - calendar element not clickable within " + timeout + " for: " + target(elementName, locator), e);
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
