package io.github.phillmon.selenium.modulars.wait;

import io.github.phillmon.selenium.modulars.calendar.DateFormat;
import io.github.phillmon.selenium.modulars.calendar.DateObject;
import io.github.phillmon.selenium.modulars.calendar.InvalidDateException;
import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Waits for a value to stop changing rather than just checking it once,
 * which is useful for values that update gradually, such as text being
 * typed by a script, an input still being auto-formatted, an element
 * still animating into position, or a file still being downloaded. A
 * value only counts as stable once it reads the same on several checks in
 * a row.
 */
public class SmartWaitActions {
    private final WebDriver driver;
    private final Duration pollInterval;
    private final int requiredStableChecks;
    private final Duration timeout;

    /**
     * Creates the smart wait actions using the default settings: checking
     * every 250 milliseconds, requiring 3 matching checks in a row, and an
     * overall timeout of 10 seconds. Expects the WebDriver for the
     * current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public SmartWaitActions(WebDriver driver) {
        this(driver, Duration.ofMillis(250), 3, Duration.ofSeconds(10));
    }

    /**
     * Creates the smart wait actions with a custom overall timeout, using
     * the default poll interval of 250 milliseconds and requiring 3
     * matching checks in a row. Expects the WebDriver for the current
     * browser session and how long to wait for a value to stabilize.
     *
     * @param driver  the WebDriver for the current browser session
     * @param timeout how long to wait overall for a value to stabilize
     */
    public SmartWaitActions(WebDriver driver, Duration timeout) {
        this(driver, Duration.ofMillis(250), 3, timeout);
    }

    /**
     * Creates the smart wait actions with full control over the polling
     * behaviour. Expects the WebDriver for the current browser session,
     * how often to re-check the value, how many matching checks in a row
     * are needed before the value counts as stable, and the overall
     * timeout.
     *
     * @param driver               the WebDriver for the current browser session
     * @param pollInterval         how often to re-check the value while waiting
     * @param requiredStableChecks how many matching checks in a row are needed before the value counts as stable
     * @param timeout              how long to wait overall for a value to stabilize
     */
    public SmartWaitActions(WebDriver driver, Duration pollInterval, int requiredStableChecks, Duration timeout) {
        this.driver = driver;
        this.pollInterval = pollInterval;
        this.requiredStableChecks = requiredStableChecks;
        this.timeout = TimeoutUtil.adjust(timeout);
    }

    /**
     * Waits until an input element's value stops changing, without
     * treating the value as sensitive in the log. Expects the locator for
     * the input, a readable name for logging, and the name of the calling
     * method. Returns the stabilized value.
     *
     * @param locator     the locator for the input element to watch
     * @param elementName a readable name for the element, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     * @return the stabilized input value
     */
    public String waitForInputValueToStabilize(By locator, String elementName, String methodName) {
        return waitForInputValueToStabilize(locator, false, elementName, methodName);
    }

    /**
     * Waits until an input element's value stops changing. Expects the
     * locator for the input, whether the value is sensitive and should be
     * hidden from the log, a readable name for logging, and the name of
     * the calling method. Returns the stabilized value. Throws a
     * WaitStabilizationException if the value never settles within the
     * configured timeout.
     *
     * @param locator     the locator for the input element to watch
     * @param sensitive   whether the value is sensitive and should be hidden from the log
     * @param elementName a readable name for the element, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     * @return the stabilized input value
     */
    public String waitForInputValueToStabilize(By locator, boolean sensitive, String elementName, String methodName) {
        return waitUntilStable(
                () -> driver.findElement(locator).getAttribute("value"),
                sensitive, methodName, "input value for " + target(elementName, locator));
    }

    /**
     * Waits until an element's visible text stops changing. Expects the
     * locator for the element, a readable name for logging, and the name
     * of the calling method. Returns the stabilized text. Throws a
     * WaitStabilizationException if the text never settles within the
     * configured timeout.
     *
     * @param locator     the locator for the element to watch
     * @param elementName a readable name for the element, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     * @return the stabilized visible text
     */
    public String waitForTextToStabilize(By locator, String elementName, String methodName) {
        return waitUntilStable(
                () -> driver.findElement(locator).getText(),
                methodName, "text for " + target(elementName, locator));
    }

    /**
     * Waits until the value of an attribute on an element stops changing.
     * Expects the locator for the element, the attribute to watch, a
     * readable name for logging, and the name of the calling method.
     * Returns the stabilized attribute value. Throws a
     * WaitStabilizationException if the value never settles within the
     * configured timeout.
     *
     * @param locator     the locator for the element to watch
     * @param attribute   the name of the attribute to watch
     * @param elementName a readable name for the element, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     * @return the stabilized attribute value
     */
    public String waitForAttributeToStabilize(By locator, String attribute, String elementName, String methodName) {
        return waitUntilStable(
                () -> driver.findElement(locator).getAttribute(attribute),
                methodName, "attribute '" + attribute + "' for " + target(elementName, locator));
    }

    /**
     * Waits until the number of elements matching a locator stops
     * changing, useful for lists that load their items gradually. Expects
     * the locator to count, a readable name for logging, and the name of
     * the calling method. Returns the stabilized count. Throws a
     * WaitStabilizationException if the count never settles within the
     * configured timeout.
     *
     * @param locator     the locator whose matching elements should be counted
     * @param elementName a readable name for the elements, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     * @return the stabilized element count
     */
    public int waitForElementCountToStabilize(By locator, String elementName, String methodName) {
        return waitUntilStable(
                () -> driver.findElements(locator).size(),
                methodName, "element count for " + target(elementName, locator));
    }

    /**
     * Waits until an element's position and size on the page stop
     * changing, useful for elements that are still animating. Expects the
     * locator for the element, a readable name for logging, and the name
     * of the calling method. Throws a WaitStabilizationException if the
     * element never settles within the configured timeout.
     *
     * @param locator     the locator for the element to watch
     * @param elementName a readable name for the element, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     */
    public void waitForElementToStopMoving(By locator, String elementName, String methodName) {
        waitUntilStable(() -> {
            Rectangle rect = driver.findElement(locator).getRect();
            return rect.getX() + "," + rect.getY() + "," + rect.getWidth() + "," + rect.getHeight();
        }, methodName, "position/size for " + target(elementName, locator));
    }

    /**
     * Waits until a date input's value stops changing and then parses it
     * using the given date format. Expects the locator for the input, the
     * date format the input uses, a readable name for logging, and the
     * name of the calling method. Returns the parsed DateObject. Throws a
     * WaitStabilizationException if the stabilized value cannot be parsed
     * as a valid date in the expected format.
     *
     * @param locator     the locator for the date input to watch
     * @param format      the date format the input uses
     * @param elementName a readable name for the element, used in log and error messages
     * @param methodName  the name of the calling method, for logging
     * @return the parsed date once the input value has stabilized
     */
    public DateObject waitForDateInputToStabilize(By locator, DateFormat format, String elementName, String methodName) {
        String stableValue = waitForInputValueToStabilize(locator, elementName, methodName);
        try {
            DateObject date = DateObject.fromString(stableValue, format);
            LoggerUtil.info(methodName + " stabilized date input resolved to: " + date
                    + " for element: " + target(elementName, locator));
            return date;
        } catch (InvalidDateException e) {
            throw new WaitStabilizationException(
                    methodName + " - stabilized input value '" + stableValue
                            + "' for " + target(elementName, locator) + " is not a valid date in the expected format", e);
        }
    }

    /**
     * Waits for a file matching a name pattern to appear in a download
     * folder and finish downloading, ignoring browser-specific
     * in-progress file extensions, then waits for its size to stop
     * changing. Expects the folder to watch, a regular expression the
     * final file name must match, and the name of the calling method for
     * logging. Returns the completed file. Throws a
     * WaitStabilizationException if no matching completed file appears
     * within the configured timeout.
     *
     * @param downloadDirectory the folder to watch for the downloaded file
     * @param fileNamePattern   a regular expression the final file name must match
     * @param methodName        the name of the calling method, for logging
     * @return the completed, stabilized file
     */
    public File waitForFileDownloadToComplete(Path downloadDirectory, String fileNamePattern, String methodName) {
        Pattern pattern = Pattern.compile(fileNamePattern);
        Instant deadline = Instant.now().plus(timeout);
        File matchedFile = null;

        while (Instant.now().isBefore(deadline)) {
            File[] candidates = downloadDirectory.toFile().listFiles((dir, name) ->
                    pattern.matcher(name).matches()
                            && !name.endsWith(".crdownload")
                            && !name.endsWith(".tmp")
                            && !name.endsWith(".part"));

            if (candidates != null && candidates.length > 0) {
                matchedFile = candidates[0];
                break;
            }
            sleep(pollInterval);
        }

        if (matchedFile == null) {
            throw new WaitStabilizationException(
                    methodName + " - no completed file matching pattern '" + fileNamePattern
                            + "' appeared in " + downloadDirectory + " within " + timeout);
        }

        File finalFile = matchedFile;
        waitUntilStable(finalFile::length, methodName, "file size for " + finalFile.getName());

        LoggerUtil.info(methodName + " download stabilized: " + finalFile.getAbsolutePath());
        return finalFile;
    }

    /**
     * Repeatedly reads a value until it comes back the same on enough
     * checks in a row to count as stable, without treating the value as
     * sensitive in the log. Expects a supplier that reads the current
     * value, the name of the calling method for logging, and a
     * description of what is being waited on, used in log and error
     * messages. Returns the stabilized value.
     */
    private <T> T waitUntilStable(Supplier<T> valueSupplier, String methodName, String description) {
        return waitUntilStable(valueSupplier, false, methodName, description);
    }

    /**
     * Repeatedly reads a value until it comes back the same on enough
     * checks in a row to count as stable. Expects a supplier that reads
     * the current value, whether the value is sensitive and should be
     * hidden from the log, the name of the calling method for logging,
     * and a description of what is being waited on, used in log and
     * error messages. Returns the stabilized value. A stale element
     * reference resets the stability count rather than failing
     * immediately, since the element may simply have been redrawn. Throws
     * a WaitStabilizationException if the value never stabilizes within
     * the configured timeout.
     */
    private <T> T waitUntilStable(Supplier<T> valueSupplier, boolean sensitive, String methodName, String description) {
        Instant deadline = Instant.now().plus(timeout);
        T lastValue = null;
        int stableCount = 0;
        boolean first = true;

        while (Instant.now().isBefore(deadline)) {
            T current;
            try {
                current = valueSupplier.get();
            } catch (StaleElementReferenceException e) {
                stableCount = 0;
                first = true;
                sleep(pollInterval);
                continue;
            }

            if (!first && Objects.equals(current, lastValue)) {
                stableCount++;
            } else {
                stableCount = 1;
            }

            lastValue = current;
            first = false;

            if (stableCount >= requiredStableChecks) {
                LoggerUtil.info(methodName + " - " + description + " stabilized at: " + mask(current, sensitive));
                return current;
            }

            sleep(pollInterval);
        }

        throw new WaitStabilizationException(
                methodName + " - " + description + " did not stabilize within " + timeout
                        + ". Last observed value: '" + mask(lastValue, sensitive) + "'");
    }

    /**
     * Builds a readable description of an element combining its name and
     * its locator, used in log and exception messages.
     */
    private String target(String elementName, By locator) {
        return elementName + " (" + locator + ")";
    }

    /**
     * Returns the value's text form as is, or a row of asterisks if it
     * should be hidden from the log because it is sensitive.
     */
    private String mask(Object value, boolean sensitive) {
        return sensitive ? "*******" : String.valueOf(value);
    }

    /**
     * Pauses the current thread for the given duration, used between
     * polling attempts. Throws a WaitStabilizationException if the thread
     * is interrupted while waiting.
     */
    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WaitStabilizationException("Interrupted while polling for stability", e);
        }
    }
}
