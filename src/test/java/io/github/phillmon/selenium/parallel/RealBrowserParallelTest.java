package io.github.phillmon.selenium.parallel;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * Drives several real, independent Chrome sessions at once, each one
 * running its own page object built on BasePage, to confirm the library
 * behaves correctly under genuine parallel browser execution and not
 * just concurrent access to shared library state. Each thread gets its
 * own WebDriver, ActionsContainer, and page object instance, so this is
 * mainly a check that nothing in the library accidentally reaches for
 * global/static browser state and that many Chrome sessions can be
 * driven side by side without interfering with each other.
 */
public class RealBrowserParallelTest {
    private static final int BROWSER_COUNT = 4;

    @Test
    public void independentPagesAcrossParallelBrowsersDoNotInterfere() throws Exception {
        String fixtureUrl = fixtureUrl();

        ExecutorService pool = Executors.newFixedThreadPool(BROWSER_COUNT);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < BROWSER_COUNT; i++) {
                int browserIndex = i;
                tasks.add(() -> {
                    runInOwnBrowser(browserIndex, fixtureUrl);
                    return null;
                });
            }

            List<Future<Void>> futures = pool.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get(120, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdown();
        }
    }

    private void runInOwnBrowser(int browserIndex, String fixtureUrl) {
        WebDriver driver = new ChromeDriver(headlessOptions());
        try {
            driver.get(fixtureUrl);

            String inputValue = "browser-" + browserIndex;
            String selectValue = selectValueFor(browserIndex);

            try (FixturePage page = new FixturePage(driver)) {
                page.fillOutForm(inputValue, selectValue);
                String output = page.readOutput();
                assertEquals(output, inputValue + "|" + selectValue + "|true",
                        "browser " + browserIndex + " should only see its own form state");
            }
        } finally {
            driver.quit();
        }
    }

    private String selectValueFor(int browserIndex) {
        String[] options = {"one", "two", "three"};
        return options[browserIndex % options.length];
    }

    private ChromeOptions headlessOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        return options;
    }

    private String fixtureUrl() {
        URL resource = getClass().getClassLoader().getResource("fixtures/parallel-test-page.html");
        if (resource == null) {
            throw new IllegalStateException("Could not find test fixture fixtures/parallel-test-page.html on the classpath");
        }
        return resource.toExternalForm();
    }
}
