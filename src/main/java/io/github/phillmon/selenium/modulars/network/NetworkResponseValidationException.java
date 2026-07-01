package io.github.phillmon.selenium.modulars.network;

/**
 * Thrown when NetworkResponseValidator cannot confirm that an expected
 * network response arrived in time, or when a response arrives but does
 * not match the expected status code or body content.
 */
public class NetworkResponseValidationException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public NetworkResponseValidationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     *
     * @param message description of what failed
     * @param cause the original exception that triggered this failure
     */
    public NetworkResponseValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
