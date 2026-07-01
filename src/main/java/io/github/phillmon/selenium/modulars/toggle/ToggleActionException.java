package io.github.phillmon.selenium.modulars.toggle;

/**
 * Thrown when ToggleActions cannot confirm the expected on or off state
 * of a toggle, either because it did not change after being clicked or
 * because it did not become visible within the expected time.
 */
public class ToggleActionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public ToggleActionException(String message) {
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
    public ToggleActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
