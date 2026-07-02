package io.github.phillmon.selenium.modulars.network;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.openqa.selenium.WebDriver;

/**
 * The page-object facing entry point for verifying real network traffic
 * during a test, such as confirming that clicking a button actually sent
 * the expected request and got back the expected response. Wraps
 * NetworkResponseValidator, which does the actual work of listening to
 * Chrome DevTools.
 */
public class NetworkValidationActions {
    private final WebDriver driver;
    private volatile NetworkResponseValidator validator;

    /**
     * Creates the network validation actions for the given driver. The
     * DevTools session itself is not opened here: it is deferred until the
     * first call that actually needs to validate network traffic, so a
     * page object that never uses network validation never pays for a CDP
     * session and does not require a Chrome-backed driver.
     *
     * @param driver the Chrome-backed WebDriver to record network traffic for
     */
    public NetworkValidationActions(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Returns the DevTools-backed validator, opening its CDP session on
     * first use. Expects a Chrome-backed WebDriver, since network
     * recording relies on Chrome's DevTools protocol.
     */
    private NetworkResponseValidator validator() {
        NetworkResponseValidator result = validator;
        if (result == null) {
            synchronized (this) {
                result = validator;
                if (result == null) {
                    LoggerUtil.info("Opening DevTools session for network validation");
                    result = new NetworkResponseValidator(driver);
                    validator = result;
                }
            }
        }
        return result;
    }

    /**
     * Runs a set of actions and confirms that every expected response
     * described in the given options actually arrived with the expected
     * status and, if configured, body. Expects a CoordinationOptions
     * describing the actions and the responses to wait for.
     *
     * @param options the actions to run and the responses to wait for
     */
    public void coordinateActionsAndResponses(CoordinationOptions options) {
        validator().validateActionsAndResponses(options.getActions(), options.getWaitForResponses(), options.getTimeout());
    }

    /**
     * Runs a set of actions, confirms the submit response arrives as
     * expected, then waits for the fetch response, automatically
     * refreshing the page and retrying once if the fetch response does
     * not show up right away. Expects a SubmitAndFetchOptions describing
     * the actions, the expected submit and fetch responses, and the
     * timing to use.
     *
     * @param options the actions, expected responses, and timing to coordinate
     */
    public void coordinateSubmitAndFetchWithReloadRecovery(SubmitAndFetchOptions options) {
        validator().validateSubmitAndFetch(
                options.getActions(),
                options.getSubmitResponse(),
                options.getFetchResponse(),
                options.getTimeout(),
                options.getFetchRetryDelay(),
                options.getReloadTimeout()
        );
    }

    /**
     * Closes the underlying DevTools session, if one was ever opened. Call
     * this once network validation is no longer needed for the current
     * browser session. Safe to call even if no network validation method
     * was ever used, since no session would have been opened.
     */
    public void close() {
        if (validator != null) {
            LoggerUtil.info("Closing NetworkValidationActions");
            validator.close();
        }
    }
}
