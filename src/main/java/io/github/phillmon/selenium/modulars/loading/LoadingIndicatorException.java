package io.github.phillmon.selenium.modulars.loading;

/**
 * Thrown when LoadingIndicatorActions cannot confirm that a loading
 * indicator has disappeared within the expected time, or when the
 * arguments given to wait for several indicators at once do not match up.
 */
public class LoadingIndicatorException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public LoadingIndicatorException(String message) {
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
    public LoadingIndicatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
