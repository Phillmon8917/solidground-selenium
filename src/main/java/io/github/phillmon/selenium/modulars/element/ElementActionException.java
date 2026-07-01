package io.github.phillmon.selenium.modulars.element;

/**
 * Thrown when ElementActions cannot find an element in the state it
 * needs, such as waiting for it to be visible, clickable, present, or
 * invisible, and the wait times out before that state is reached.
 */
public class ElementActionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public ElementActionException(String message) {
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
    public ElementActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
