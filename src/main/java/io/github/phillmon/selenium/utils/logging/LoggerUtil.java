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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The single place every action class in the framework sends its log
 * messages through. Every message is written to the slf4j logger, to a
 * fresh execution log file for the current run, and to whichever
 * TestReporter instances are currently registered (Allure by default), so
 * the same log line ends up in the console, on disk, and in the test
 * report all at once.
 */
public final class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Path LOG_FILE = Paths.get("src", "logs", "execution.log");
    private static final Object FILE_LOCK = new Object();

    private static final CopyOnWriteArrayList<TestReporter> reporters =
            new CopyOnWriteArrayList<>(java.util.List.of(new AllureReporter()));

    static {
        resetLogFile();
    }

    private LoggerUtil() {
    }

    /**
     * Replaces every currently registered reporter with a single one, for
     * example switching from the default Allure reporter to a TestNG
     * reporter, or to a custom reporter supplied by the caller. Expects
     * the reporter to use from now on, or null to stop attaching to any
     * test report (the file log keeps working regardless).
     */
    public static void setReporter(TestReporter reporter) {
        reporters.clear();
        if (reporter != null) {
            reporters.add(reporter);
        }
    }

    /**
     * Adds an extra reporter alongside whatever is already registered,
     * for example attaching to both Allure and TestNG in the same run.
     * Expects the reporter to add; passing null does nothing.
     */
    public static void addReporter(TestReporter reporter) {
        if (reporter != null) {
            reporters.add(reporter);
        }
    }

    /**
     * Logs an informational message. Expects the message text. Writes it
     * to the slf4j logger, appends it to the execution log file, and
     * sends it to every registered reporter.
     */
    public static void info(String message) {
        logger.info(message);
        writeToFile("INFO", message);
        for (TestReporter reporter : reporters) {
            reporter.info(message);
        }
    }

    /**
     * Logs a warning message. Expects the message text. Writes it to the
     * slf4j logger, appends it to the execution log file, and sends it to
     * every registered reporter.
     */
    public static void warning(String message) {
        logger.warn(message);
        writeToFile("WARNING", message);
        for (TestReporter reporter : reporters) {
            reporter.warning(message);
        }
    }

    /**
     * Logs an error message with no associated exception. Expects the
     * message text. Writes it to the slf4j logger, appends it to the
     * execution log file, and sends it to every registered reporter.
     */
    public static void error(String message) {
        logger.error(message);
        writeToFile("ERROR", message);
        for (TestReporter reporter : reporters) {
            reporter.error(message);
        }
    }

    /**
     * Logs an error message together with the exception that caused it.
     * Expects the message text and the exception. Writes both to the
     * slf4j logger, appends them to the execution log file, and sends
     * them to every registered reporter.
     */
    public static void error(String message, Throwable e) {
        logger.error(message, e);
        writeToFile("ERROR", message + "\n\n" + e.getMessage());
        for (TestReporter reporter : reporters) {
            reporter.error(message, e);
        }
    }

    /**
     * Empties the execution log file at the start of the run, creating
     * its parent folder first if needed, so each run starts with a clean
     * log. If the file cannot be reset, this only logs the problem
     * through slf4j rather than stopping the whole framework from
     * starting up.
     */
    private static void resetLogFile() {
        try {
            Path parent = LOG_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(LOG_FILE, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Unable to reset log file at " + LOG_FILE.toAbsolutePath(), e);
        }
    }

    /**
     * Appends one formatted line to the execution log file, made up of a
     * timestamp, the level, and the message. Expects the level label
     * (such as INFO, WARNING, or ERROR) and the message text. Writes to
     * the file under a lock so messages logged from different threads do
     * not get interleaved or lost. If writing fails, this only logs the
     * problem through slf4j rather than throwing.
     */
    private static void writeToFile(String level, String message) {
        String line = "[" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "] [" + level + "] " + message + System.lineSeparator();
        synchronized (FILE_LOCK) {
            try {
                Files.write(LOG_FILE, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error("Failed to write to log file " + LOG_FILE.toAbsolutePath(), e);
            }
        }
    }
}
