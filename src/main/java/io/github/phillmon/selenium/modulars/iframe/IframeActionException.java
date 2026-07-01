package io.github.phillmon.selenium.modulars.iframe;

/**
 * Thrown when IframeActions cannot switch into a requested frame, such as
 * when the frame never becomes available within the timeout, when
 * switching to a parent frame is attempted from the top-level page, or
 * when nested frame arguments do not match up.
 */
public class IframeActionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public IframeActionException(String message) {
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
    public IframeActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
