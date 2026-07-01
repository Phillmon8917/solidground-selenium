package io.github.phillmon.selenium.modulars.network;

/**
 * Thrown when the network validation classes are configured with
 * arguments that do not make sense, such as an empty list of actions, a
 * blank url, or setting both a single status and a list of expected
 * statuses at the same time.
 */
public class InvalidNetworkExpectationException extends IllegalArgumentException {
    /**
     * Creates the exception with a message describing what part of the
     * configuration was invalid.
     *
     * @param message description of what part of the configuration was invalid
     */
    public InvalidNetworkExpectationException(String message) {
        super(message);
    }
}
