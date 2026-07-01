package io.github.phillmon.selenium.utils.logging;

import org.testng.Reporter;

/**
 * Sends log messages to TestNG's own reporter, so they show up in
 * TestNG's HTML report output. Use this with LoggerUtil.setReporter as an
 * alternative to the default Allure reporter, or alongside it with
 * addReporter.
 */
public class TestNgReporter implements TestReporter {
    /**
     * Creates a reporter that logs messages to TestNG's own reporter.
     */
    public TestNgReporter() {
    }

    /**
     * Logs the message to TestNG's reporter with an [INFO] prefix.
     *
     * @param message the message to log
     */
    @Override
    public void info(String message) {
        Reporter.log("[INFO] " + message, true);
    }

    /**
     * Logs the message to TestNG's reporter with a [WARNING] prefix.
     *
     * @param message the message to log
     */
    @Override
    public void warning(String message) {
        Reporter.log("[WARNING] " + message, true);
    }

    /**
     * Logs the message to TestNG's reporter with an [ERROR] prefix.
     *
     * @param message the message to log
     */
    @Override
    public void error(String message) {
        Reporter.log("[ERROR] " + message, true);
    }

    /**
     * Logs the message and the exception's own message to TestNG's
     * reporter with an [ERROR] prefix.
     *
     * @param message the message to log
     * @param throwable the exception whose message should be appended to the log entry
     */
    @Override
    public void error(String message, Throwable throwable) {
        Reporter.log("[ERROR] " + message + "\n\n" + throwable.getMessage(), true);
    }
}
