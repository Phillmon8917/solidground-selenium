package io.github.phillmon.selenium.utils.logging;

import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Drives LoggerUtil from many threads at once, the way a parallel TestNG
 * run would, and confirms that setReporter and the execution log file
 * stay isolated per thread instead of leaking between threads.
 */
public class LoggerUtilParallelTest {
    private static final int THREAD_COUNT = 12;
    private static final int MESSAGES_PER_THREAD = 100;

    @Test
    public void reportersAndLogFilesStayIsolatedPerThread() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            List<Callable<ThreadResult>> tasks = new ArrayList<>();
            for (int i = 0; i < THREAD_COUNT; i++) {
                int threadIndex = i;
                tasks.add(() -> runOnWorkerThread(threadIndex));
            }

            List<Future<ThreadResult>> futures = pool.invokeAll(tasks);

            for (Future<ThreadResult> future : futures) {
                ThreadResult result = future.get(30, TimeUnit.SECONDS);
                verifyIsolation(result);
            }
        } finally {
            pool.shutdown();
        }
    }

    private ThreadResult runOnWorkerThread(int threadIndex) throws Exception {
        String marker = "thread-" + threadIndex;
        RecordingReporter reporter = new RecordingReporter();
        LoggerUtil.setReporter(reporter);

        for (int i = 0; i < MESSAGES_PER_THREAD; i++) {
            LoggerUtil.info(marker + " message " + i);
        }

        Thread current = Thread.currentThread();
        Path logFile = expectedLogFile(current);
        return new ThreadResult(marker, reporter.messages, logFile);
    }

    private void verifyIsolation(ThreadResult result) throws Exception {
        assertEquals(result.reporterMessages.size(), MESSAGES_PER_THREAD,
                "reporter for " + result.marker + " should have received exactly its own messages");
        for (String message : result.reporterMessages) {
            assertTrue(message.startsWith(result.marker + " "),
                    "reporter for " + result.marker + " received a foreign message: " + message);
        }

        assertTrue(Files.exists(result.logFile), "expected a log file at " + result.logFile);
        List<String> lines = Files.readAllLines(result.logFile);
        assertEquals(lines.size(), MESSAGES_PER_THREAD,
                "log file for " + result.marker + " should contain exactly its own lines");
        for (String line : lines) {
            assertTrue(line.contains(result.marker + " "),
                    "log file for " + result.marker + " contains a foreign line: " + line);
        }
    }

    /**
     * Mirrors LoggerUtil's own file naming scheme so the test can check
     * the right file without LoggerUtil needing to expose it.
     */
    private Path expectedLogFile(Thread thread) {
        String safeName = thread.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        return Paths.get("target", "logs", "execution-" + safeName + "-" + thread.getId() + ".log");
    }

    private static final class ThreadResult {
        final String marker;
        final List<String> reporterMessages;
        final Path logFile;

        ThreadResult(String marker, List<String> reporterMessages, Path logFile) {
            this.marker = marker;
            this.reporterMessages = reporterMessages;
            this.logFile = logFile;
        }
    }
}
