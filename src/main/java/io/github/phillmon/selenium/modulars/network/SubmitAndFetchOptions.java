package io.github.phillmon.selenium.modulars.network;

import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;

import java.time.Duration;
import java.util.List;

/**
 * Holds the settings for the common pattern of submitting something and
 * then fetching the result, where the fetch might not show up
 * immediately. Describes the actions to run, the response expected for
 * the submit request, the response expected for the fetch request, and
 * how long to wait at each stage, including a page refresh and retry if
 * the fetch response does not appear the first time.
 */
public class SubmitAndFetchOptions {
    private final List<Runnable> actions;
    private final ResponseExpectation submitResponse;
    private final ResponseExpectation fetchResponse;
    private final Duration timeout;
    private final Duration fetchRetryDelay;
    private final Duration reloadTimeout;

    /**
     * Creates options using every default timing value. Expects a
     * non-empty list of actions to run, the response expected for the
     * submit, and the response expected for the fetch. Throws an
     * InvalidNetworkExpectationException if the actions are missing or
     * either response expectation is null.
     *
     * @param actions        the actions to run, such as clicking a submit button; must not be null or empty
     * @param submitResponse the response expected to confirm the submit succeeded; must not be null
     * @param fetchResponse  the response expected when fetching the result of the submit; must not be null
     */
    public SubmitAndFetchOptions(List<Runnable> actions, ResponseExpectation submitResponse, ResponseExpectation fetchResponse) {
        this(actions, submitResponse, fetchResponse, NetworkDefaults.DEFAULT_TIMEOUT,
                NetworkDefaults.FETCH_RETRY_DELAY, NetworkDefaults.RELOAD_TIMEOUT);
    }

    /**
     * Creates options with a custom overall timeout but default retry and
     * reload timing. Expects a non-empty list of actions to run, the
     * response expected for the submit, the response expected for the
     * fetch, and how long to wait for the submit response. Throws an
     * InvalidNetworkExpectationException if the actions are missing or
     * either response expectation is null.
     *
     * @param actions        the actions to run, such as clicking a submit button; must not be null or empty
     * @param submitResponse the response expected to confirm the submit succeeded; must not be null
     * @param fetchResponse  the response expected when fetching the result of the submit; must not be null
     * @param timeout        how long to wait for the submit response, or null to use the default
     */
    public SubmitAndFetchOptions(List<Runnable> actions, ResponseExpectation submitResponse,
                                 ResponseExpectation fetchResponse, Duration timeout) {
        this(actions, submitResponse, fetchResponse, timeout,
                NetworkDefaults.FETCH_RETRY_DELAY, NetworkDefaults.RELOAD_TIMEOUT);
    }

    /**
     * Creates options with full control over every timing value. Expects
     * a non-empty list of actions to run, the response expected for the
     * submit, the response expected for the fetch, how long to wait for
     * the submit response, how long to wait before giving up on the
     * first fetch attempt and refreshing the page, and how long to wait
     * for the fetch response after that refresh. Throws an
     * InvalidNetworkExpectationException if the actions are missing or
     * either response expectation is null.
     *
     * @param actions         the actions to run, such as clicking a submit button; must not be null or empty
     * @param submitResponse  the response expected to confirm the submit succeeded; must not be null
     * @param fetchResponse   the response expected when fetching the result of the submit; must not be null
     * @param timeout         how long to wait for the submit response, or null to use the default
     * @param fetchRetryDelay how long to wait for the fetch response before giving up and refreshing the page, or null to use the default
     * @param reloadTimeout   how long to wait for the fetch response after the page has been refreshed, or null to use the default
     */
    public SubmitAndFetchOptions(List<Runnable> actions, ResponseExpectation submitResponse, ResponseExpectation fetchResponse,
                                 Duration timeout, Duration fetchRetryDelay, Duration reloadTimeout) {
        if (actions == null || actions.isEmpty()) {
            throw new InvalidNetworkExpectationException("At least one action must be provided");
        }
        if (submitResponse == null || fetchResponse == null) {
            throw new InvalidNetworkExpectationException("submitResponse and fetchResponse must both be provided");
        }
        this.actions = actions;
        this.submitResponse = submitResponse;
        this.fetchResponse = fetchResponse;
        this.timeout = timeout != null ? TimeoutUtil.adjust(timeout) : NetworkDefaults.DEFAULT_TIMEOUT;
        this.fetchRetryDelay = fetchRetryDelay != null ? TimeoutUtil.adjust(fetchRetryDelay) : NetworkDefaults.FETCH_RETRY_DELAY;
        this.reloadTimeout = reloadTimeout != null ? TimeoutUtil.adjust(reloadTimeout) : NetworkDefaults.RELOAD_TIMEOUT;
    }

    /**
     * Returns the actions that should be run, such as clicking a submit
     * button.
     *
     * @return the actions to run
     */
    public List<Runnable> getActions() {
        return actions;
    }

    /**
     * Returns the response expected to confirm the submit succeeded.
     *
     * @return the expected submit response
     */
    public ResponseExpectation getSubmitResponse() {
        return submitResponse;
    }

    /**
     * Returns the response expected when fetching the result of the
     * submit.
     *
     * @return the expected fetch response
     */
    public ResponseExpectation getFetchResponse() {
        return fetchResponse;
    }

    /**
     * Returns how long to wait for the submit response.
     *
     * @return the submit response timeout
     */
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * Returns how long to wait for the fetch response before giving up
     * and refreshing the page to try again.
     *
     * @return the fetch retry delay
     */
    public Duration getFetchRetryDelay() {
        return fetchRetryDelay;
    }

    /**
     * Returns how long to wait for the fetch response after the page has
     * been refreshed.
     *
     * @return the reload timeout
     */
    public Duration getReloadTimeout() {
        return reloadTimeout;
    }
}
