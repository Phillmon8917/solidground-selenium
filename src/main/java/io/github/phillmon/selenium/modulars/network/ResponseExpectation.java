package io.github.phillmon.selenium.modulars.network;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Describes one network response that a test expects to see: which url it
 * should come from, optionally which HTTP method the request should use,
 * and optionally which status code (or codes) and body content the
 * response should have. NetworkResponseValidator uses this description to
 * find and check the right response among all the network traffic the
 * browser generates.
 */
public class ResponseExpectation {

    private final String url;
    private final String method;
    private final Integer status;
    private final List<Integer> expectedStatuses;
    private final Predicate<String> bodyValidator;

    /**
     * Creates an expectation for any response whose url contains the
     * given text, with no restriction on method or status. Expects a
     * non-blank url fragment to match against.
     *
     * @param url the url fragment the response must contain
     */
    public ResponseExpectation(String url) {
        this(url, null, null, null, null);
    }

    /**
     * Creates an expectation for a response matching both the url and the
     * HTTP method. Expects a non-blank url fragment and the HTTP method
     * the request should use, such as "GET" or "POST".
     *
     * @param url    the url fragment the response must contain
     * @param method the HTTP method the request should use, such as "GET" or "POST"
     */
    public ResponseExpectation(String url, String method) {
        this(url, method, null, null, null);
    }

    /**
     * Creates an expectation for a response matching the url, method, and
     * a single expected status code. Expects a non-blank url fragment,
     * the HTTP method, and the status code the response must have.
     *
     * @param url    the url fragment the response must contain
     * @param method the HTTP method the request should use, such as "GET" or "POST"
     * @param status the status code the response must have
     */
    public ResponseExpectation(String url, String method, Integer status) {
        this(url, method, status, null, null);
    }

    /**
     * Creates an expectation for a response matching the url and method,
     * where the status must be one of several acceptable codes. Expects a
     * non-blank url fragment, the HTTP method, and the list of status
     * codes that are all considered acceptable.
     *
     * @param url              the url fragment the response must contain
     * @param method           the HTTP method the request should use, such as "GET" or "POST"
     * @param expectedStatuses the list of status codes that are all considered acceptable
     */
    public ResponseExpectation(String url, String method, List<Integer> expectedStatuses) {
        this(url, method, null, expectedStatuses, null);
    }

    private ResponseExpectation(String url, String method, Integer status,
                                List<Integer> expectedStatuses, Predicate<String> bodyValidator) {
        if (url == null || url.isBlank()) {
            throw new InvalidNetworkExpectationException("url must not be null or blank");
        }
        if (status != null && expectedStatuses != null) {
            throw new InvalidNetworkExpectationException("Cannot set both status and expectedStatuses");
        }
        this.url = url;
        this.method = method;
        this.status = status;
        this.expectedStatuses = expectedStatuses;
        this.bodyValidator = bodyValidator;
    }

    /**
     * Builds one ResponseExpectation per given status code, all matching
     * the same url and method, useful for setting up separate checks for
     * a request that could reasonably come back with several different
     * codes. Expects a non-blank url, the HTTP method, and at least one
     * status code. Throws an InvalidNetworkExpectationException if no
     * status codes are given.
     *
     * @param url      the url fragment the response must contain
     * @param method   the HTTP method the request should use, such as "GET" or "POST"
     * @param statuses the status codes to build one expectation each for; at least one must be given
     * @return one ResponseExpectation per given status code, all matching the same url and method
     */
    public static List<ResponseExpectation> forStatuses(String url, String method, Integer... statuses) {
        if (statuses == null || statuses.length == 0) {
            throw new InvalidNetworkExpectationException("At least one status code must be provided");
        }
        return Arrays.stream(statuses)
                .map(status -> new ResponseExpectation(url, method, status))
                .toList();
    }

    /**
     * Returns a copy of this expectation with a body validator attached,
     * so the response body is checked as well as the url, method, and
     * status. Expects a predicate that returns true when the response
     * body text is acceptable.
     *
     * @param bodyValidator predicate that returns true when the response body text is acceptable
     * @return a copy of this expectation with the given body validator attached
     */
    public ResponseExpectation withBodyValidator(Predicate<String> bodyValidator) {
        return new ResponseExpectation(this.url, this.method, this.status, this.expectedStatuses, bodyValidator);
    }

    /**
     * Returns the url fragment this expectation matches against.
     *
     * @return the url fragment this expectation matches against
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the HTTP method this expectation requires, or null if any
     * method is acceptable.
     *
     * @return the HTTP method this expectation requires, or null if any method is acceptable
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns the single status code this expectation requires, or null
     * if a list of statuses was used instead, or if no status was
     * specified at all.
     *
     * @return the single status code this expectation requires, or null if not applicable
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Returns the list of acceptable status codes for this expectation,
     * or null if a single status was used instead, or if no status was
     * specified at all.
     *
     * @return the list of acceptable status codes for this expectation, or null if not applicable
     */
    public List<Integer> getExpectedStatuses() {
        return expectedStatuses;
    }

    /**
     * Returns the predicate used to check the response body, or null if
     * this expectation does not check the body.
     *
     * @return the predicate used to check the response body, or null if this expectation does not check the body
     */
    public Predicate<String> getBodyValidator() {
        return bodyValidator;
    }

    /**
     * Returns whether a body validator has been attached to this
     * expectation.
     *
     * @return true if a body validator has been attached to this expectation, false otherwise
     */
    public boolean hasBodyValidator() {
        return bodyValidator != null;
    }

    /**
     * Returns a readable summary of this expectation's url, method, and
     * status, used in log messages and exceptions.
     *
     * @return a readable summary of this expectation's url, method, and status
     */
    @Override
    public String toString() {
        return "url=" + url
                + ", method=" + (method != null ? method : "ANY")
                + ", status=" + (status != null ? status
                : (expectedStatuses != null ? expectedStatuses : "DEFAULT_SUCCESS"));
    }
}
