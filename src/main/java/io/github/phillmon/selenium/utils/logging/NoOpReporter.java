package io.github.phillmon.selenium.utils.logging;

/**
 * A reporter that does nothing with any message it receives. Use this
 * with LoggerUtil.setReporter when only the file log is wanted, with no
 * messages attached to any test report.
 */
public class NoOpReporter implements TestReporter {
    /**
     * Creates a reporter that ignores every message it receives.
     */
    public NoOpReporter() {
    }

    /**
     * Does nothing with the message.
     *
     * @param message the informational message that is ignored
     */
    @Override
    public void info(String message) {
    }

    /**
     * Does nothing with the message.
     *
     * @param message the warning message that is ignored
     */
    @Override
    public void warning(String message) {
    }

    /**
     * Does nothing with the message.
     *
     * @param message the error message that is ignored
     */
    @Override
    public void error(String message) {
    }

    /**
     * Does nothing with the message or the exception.
     *
     * @param message the error message that is ignored
     * @param throwable the exception that is ignored
     */
    @Override
    public void error(String message, Throwable throwable) {
    }
}
