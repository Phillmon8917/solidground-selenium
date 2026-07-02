package io.github.phillmon.selenium.base;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * Drives PageModularOptions.defaults() from many threads at once, the way
 * a parallel TestNG run would create page objects on different threads,
 * and confirms the default SoftAssert is shared within a thread (so
 * assertAll from any page sees every page's soft failures) but never
 * leaks across threads.
 */
public class PageModularOptionsParallelTest {
    private static final int THREAD_COUNT = 12;
    private static final int OPTIONS_PER_THREAD = 25;

    @Test
    public void defaultSoftAssertIsSharedWithinThreadButIsolatedAcrossThreads() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            List<Callable<List<SoftAssert>>> tasks = new ArrayList<>();
            for (int i = 0; i < THREAD_COUNT; i++) {
                tasks.add(this::softAssertsSeenOnThisThread);
            }

            List<Future<List<SoftAssert>>> futures = pool.invokeAll(tasks);

            List<SoftAssert> oneInstancePerThread = new ArrayList<>();
            for (Future<List<SoftAssert>> future : futures) {
                List<SoftAssert> instances = future.get(30, TimeUnit.SECONDS);

                for (int i = 1; i < instances.size(); i++) {
                    assertSame(instances.get(i), instances.get(0),
                            "every PageModularOptions.defaults() built on the same thread should "
                                    + "share one SoftAssert");
                }
                oneInstancePerThread.add(instances.get(0));
            }

            for (int i = 0; i < oneInstancePerThread.size(); i++) {
                for (int j = i + 1; j < oneInstancePerThread.size(); j++) {
                    assertNotSame(oneInstancePerThread.get(i), oneInstancePerThread.get(j),
                            "the default SoftAssert must not leak across threads");
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    private List<SoftAssert> softAssertsSeenOnThisThread() {
        PageModularOptions.resetCurrentThreadSoftAssert();

        List<SoftAssert> seen = new ArrayList<>();
        for (int i = 0; i < OPTIONS_PER_THREAD; i++) {
            seen.add(PageModularOptions.defaults().createSoftAssert());
        }
        return seen;
    }
}
