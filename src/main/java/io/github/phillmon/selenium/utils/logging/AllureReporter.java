package io.github.phillmon.selenium.utils.logging;

import io.qameta.allure.Allure;

/**
 * Sends log messages to the Allure test report as attachments, so
 * information messages, warnings, and errors logged during a test show
 * up alongside the test's result in the Allure report. This is the
 * default reporter LoggerUtil uses out of the box.
 */
public class AllureReporter implements TestReporter {
    /**
     * Attaches the message to the Allure report under an INFO label.
     */
    @Override
    public void info(String message) {
        Allure.addAttachment("INFO", message);
    }

    /**
     * Attaches the message to the Allure report under a WARNING label.
     */
    @Override
    public void warning(String message) {
        Allure.addAttachment("WARNING", message);
    }

    /**
     * Attaches the message to the Allure report under an ERROR label.
     */
    @Override
    public void error(String message) {
        Allure.addAttachment("ERROR", message);
    }

    /**
     * Attaches the message and the exception's own message to the Allure
     * report under an ERROR label.
     */
    @Override
    public void error(String message, Throwable throwable) {
        Allure.addAttachment("ERROR", message + "\n\n" + throwable.getMessage());
    }
}
