package io.github.phillmon.selenium.errors;

/**
 * Describes a step that does not return a value but is allowed to throw
 * any exception. SafeStep uses this so it can wrap ordinary page-object
 * calls, which may throw checked or unchecked exceptions, without the
 * caller needing its own try/catch.
 */
@FunctionalInterface
public interface ThrowingRunnable {
    /**
     * Runs the step. Any exception thrown here is caught and handled by
     * whichever SafeStep method is running this step.
     */
    void run() throws Exception;
}
