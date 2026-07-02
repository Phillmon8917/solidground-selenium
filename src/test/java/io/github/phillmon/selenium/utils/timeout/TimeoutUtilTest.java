package io.github.phillmon.selenium.utils.timeout;

import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Unit tests for TimeoutUtil's pure arithmetic. IS_CI is computed once
 * from the CI environment variable when the class loads, so these tests
 * work out the expected multiplier from that same environment variable
 * at run time instead of assuming a fixed CI/non-CI environment.
 */
public class TimeoutUtilTest {
    private static long defaultCiFactor() {
        String ci = System.getenv("CI");
        return (ci != null && !ci.isEmpty()) ? 2 : 1;
    }

    private static long factorFor(CiMultiplier multiplier) {
        String ci = System.getenv("CI");
        return (ci != null && !ci.isEmpty()) ? multiplier.getFactor() : 1;
    }

    @Test
    public void ofMillisAppliesDefaultDoubleMultiplierOnlyWhenRunningOnCi() {
        assertEquals(TimeoutUtil.ofMillis(1000), 1000 * defaultCiFactor());
    }

    @Test
    public void ofMillisRespectsExplicitMultiplier() {
        assertEquals(TimeoutUtil.ofMillis(1000, CiMultiplier.TRIPLE), 1000 * factorFor(CiMultiplier.TRIPLE));
    }

    @Test
    public void ofSecondsConvertsToMillisBeforeMultiplying() {
        assertEquals(TimeoutUtil.ofSeconds(5), 5000 * defaultCiFactor());
    }

    @Test
    public void ofMinutesConvertsToMillisBeforeMultiplying() {
        assertEquals(TimeoutUtil.ofMinutes(2), 2L * 60 * 1000 * defaultCiFactor());
    }

    @Test
    public void adjustReturnsNullForNullDuration() {
        assertNull(TimeoutUtil.adjust(null));
    }

    @Test
    public void adjustMultipliesDurationOnlyWhenRunningOnCi() {
        Duration result = TimeoutUtil.adjust(Duration.ofSeconds(10));
        assertEquals(result, Duration.ofSeconds(10 * defaultCiFactor()));
    }

    @Test
    public void singleMultiplierNeverChangesTheBaseTimeout() {
        assertEquals(TimeoutUtil.ofMillis(1234, CiMultiplier.SINGLE), 1234);
    }
}
