package io.github.phillmon.selenium.video;

/**
 * Thrown when VideoRecorder cannot start, capture, or finalize a video
 * recording, such as when the driver is not Chrome-backed or the output
 * file cannot be written.
 */
public class VideoRecordingException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     *
     * @param message description of what failed
     */
    public VideoRecordingException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     *
     * @param message description of what failed
     * @param cause   the original exception that triggered this failure
     */
    public VideoRecordingException(String message, Throwable cause) {
        super(message, cause);
    }
}
