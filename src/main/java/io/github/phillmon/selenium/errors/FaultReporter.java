package io.github.phillmon.selenium.errors;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;

/**
 * Central place where failures get logged. Every part of the framework
 * that catches an exception and wants it recorded should call into this
 * class instead of logging the exception itself. It builds a structured
 * fault record, makes sure the same failure is not logged twice, and falls
 * back to a simple message if the structured logging itself fails.
 */
public final class FaultReporter {
    private FaultReporter() {
    }

    /**
     * Reports a failure with no extra context. Expects the error that was
     * caught and the name of the place where it happened (the source).
     * This is the same as calling captureFault(error, source, null).
     */
    public static void captureFault(Throwable error, String source) {
        captureFault(error, source, null);
    }

    /**
     * Reports a failure together with extra context about what was
     * happening when it occurred. Expects the error that was caught, the
     * source it came from, and an optional context string which can be
     * null. If the error is null, or the same error was already reported
     * before, nothing happens. Otherwise this builds a fault record and
     * writes it to the log through LoggerUtil. If writing the log itself
     * fails for any reason, a simpler fallback message is logged instead.
     */
    public static void captureFault(Throwable error, String source, String context) {
        if (error == null || !FaultCache.shouldReport(error)) {
            return;
        }

        try {
            FaultDetails details = FaultAnalyzer.createDetails(error, source, context);
            LoggerUtil.error(details.toLogString(), error);
        } catch (Exception loggingFailure) {
            handleLoggingFailure(loggingFailure, source);
        }
    }

    /**
     * Creates a new failure out of the given message, reports it, and then
     * throws it. Expects the source it came from and the message to use.
     * Use this when the code needs to stop immediately with a clear error,
     * for example when a required value is missing.
     */
    public static void logAndThrow(String source, String message) {
        StepExecutionException error = new StepExecutionException(message);
        captureFault(error, source);
        throw error;
    }

    /**
     * Reports a failure built from a plain message, without throwing
     * anything. Expects the source it came from and the message to log.
     * Use this when something has gone wrong but the test should still be
     * allowed to continue running.
     */
    public static void log(String source, String message) {
        captureFault(new StepExecutionException(message), source);
    }

    /**
     * Forgets every failure that has been reported so far, so a repeated
     * failure will be logged again instead of being skipped as a
     * duplicate. Useful between test runs or test cases.
     */
    public static void clearFaultCache() {
        FaultCache.clearAll();
    }

    /**
     * Handles the rare case where logging the fault itself throws an
     * exception. Expects the exception that was thrown while logging and
     * the original source of the failure. It first tries to log a short
     * fallback message through LoggerUtil, and only prints to the console
     * as a very last resort if LoggerUtil also fails.
     */
    private static void handleLoggingFailure(Exception loggingFailure, String source) {
        String fallback = "FaultReporter failure while logging fault from source '" + source + "': "
                + FaultAnalyzer.getMessage(loggingFailure);
        try {
            LoggerUtil.error(fallback);
        } catch (Exception ignored) {
            System.err.println(fallback);
        }
    }
}
