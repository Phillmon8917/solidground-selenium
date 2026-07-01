package io.github.phillmon.selenium.errors;

/**
 * Unchecked exception thrown when a step run through SafeStep or
 * FaultReporter fails and the original error was not already a
 * RuntimeException. Wrapping it this way means callers never have to
 * declare or catch checked exceptions just to use SafeStep.
 */
public class StepExecutionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach, such as a manually logged failure.
     *
     * @param message description of what failed
     */
    public StepExecutionException(String message) {
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
    public StepExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
