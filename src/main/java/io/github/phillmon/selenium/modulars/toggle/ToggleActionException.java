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
     */
    public ToggleActionException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     */
    public ToggleActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
