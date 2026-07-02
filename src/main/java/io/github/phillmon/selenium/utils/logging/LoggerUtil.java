package io.github.phillmon.selenium.utils.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The single place every action class in the framework sends its log
 * messages through. Every message is written to the slf4j logger, to a
 * fresh execution log file for the current thread, and to whichever
 * TestReporter instances are currently registered on the current thread
 * (Allure by default), so the same log line ends up in the console, on
 * disk, and in the test report all at once.
 *
 * Reporters and the execution log file are both scoped per thread rather
 * than shared globally, so calling setReporter or logging from one test
 * thread never affects another test thread running in parallel.
 */
public final class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Path LOG_DIRECTORY = Paths.get("target", "logs");

    private static final ThreadLocal<List<TestReporter>> reporters =
            ThreadLocal.withInitial(() -> new CopyOnWriteArrayList<>(List.of(new AllureReporter())));

    private static final ThreadLocal<Path> logFile = ThreadLocal.withInitial(LoggerUtil::createLogFileForCurrentThread);

    private LoggerUtil() {
    }

    /**
     * Replaces every reporter currently registered on this thread with a
     * single one, for example switching from the default Allure reporter
     * to a TestNG reporter, or to a custom reporter supplied by the
     * caller. Only affects the calling thread. Expects the reporter to
     * use from now on, or null to stop attaching to any test report on
     * this thread (the file log keeps working regardless).
     *
     * @param reporter the reporter to use from now on, or null to stop
     *                 attaching to any test report
     */
    public static void setReporter(TestReporter reporter) {
        List<TestReporter> current = reporters.get();
        current.clear();
        if (reporter != null) {
            current.add(reporter);
        }
    }

    /**
     * Adds an extra reporter alongside whatever is already registered on
     * this thread, for example attaching to both Allure and TestNG in the
     * same run. Only affects the calling thread. Expects the reporter to
     * add; passing null does nothing.
     *
     * @param reporter the reporter to add; passing null does nothing
     */
    public static void addReporter(TestReporter reporter) {
        if (reporter != null) {
            reporters.get().add(reporter);
        }
    }

    /**
     * Logs an informational message. Expects the message text. Writes it
     * to the slf4j logger, appends it to this thread's execution log
     * file, and sends it to every reporter registered on this thread.
     *
     * @param message the message text to log
     */
    public static void info(String message) {
        logger.info(message);
        writeToFile("INFO", message);
        for (TestReporter reporter : reporters.get()) {
            reporter.info(message);
        }
    }

    /**
     * Logs a warning message. Expects the message text. Writes it to the
     * slf4j logger, appends it to this thread's execution log file, and
     * sends it to every reporter registered on this thread.
     *
     * @param message the message text to log
     */
    public static void warning(String message) {
        logger.warn(message);
        writeToFile("WARNING", message);
        for (TestReporter reporter : reporters.get()) {
            reporter.warning(message);
        }
    }

    /**
     * Logs an error message with no associated exception. Expects the
     * message text. Writes it to the slf4j logger, appends it to this
     * thread's execution log file, and sends it to every reporter
     * registered on this thread.
     *
     * @param message the message text to log
     */
    public static void error(String message) {
        logger.error(message);
        writeToFile("ERROR", message);
        for (TestReporter reporter : reporters.get()) {
            reporter.error(message);
        }
    }

    /**
     * Logs an error message together with the exception that caused it.
     * Expects the message text and the exception. Writes both to the
     * slf4j logger, appends them to this thread's execution log file, and
     * sends them to every reporter registered on this thread.
     *
     * @param message the message text to log
     * @param e       the exception that caused the error
     */
    public static void error(String message, Throwable e) {
        logger.error(message, e);
        writeToFile("ERROR", message + "\n\n" + e.getMessage());
        for (TestReporter reporter : reporters.get()) {
            reporter.error(message, e);
        }
    }

    /**
     * Works out and empties the execution log file for the current
     * thread, creating the shared log directory first if needed, so each
     * thread starts its own log with a clean file the first time it logs
     * anything. If the file cannot be reset, this only logs the problem
     * through slf4j rather than stopping the whole framework from
     * starting up.
     */
    private static Path createLogFileForCurrentThread() {
        Thread current = Thread.currentThread();
        String safeName = current.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path file = LOG_DIRECTORY.resolve("execution-" + safeName + "-" + current.getId() + ".log");
        try {
            Files.createDirectories(LOG_DIRECTORY);
            Files.write(file, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Unable to reset log file at " + file.toAbsolutePath(), e);
        }
        return file;
    }

    /**
     * Appends one formatted line to this thread's execution log file,
     * made up of a timestamp, the level, and the message. Expects the
     * level label (such as INFO, WARNING, or ERROR) and the message text.
     * Since the file is only ever touched by the thread it belongs to, no
     * locking is needed to keep messages from different threads from
     * interleaving or being lost. If writing fails, this only logs the
     * problem through slf4j rather than throwing.
     */
    private static void writeToFile(String level, String message) {
        String line = "[" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "] [" + level + "] " + message + System.lineSeparator();
        Path file = logFile.get();
        try {
            Files.write(file, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Failed to write to log file " + file.toAbsolutePath(), e);
        }
    }
}
