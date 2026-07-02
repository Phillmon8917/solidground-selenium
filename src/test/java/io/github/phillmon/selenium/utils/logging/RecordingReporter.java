package io.github.phillmon.selenium.utils.logging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A TestReporter that simply keeps every message it receives, so a test
 * can assert on exactly what was reported to it without touching Allure.
 */
class RecordingReporter implements TestReporter {
    final List<String> messages = new CopyOnWriteArrayList<>();

    @Override
    public void info(String message) {
        messages.add(message);
    }

    @Override
    public void warning(String message) {
        messages.add(message);
    }

    @Override
    public void error(String message) {
        messages.add(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        messages.add(message);
    }
}
