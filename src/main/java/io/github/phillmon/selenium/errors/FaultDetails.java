package io.github.phillmon.selenium.errors;

/**
 * Holds all the information collected about one failure: when it happened,
 * where it happened, what extra context was given, what kind of exception
 * it was, its message, and its stack trace. FaultAnalyzer builds these and
 * FaultReporter writes them to the log.
 *
 * @param timestamp     when the failure was recorded
 * @param source        the page object, class, or method the failure came from
 * @param context       extra caller-supplied detail about what was happening, may be blank
 * @param exceptionType the fully qualified name of the exception type
 * @param message       the exception's message
 * @param stackTrace    the exception's full stack trace as text
 */
public record FaultDetails(
        String timestamp,
        String source,
        String context,
        String exceptionType,
        String message,
        String stackTrace
) {
    /**
     * Formats all the fields into one plain text block that can be written
     * straight to the log file. The context line is only included when a
     * context was actually given. The full stack trace is always added on
     * a new line at the end so the log has enough detail to debug the
     * failure.
     *
     * @return the formatted fault details as a plain text log block
     */
    public String toLogString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[FAULT] timestamp=").append(timestamp)
                .append(" source=").append(source);
        if (context != null && !context.isBlank()) {
            sb.append(" context=").append(context);
        }
        sb.append(" type=").append(exceptionType)
                .append(" message=").append(message)
                .append(System.lineSeparator())
                .append(stackTrace);
        return sb.toString();
    }
}
