package io.github.phillmon.selenium.modulars.keyboard;

/**
 * Thrown when KeyboardActions cannot find the element it needs to send
 * keys to within the expected time.
 */
public class KeyboardActionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public KeyboardActionException(String message) {
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
    public KeyboardActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
