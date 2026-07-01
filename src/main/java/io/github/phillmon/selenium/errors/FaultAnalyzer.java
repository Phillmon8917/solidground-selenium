package io.github.phillmon.selenium.errors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

/**
 * Turns a raised exception into a FaultDetails record and reads plain
 * information out of an exception, such as its message and its stack trace.
 * This class does not log or store anything, it only reads and formats
 * data from a Throwable so other classes in this package can use it.
 */
public final class FaultAnalyzer {
    private FaultAnalyzer() {
    }

    /**
     * Builds a FaultDetails record for the given error. It expects the
     * error itself, the name of the place where the error happened (the
     * source), and an optional context string that adds more detail about
     * what was going on when the error happened. The context can be null.
     * The returned FaultDetails contains the current time, the source, the
     * context, the full class name of the exception, its message, and its
     * full stack trace as text.
     */
    public static FaultDetails createDetails(Throwable error, String source, String context) {
        return new FaultDetails(
                Instant.now().toString(),
                source,
                context,
                error.getClass().getName(),
                getMessage(error),
                stackTraceOf(error)
        );
    }

    /**
     * Returns a readable message for the given error. If the error has its
     * own message, that message is returned as is. If the error has no
     * message, or the message is blank, the simple class name of the
     * error is returned instead so the caller always gets some text back.
     */
    public static String getMessage(Throwable error) {
        String message = error.getMessage();
        return (message == null || message.isBlank()) ? error.getClass().getSimpleName() : message;
    }

    /**
     * Converts the stack trace of the given error into a plain text string,
     * the same text that would be printed to the console if the error was
     * printed directly.
     */
    private static String stackTraceOf(Throwable error) {
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
