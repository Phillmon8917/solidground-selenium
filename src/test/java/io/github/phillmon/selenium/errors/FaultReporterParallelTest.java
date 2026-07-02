package io.github.phillmon.selenium.errors;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.logging.TestReporter;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * Drives FaultReporter from many threads at once, each throwing its own
 * distinct exceptions and each re-reporting one of them a second time (the
 * nested SafeStep.run scenario FaultCache exists for). Confirms every
 * distinct exception across every thread is reported exactly once, with
 * no genuine failure silently dropped and no duplicate reported twice,
 * even under concurrent access to the shared dedup cache.
 */
public class FaultReporterParallelTest {
    private static final int THREAD_COUNT = 12;
    private static final int EXCEPTIONS_PER_THREAD = 50;

    @Test
    public void everyDistinctExceptionIsReportedExactlyOnceAcrossThreads() throws Exception {
        FaultReporter.clearFaultCache();
        ConcurrentLinkedQueue<String> reportedMessages = new ConcurrentLinkedQueue<>();

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < THREAD_COUNT; i++) {
                int threadIndex = i;
                tasks.add(() -> {
                    reportOnWorkerThread(threadIndex, reportedMessages);
                    return null;
                });
            }

            List<Future<Void>> futures = pool.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdown();
        }

        assertEquals(reportedMessages.size(), THREAD_COUNT * EXCEPTIONS_PER_THREAD,
                "every distinct exception across every thread should be reported exactly once, "
                        + "with none dropped due to a colliding identity and none double-reported");
    }

    private void reportOnWorkerThread(int threadIndex, ConcurrentLinkedQueue<String> reportedMessages) {
        LoggerUtil.setReporter(new CountingReporter(reportedMessages));

        for (int i = 0; i < EXCEPTIONS_PER_THREAD; i++) {
            RuntimeException error = new RuntimeException("thread-" + threadIndex + " failure " + i);
            FaultReporter.captureFault(error, "FaultReporterParallelTest");
            // Re-report the same instance, simulating an outer SafeStep.run catching
            // the same exception a nested inner SafeStep.run already reported.
            FaultReporter.captureFault(error, "FaultReporterParallelTest");
        }
    }

    private static final class CountingReporter implements TestReporter {
        private final ConcurrentLinkedQueue<String> sink;

        CountingReporter(ConcurrentLinkedQueue<String> sink) {
            this.sink = sink;
        }

        @Override
        public void info(String message) {
        }

        @Override
        public void warning(String message) {
        }

        @Override
        public void error(String message) {
            sink.add(message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            sink.add(message);
        }
    }
}
