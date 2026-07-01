package io.github.phillmon.selenium.modulars.reader;

/**
 * Thrown when a document reader cannot download, read, or delete a
 * document, or when a DocumentSource or reader is configured with
 * arguments that do not make sense, such as a missing url or path.
 */
public class ReaderException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     */
    public ReaderException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     */
    public ReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
