package io.github.phillmon.selenium.utils.logging;

/**
 * A reporter that does nothing with any message it receives. Use this
 * with LoggerUtil.setReporter when only the file log is wanted, with no
 * messages attached to any test report.
 */
public class NoOpReporter implements TestReporter {
    /**
     * Does nothing with the message.
     */
    @Override
    public void info(String message) {
    }

    /**
     * Does nothing with the message.
     */
    @Override
    public void warning(String message) {
    }

    /**
     * Does nothing with the message.
     */
    @Override
    public void error(String message) {
    }

    /**
     * Does nothing with the message or the exception.
     */
    @Override
    public void error(String message, Throwable throwable) {
    }
}
