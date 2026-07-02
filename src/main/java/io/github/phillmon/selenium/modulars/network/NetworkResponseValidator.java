package io.github.phillmon.selenium.modulars.network;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.latest.network.Network;
import org.openqa.selenium.devtools.latest.network.model.Request;
import org.openqa.selenium.devtools.latest.network.model.RequestId;
import org.openqa.selenium.devtools.latest.network.model.Response;

import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listens to the browser's real network traffic through Chrome DevTools
 * and checks that expected requests actually happened with the expected
 * status codes and, optionally, response bodies. This is the engine
 * behind NetworkValidationActions: it records every request and response
 * the browser makes, then matches them against ResponseExpectation
 * objects supplied by the caller. Only works with ChromeDriver, since it
 * relies on Chrome's DevTools protocol.
 */
class NetworkResponseValidator {

    private final WebDriver driver;
    private final DevTools devTools;

    private final Map<RequestId, Request> requests = new ConcurrentHashMap<>();
    private final Map<RequestId, Response> responses = new ConcurrentHashMap<>();

    private static final List<Integer> DEFAULT_SUCCESS = List.of(200, 201, 204);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(300);

    /**
     * Opens a DevTools session on the given driver and starts recording
     * every network request and response the browser makes from this
     * point on. Expects a WebDriver that is actually a ChromeDriver.
     */
    NetworkResponseValidator(WebDriver driver) {
        if (!(driver instanceof ChromeDriver)) {
            throw new NetworkResponseValidationException(
                    "Network validation requires a ChromeDriver-backed WebDriver (Chrome DevTools "
                            + "Protocol is Chrome-only), but got: "
                            + (driver == null ? "null" : driver.getClass().getName()));
        }
        this.driver = driver;

        try {
            this.devTools = ((ChromeDriver) driver).getDevTools();

            devTools.createSession();
            devTools.send(Network.enable(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            ));

            devTools.addListener(Network.requestWillBeSent(), e ->
                    requests.put(e.getRequestId(), e.getRequest())
            );

            devTools.addListener(Network.responseReceived(), e ->
                    responses.put(e.getRequestId(), e.getResponse())
            );
        } catch (LinkageError | RuntimeException e) {
            throw new NetworkResponseValidationException(
                    "Failed to open a Chrome DevTools Protocol session for network validation. "
                            + "This library uses Selenium's selenium-devtools-latest artifact, which "
                            + "tracks whichever CDP version shipped with the selenium-java release this "
                            + "project depends on. If the installed Chrome is much newer than that "
                            + "Selenium release, bump both the selenium-java and selenium-devtools-latest "
                            + "dependency versions in this library's pom.xml to a more recent, matching pair.",
                    e);
        }
    }

    /**
     * Clears any previously recorded traffic, runs the given actions, and
     * then waits for and validates each expected response in turn.
     * Expects the actions to run, the responses expected as a result, and
     * how long to wait for each one. Throws a
     * NetworkResponseValidationException if any expected response does
     * not arrive in time or does not match what was expected.
     */
    void validateActionsAndResponses(List<Runnable> actions, List<ResponseExpectation> expectations, Duration timeout) {
        clear();

        LoggerUtil.info("Running " + actions.size() + " action(s), validating "
                + expectations.size() + " response(s) within " + timeout);

        actions.forEach(Runnable::run);

        if (expectations.isEmpty()) {
            return;
        }

        Set<RequestId> claimedIds = new HashSet<>();
        for (ResponseExpectation expectation : expectations) {
            RequestId id = awaitMatch(expectation, timeout, claimedIds, "response: " + expectation);
            claimedIds.add(id);
            validate(id, expectation);
            LoggerUtil.info("Validated response: " + expectation);
        }
    }

    /**
     * Clears any previously recorded traffic, runs the given actions,
     * waits for and validates the submit response, then waits for the
     * fetch response. If the fetch response does not appear within the
     * initial retry delay, the page is refreshed and the wait is tried
     * again with a longer timeout, since some pages need a reload before
     * showing the freshly submitted data. Expects the actions to run, the
     * expected submit response, the expected fetch response, how long to
     * wait for the submit response, how long to wait for the first fetch
     * attempt, and how long to wait for the fetch after the page is
     * refreshed. Throws a NetworkResponseValidationException if either
     * response does not arrive in time or does not match what was
     * expected.
     */
    void validateSubmitAndFetch(List<Runnable> actions, ResponseExpectation submit,
                                ResponseExpectation fetch, Duration timeout,
                                Duration fetchRetryDelay, Duration reloadTimeout) {
        clear();

        LoggerUtil.info("Running " + actions.size() + " action(s); validating submit="
                + submit + ", fetch=" + fetch);

        actions.forEach(Runnable::run);

        Set<RequestId> claimedIds = new HashSet<>();

        RequestId submitId = awaitMatch(submit, timeout, claimedIds, "submit response: " + submit);
        claimedIds.add(submitId);
        validate(submitId, submit);
        LoggerUtil.info("Validated submit response: " + submit);

        RequestId fetchId;
        try {
            fetchId = awaitMatch(fetch, fetchRetryDelay, claimedIds, "fetch response: " + fetch);
        } catch (NetworkResponseValidationException initialTimeout) {
            LoggerUtil.warning("Fetch response not found within " + fetchRetryDelay
                    + " for " + fetch + "; refreshing and retrying");
            driver.navigate().refresh();
            fetchId = awaitMatch(fetch, reloadTimeout, claimedIds, "fetch response after refresh: " + fetch);
        }
        claimedIds.add(fetchId);
        validate(fetchId, fetch);
        LoggerUtil.info("Validated fetch response: " + fetch);
    }

    /**
     * Polls the recorded traffic until a response matching the given
     * expectation shows up, ignoring any response id already claimed by
     * an earlier match. Expects the expectation to match, how long to
     * wait, the set of ids already claimed, and a description used in log
     * and error messages. Returns the id of the matching response. Throws
     * a NetworkResponseValidationException if no match appears within the
     * timeout, and logs the network traffic captured so far to help
     * diagnose the failure.
     */
    private RequestId awaitMatch(ResponseExpectation expectation, Duration timeout,
                                 Set<RequestId> claimedIds, String description) {
        RequestId[] holder = new RequestId[1];
        try {
            newWait(timeout).until(d -> {
                RequestId id = locateMatchId(expectation, claimedIds);
                holder[0] = id;
                return id != null;
            });
            return holder[0];
        } catch (org.openqa.selenium.TimeoutException te) {
            logNetworkState("Timed out waiting for " + description);
            throw new NetworkResponseValidationException(
                    "Timed out after " + timeout + " waiting for " + description, te);
        }
    }

    /**
     * Builds a FluentWait that polls the recorded traffic on a fixed
     * interval and ignores DevTools errors between polls, since those can
     * happen transiently while events are still being delivered. Expects
     * the maximum time to wait.
     */
    private Wait<WebDriver> newWait(Duration timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(POLL_INTERVAL)
                .ignoring(org.openqa.selenium.devtools.DevToolsException.class);
    }

    /**
     * Searches the recorded responses for one that matches the given
     * expectation and has not already been claimed by another
     * expectation. Expects the expectation to match and the set of ids
     * already claimed. Returns the matching request id, or null if
     * nothing matches yet.
     */
    private RequestId locateMatchId(ResponseExpectation expectation, Set<RequestId> claimedIds) {
        for (Map.Entry<RequestId, Response> entry : responses.entrySet()) {
            RequestId id = entry.getKey();
            if (claimedIds.contains(id)) {
                continue;
            }
            Response response = entry.getValue();
            Request request = requests.get(id);

            if (request == null) {
                continue;
            }

            if (matches(request, response, expectation)) {
                return id;
            }
        }
        return null;
    }

    /**
     * Checks whether a recorded request and response satisfy an
     * expectation's url, method, and status requirements. Expects the
     * recorded request, its response, and the expectation to check
     * against. Returns true only if every requirement that was actually
     * set on the expectation is satisfied.
     */
    private boolean matches(Request req, Response res, ResponseExpectation e) {
        boolean urlMatch = res.getUrl().contains(e.getUrl());

        boolean methodMatch = e.getMethod() == null ||
                req.getMethod().equalsIgnoreCase(e.getMethod());

        boolean statusMatch = e.getStatus() == null ||
                res.getStatus().intValue() == e.getStatus();

        return urlMatch && methodMatch && statusMatch;
    }

    /**
     * Confirms that a matched response's status code is acceptable, and
     * if a body validator was given, that the response body also passes
     * it. Expects the id of the matched response and the expectation it
     * matched. Throws a NetworkResponseValidationException if the status
     * is not one of the allowed codes, or if the body validator rejects
     * the response body.
     */
    private void validate(RequestId id, ResponseExpectation expectation) {
        Response response = responses.get(id);

        List<Integer> allowed = expectation.getExpectedStatuses() != null
                ? expectation.getExpectedStatuses()
                : (expectation.getStatus() != null ? List.of(expectation.getStatus()) : DEFAULT_SUCCESS);

        if (!allowed.contains(response.getStatus().intValue())) {
            String message = "Unexpected status " + response.getStatus() + " for " + expectation
                    + " (expected one of: " + allowed + ")";
            LoggerUtil.error(message);
            throw new NetworkResponseValidationException(message);
        }

        if (expectation.hasBodyValidator()) {
            String body = fetchResponseBody(id);
            if (!expectation.getBodyValidator().test(body)) {
                String message = "Response body validation failed for " + expectation
                        + "\nBody: " + truncate(body, 500);
                LoggerUtil.error(message);
                throw new NetworkResponseValidationException(message);
            }
        }
    }

    /**
     * Fetches the body text of a recorded response through DevTools.
     * Expects the id of the response to fetch. Returns the body text, or
     * an empty string if the body could not be retrieved, since a
     * fetching problem should not itself crash the validation.
     */
    private String fetchResponseBody(RequestId id) {
        try {
            Network.GetResponseBodyResponse body = devTools.send(Network.getResponseBody(id));
            return body.getBody();
        } catch (Exception e) {
            LoggerUtil.warning("Could not fetch response body for validation: " + e.getMessage());
            return "";
        }
    }

    /**
     * Shortens a piece of text to a maximum length for use in log
     * messages, adding a marker at the end when text was cut off. Expects
     * the text to shorten, which can be null, and the maximum length to
     * keep.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength) + "...[truncated]" : text;
    }

    /**
     * Logs every request and response recorded so far, along with its
     * status or a note that no response was received yet. Expects a short
     * description of why the log is being written, used as a heading.
     */
    void logNetworkState(String context) {
        if (requests.isEmpty()) {
            LoggerUtil.info(context + " - no network requests captured");
            return;
        }

        LoggerUtil.info(context + " - captured " + requests.size() + " request(s), "
                + responses.size() + " response(s):");

        for (Map.Entry<RequestId, Request> entry : requests.entrySet()) {
            RequestId id = entry.getKey();
            Request request = entry.getValue();
            Response response = responses.get(id);
            String status = response != null
                    ? String.valueOf(response.getStatus())
                    : "PENDING (no response received)";
            LoggerUtil.info("  [" + id + "] " + request.getMethod() + " " + request.getUrl() + " -> " + status);
        }
    }

    /**
     * Forgets every request and response recorded so far, so the next
     * validation starts with a clean slate.
     */
    void clear() {
        requests.clear();
        responses.clear();
    }

    /**
     * Closes the DevTools session opened by this validator. Call this
     * once network validation is no longer needed for the current
     * browser session.
     */
    void close() {
        LoggerUtil.info("Closing DevTools session for NetworkResponseValidator");
        devTools.close();
    }
}
