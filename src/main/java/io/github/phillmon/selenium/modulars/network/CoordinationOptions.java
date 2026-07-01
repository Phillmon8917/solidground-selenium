package io.github.phillmon.selenium.modulars.network;

import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;

import java.time.Duration;
import java.util.List;

/**
 * Holds the settings for coordinating a set of page actions with the
 * network responses they are expected to trigger: which actions to run,
 * which responses to wait for, and how long to wait for them.
 * NetworkValidationActions.coordinateActionsAndResponses uses this to run
 * the actions and then confirm every expected response actually arrived.
 */
public class CoordinationOptions {
    private final List<Runnable> actions;
    private final List<ResponseExpectation> waitForResponses;
    private final Duration timeout;

    /**
     * Creates options that run the given actions with no responses to
     * wait for, using the default timeout. Expects a non-empty list of
     * actions to run. Throws an InvalidNetworkExpectationException if the
     * list is null or empty.
     *
     * @param actions the actions to run, such as clicking a button that
     *                triggers a network request; must not be null or empty
     */
    public CoordinationOptions(List<Runnable> actions) {
        this(actions, List.of(), NetworkDefaults.DEFAULT_TIMEOUT);
    }

    /**
     * Creates options that run the given actions and then wait for the
     * given responses, using the default timeout. Expects a non-empty
     * list of actions to run and the list of responses expected to
     * result from them. Throws an InvalidNetworkExpectationException if
     * the action list is null or empty.
     *
     * @param actions          the actions to run, such as clicking a button
     *                         that triggers a network request; must not be
     *                         null or empty
     * @param waitForResponses the responses expected to result from the
     *                         actions
     */
    public CoordinationOptions(List<Runnable> actions, List<ResponseExpectation> waitForResponses) {
        this(actions, waitForResponses, NetworkDefaults.DEFAULT_TIMEOUT);
    }

    /**
     * Creates options that run the given actions, wait for the given
     * responses, and use a custom timeout. Expects a non-empty list of
     * actions to run, the list of responses expected to result from them
     * (which can be null or empty if none are expected), and how long to
     * wait for those responses. Throws an
     * InvalidNetworkExpectationException if the action list is null or
     * empty.
     *
     * @param actions          the actions to run, such as clicking a button
     *                         that triggers a network request; must not be
     *                         null or empty
     * @param waitForResponses the responses expected to result from the
     *                         actions, may be null or empty if none are
     *                         expected
     * @param timeout          how long to wait for the expected responses to
     *                         arrive, may be null to fall back to the
     *                         default timeout
     */
    public CoordinationOptions(List<Runnable> actions, List<ResponseExpectation> waitForResponses, Duration timeout) {
        if (actions == null || actions.isEmpty()) {
            throw new InvalidNetworkExpectationException("At least one action must be provided");
        }
        this.actions = actions;
        this.waitForResponses = waitForResponses != null ? waitForResponses : List.of();
        this.timeout = timeout != null ? TimeoutUtil.adjust(timeout) : NetworkDefaults.DEFAULT_TIMEOUT;
    }

    /**
     * Returns the actions that should be run, such as clicking a button
     * that triggers a network request.
     *
     * @return the actions that should be run
     */
    public List<Runnable> getActions() {
        return actions;
    }

    /**
     * Returns the responses that should be waited for and validated
     * after the actions run.
     *
     * @return the responses that should be waited for and validated
     */
    public List<ResponseExpectation> getWaitForResponses() {
        return waitForResponses;
    }

    /**
     * Returns how long to wait for the expected responses to arrive.
     *
     * @return how long to wait for the expected responses to arrive
     */
    public Duration getTimeout() {
        return timeout;
    }
}
