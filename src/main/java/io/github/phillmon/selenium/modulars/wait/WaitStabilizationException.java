package io.github.phillmon.selenium.modulars.wait;

/**
 * Thrown when SmartWaitActions cannot confirm that a value, element
 * position, or file download has settled into a stable state within the
 * expected time.
 */
public class WaitStabilizationException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed to stabilize
     */
    public WaitStabilizationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     *
     * @param message description of what failed to stabilize
     * @param cause the original exception that triggered this failure
     */
    public WaitStabilizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
