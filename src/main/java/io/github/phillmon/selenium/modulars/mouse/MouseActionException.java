package io.github.phillmon.selenium.modulars.mouse;

/**
 * Thrown when MouseActions cannot find an element in the state it needs
 * within the expected time, or when a chained gesture such as hoverChain
 * is given arguments that do not match up, or is interrupted while
 * running a slow drag and drop.
 */
public class MouseActionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public MouseActionException(String message) {
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
    public MouseActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
