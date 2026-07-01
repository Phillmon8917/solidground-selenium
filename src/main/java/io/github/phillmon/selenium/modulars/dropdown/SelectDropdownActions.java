package io.github.phillmon.selenium.modulars.dropdown;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Actions for working with native HTML select dropdowns, using Selenium's
 * Select wrapper underneath. Supports single selects and multi-selects:
 * choosing an option, removing a selection from a multi-select, and
 * reading back what is currently selected or what options exist.
 */
public class SelectDropdownActions {
    private final WebDriver driver;
    private final Duration timeout;

    /**
     * Creates the dropdown actions using a default timeout of 10 seconds.
     * Expects the WebDriver for the current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public SelectDropdownActions(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    /**
     * Creates the dropdown actions with a custom timeout. Expects the
     * WebDriver for the current browser session and how long to wait for
     * the dropdown to become visible.
     *
     * @param driver  the WebDriver for the current browser session
     * @param timeout how long to wait for the dropdown to become visible
     */
    public SelectDropdownActions(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Selects the option whose visible text matches exactly. Expects the
     * locator for the dropdown, the visible text of the option to select,
     * a readable name for the dropdown for logging, and the name of the
     * calling method. Throws a DropdownActionException if no option with
     * that text exists, and includes the list of available options in the
     * error to help diagnose the problem.
     *
     * @param locator     the locator for the dropdown
     * @param visibleText the visible text of the option to select
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectByVisibleText(By locator, String visibleText, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        try {
            select.selectByVisibleText(visibleText);
            LoggerUtil.info(methodName + " selected option with visible text: " + visibleText
                    + " on dropdown: " + target(elementName, locator));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new DropdownActionException(
                    methodName + " - no option found with visible text: '" + visibleText
                            + "' on dropdown: " + target(elementName, locator)
                            + ". Available options: " + getAllOptionsText(locator, elementName, methodName), e);
        }
    }

    /**
     * Selects the option whose value attribute matches. Expects the
     * locator for the dropdown, the value attribute to match, a readable
     * name for the dropdown for logging, and the name of the calling
     * method. Throws a DropdownActionException if no option with that
     * value exists.
     *
     * @param locator     the locator for the dropdown
     * @param value       the value attribute to match
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectByValue(By locator, String value, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        try {
            select.selectByValue(value);
            LoggerUtil.info(methodName + " selected option with value: " + value
                    + " on dropdown: " + target(elementName, locator));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new DropdownActionException(
                    methodName + " - no option found with value: '" + value
                            + "' on dropdown: " + target(elementName, locator), e);
        }
    }

    /**
     * Selects the option at the given position in the list, starting at
     * zero. Expects the locator for the dropdown, the zero-based index of
     * the option, a readable name for the dropdown for logging, and the
     * name of the calling method. Throws a DropdownActionException if the
     * index does not match any option, and includes the total number of
     * options in the error.
     *
     * @param locator     the locator for the dropdown
     * @param index       the zero-based index of the option to select
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectByIndex(By locator, int index, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        try {
            select.selectByIndex(index);
            LoggerUtil.info(methodName + " selected option at index: " + index
                    + " on dropdown: " + target(elementName, locator));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new DropdownActionException(
                    methodName + " - no option found at index: " + index
                            + " on dropdown: " + target(elementName, locator)
                            + ". Total options available: " + select.getOptions().size(), e);
        }
    }

    /**
     * Selects the first option whose visible text contains the given
     * partial text, useful when the exact wording is not known. Expects
     * the locator for the dropdown, the partial text to search for, a
     * readable name for the dropdown for logging, and the name of the
     * calling method. Throws a DropdownActionException if no option
     * contains that text.
     *
     * @param locator     the locator for the dropdown
     * @param partialText the partial text to search for
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void selectByVisibleTextContains(By locator, String partialText, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        WebElement match = select.getOptions().stream()
                .filter(option -> option.getText().contains(partialText))
                .findFirst()
                .orElseThrow(() -> new DropdownActionException(
                        methodName + " - no option found containing text: '" + partialText
                                + "' on dropdown: " + target(elementName, locator)
                                + ". Available options: " + getAllOptionsText(locator, elementName, methodName)));
        match.click();
        LoggerUtil.info(methodName + " selected option containing text: " + partialText
                + " on dropdown: " + target(elementName, locator));
    }

    /**
     * Removes the selection from the option with the given visible text,
     * on a multi-select dropdown. Expects the locator for the dropdown,
     * the visible text of the option to deselect, a readable name for the
     * dropdown for logging, and the name of the calling method. Throws a
     * DropdownActionException if the dropdown is not a multi-select.
     *
     * @param locator     the locator for the dropdown
     * @param visibleText the visible text of the option to deselect
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void deselectByVisibleText(By locator, String visibleText, String elementName, String methodName) {
        Select select = resolveMultiSelect(locator, elementName, methodName);
        select.deselectByVisibleText(visibleText);
        LoggerUtil.info(methodName + " deselected option with visible text: " + visibleText
                + " on dropdown: " + target(elementName, locator));
    }

    /**
     * Removes the selection from the option with the given value
     * attribute, on a multi-select dropdown. Expects the locator for the
     * dropdown, the value attribute to deselect, a readable name for the
     * dropdown for logging, and the name of the calling method. Throws a
     * DropdownActionException if the dropdown is not a multi-select.
     *
     * @param locator     the locator for the dropdown
     * @param value       the value attribute to deselect
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void deselectByValue(By locator, String value, String elementName, String methodName) {
        Select select = resolveMultiSelect(locator, elementName, methodName);
        select.deselectByValue(value);
        LoggerUtil.info(methodName + " deselected option with value: " + value
                + " on dropdown: " + target(elementName, locator));
    }

    /**
     * Removes the selection from the option at the given position, on a
     * multi-select dropdown. Expects the locator for the dropdown, the
     * zero-based index to deselect, a readable name for the dropdown for
     * logging, and the name of the calling method. Throws a
     * DropdownActionException if the dropdown is not a multi-select.
     *
     * @param locator     the locator for the dropdown
     * @param index       the zero-based index of the option to deselect
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void deselectByIndex(By locator, int index, String elementName, String methodName) {
        Select select = resolveMultiSelect(locator, elementName, methodName);
        select.deselectByIndex(index);
        LoggerUtil.info(methodName + " deselected option at index: " + index
                + " on dropdown: " + target(elementName, locator));
    }

    /**
     * Clears every selected option on a multi-select dropdown. Expects
     * the locator for the dropdown, a readable name for the dropdown for
     * logging, and the name of the calling method. Throws a
     * DropdownActionException if the dropdown is not a multi-select.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     */
    public void deselectAll(By locator, String elementName, String methodName) {
        Select select = resolveMultiSelect(locator, elementName, methodName);
        select.deselectAll();
        LoggerUtil.info(methodName + " deselected all options on dropdown: " + target(elementName, locator));
    }

    /**
     * Returns the visible text of the first currently selected option.
     * Expects the locator for the dropdown, a readable name for the
     * dropdown for logging, and the name of the calling method.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return the visible text of the first currently selected option
     */
    public String getSelectedOptionText(By locator, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        String text = select.getFirstSelectedOption().getText();
        LoggerUtil.info(methodName + " found selected option text: " + text
                + " on dropdown: " + target(elementName, locator));
        return text;
    }

    /**
     * Returns the value attribute of the first currently selected option.
     * Expects the locator for the dropdown, a readable name for the
     * dropdown for logging, and the name of the calling method.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return the value attribute of the first currently selected option
     */
    public String getSelectedOptionValue(By locator, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        String value = select.getFirstSelectedOption().getAttribute("value");
        LoggerUtil.info(methodName + " found selected option value: " + value
                + " on dropdown: " + target(elementName, locator));
        return value;
    }

    /**
     * Returns the visible text of every option currently selected on a
     * multi-select dropdown. Expects the locator for the dropdown, a
     * readable name for the dropdown for logging, and the name of the
     * calling method. Throws a DropdownActionException if the dropdown is
     * not a multi-select.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return the visible text of every option currently selected
     */
    public List<String> getAllSelectedOptionsText(By locator, String elementName, String methodName) {
        Select select = resolveMultiSelect(locator, elementName, methodName);
        List<String> selected = select.getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        LoggerUtil.info(methodName + " found " + selected.size() + " selected option(s): " + selected
                + " on dropdown: " + target(elementName, locator));
        return selected;
    }

    /**
     * Returns the visible text of every option the dropdown has,
     * regardless of which ones are selected. Expects the locator for the
     * dropdown, a readable name for the dropdown for logging, and the
     * name of the calling method.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return the visible text of every option the dropdown has
     */
    public List<String> getAllOptionsText(By locator, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        List<String> options = select.getOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        LoggerUtil.info(methodName + " found " + options.size() + " total option(s): " + options
                + " on dropdown: " + target(elementName, locator));
        return options;
    }

    /**
     * Returns how many options the dropdown has in total. Expects the
     * locator for the dropdown, a readable name for the dropdown for
     * logging, and the name of the calling method.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return how many options the dropdown has in total
     */
    public int getOptionsCount(By locator, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        int count = select.getOptions().size();
        LoggerUtil.info(methodName + " found options count: " + count
                + " on dropdown: " + target(elementName, locator));
        return count;
    }

    /**
     * Returns whether the dropdown allows more than one option to be
     * selected at once. Expects the locator for the dropdown, a readable
     * name for the dropdown for logging, and the name of the calling
     * method.
     *
     * @param locator     the locator for the dropdown
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return whether the dropdown allows more than one option to be selected at once
     */
    public boolean isMultiple(By locator, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        boolean multiple = select.isMultiple();
        LoggerUtil.info(methodName + " found isMultiple: " + multiple
                + " on dropdown: " + target(elementName, locator));
        return multiple;
    }

    /**
     * Returns whether the dropdown has an option with the given visible
     * text. Expects the locator for the dropdown, the visible text to
     * look for, a readable name for the dropdown for logging, and the
     * name of the calling method.
     *
     * @param locator     the locator for the dropdown
     * @param visibleText the visible text to look for
     * @param elementName a readable name for the dropdown, for logging
     * @param methodName  the name of the calling method, for logging
     * @return whether the dropdown has an option with the given visible text
     */
    public boolean hasOptionWithText(By locator, String visibleText, String elementName, String methodName) {
        boolean found = getAllOptionsText(locator, elementName, methodName).contains(visibleText);
        LoggerUtil.info(methodName + " checked for option '" + visibleText + "': " + found
                + " on dropdown: " + target(elementName, locator));
        return found;
    }

    /**
     * Waits for the dropdown element to be visible and wraps it in
     * Selenium's Select helper. Expects the locator for the dropdown, a
     * readable name for logging, and the name of the calling method.
     */
    private Select resolveSelect(By locator, String elementName, String methodName) {
        WebElement element = waitForVisible(locator, elementName, methodName);
        return new Select(element);
    }

    /**
     * Resolves the dropdown the same way as resolveSelect, but also
     * checks that it supports multiple selections. Expects the locator
     * for the dropdown, a readable name for logging, and the name of the
     * calling method. Throws a DropdownActionException if the dropdown
     * does not allow multiple selections.
     */
    private Select resolveMultiSelect(By locator, String elementName, String methodName) {
        Select select = resolveSelect(locator, elementName, methodName);
        if (!select.isMultiple()) {
            throw new DropdownActionException(
                    methodName + " - dropdown " + target(elementName, locator) + " is not a multi-select, cannot deselect options");
        }
        return select;
    }

    /**
     * Waits until the dropdown element is visible on the page. Expects
     * the locator for the dropdown, a readable name for logging, and the
     * name of the calling method. Returns the visible element. Throws a
     * DropdownActionException if the dropdown does not become visible
     * within the configured timeout.
     */
    private WebElement waitForVisible(By locator, String elementName, String methodName) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new DropdownActionException(
                    methodName + " - dropdown not visible within " + timeout + " for: " + target(elementName, locator), e);
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
