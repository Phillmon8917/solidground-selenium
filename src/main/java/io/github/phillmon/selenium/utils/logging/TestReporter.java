package io.github.phillmon.selenium.utils.logging;

/**
 * Describes a destination that test-execution messages can be sent to,
 * such as Allure, TestNG's own reporter, or a customer's own reporting
 * tool. LoggerUtil sends every message it logs to whichever reporters
 * have been registered through setReporter or addReporter, in addition
 * to always writing to the file log.
 */
public interface TestReporter {
    /**
     * Records an informational message.
     *
     * @param message the message to record
     */
    void info(String message);

    /**
     * Records a warning message.
     *
     * @param message the message to record
     */
    void warning(String message);

    /**
     * Records an error message with no associated exception.
     *
     * @param message the message to record
     */
    void error(String message);

    /**
     * Records an error message together with the exception that caused
     * it.
     *
     * @param message   the message to record
     * @param throwable the exception that caused the error
     */
    void error(String message, Throwable throwable);
}
