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
    private final NetworkResponseValidator validator;

    /**
     * Creates the network validation actions and starts recording network
     * traffic for the given driver. Expects a WebDriver backed by Chrome,
     * since network recording relies on Chrome's DevTools protocol.
     *
     * @param driver the Chrome-backed WebDriver to record network traffic for
     */
    public NetworkValidationActions(WebDriver driver) {
        this.validator = new NetworkResponseValidator(driver);
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
        validator.validateActionsAndResponses(options.getActions(), options.getWaitForResponses(), options.getTimeout());
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
        validator.validateSubmitAndFetch(
                options.getActions(),
                options.getSubmitResponse(),
                options.getFetchResponse(),
                options.getTimeout(),
                options.getFetchRetryDelay(),
                options.getReloadTimeout()
        );
    }

    /**
     * Closes the underlying DevTools session. Call this once network
     * validation is no longer needed for the current browser session.
     */
    public void close() {
        LoggerUtil.info("Closing NetworkValidationActions");
        validator.close();
    }
}
