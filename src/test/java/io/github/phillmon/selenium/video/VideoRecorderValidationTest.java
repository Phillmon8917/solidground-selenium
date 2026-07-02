package io.github.phillmon.selenium.video;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.lang.reflect.Proxy;

import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Validates VideoRecorder's constructor guards without needing a real
 * browser, since these checks happen before any WebDriver call is made.
 */
public class VideoRecorderValidationTest {
    @Test
    public void rejectsANonChromeDriverWithAClearMessage() {
        WebDriver notChrome = notImplementedWebDriver();

        try {
            new VideoRecorder(notChrome);
            fail("expected a VideoRecordingException for a non-Chrome driver");
        } catch (VideoRecordingException expected) {
            assertTrue(expected.getMessage().contains("ChromeDriver"),
                    "the error should explain that video recording needs a ChromeDriver");
        }
    }

    @Test
    public void rejectsNullOptions() {
        WebDriver notChrome = notImplementedWebDriver();
        assertThrows(IllegalArgumentException.class, () -> new VideoRecorder(notChrome, null));
    }

    /**
     * Builds a WebDriver that throws if any of its methods are actually
     * called, since VideoRecorder's constructor only needs to check the
     * driver's type and should never invoke a WebDriver method before
     * that check runs.
     */
    private WebDriver notImplementedWebDriver() {
        return (WebDriver) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{WebDriver.class},
                (proxy, method, args) -> {
                    throw new UnsupportedOperationException("not implemented in this test double");
                });
    }
}
