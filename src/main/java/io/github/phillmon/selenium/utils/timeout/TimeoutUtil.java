package io.github.phillmon.selenium.utils.timeout;

import java.time.Duration;

/**
 * Adjusts timeouts so tests wait longer when running on CI. Whether the
 * tests are running on CI is decided once, by checking whether a CI
 * environment variable is set and non-empty. Every wait timeout used
 * throughout the framework should be passed through one of these methods
 * so CI runs get the extra buffer they typically need.
 */
public final class TimeoutUtil {
    private static final boolean IS_CI =
            System.getenv("CI") != null &&
                    !System.getenv("CI").isEmpty();

    private TimeoutUtil() {
    }

    /**
     * Returns a timeout in milliseconds, doubled if running on CI.
     * Expects the base timeout in milliseconds to start from.
     *
     * @param milliseconds the base timeout, in milliseconds, to start from
     * @return the timeout in milliseconds, doubled when running on CI
     */
    public static long ofMillis(long milliseconds) {
        return ofMillis(milliseconds, CiMultiplier.DOUBLE);
    }

    /**
     * Returns a timeout in milliseconds, multiplied by the given factor
     * if running on CI. Expects the base timeout in milliseconds and the
     * multiplier to apply on CI.
     *
     * @param milliseconds the base timeout, in milliseconds, to start from
     * @param ciMultiplier the multiplier to apply when running on CI
     * @return the timeout in milliseconds, multiplied by the given factor when running on CI
     */
    public static long ofMillis(long milliseconds, CiMultiplier ciMultiplier) {
        return IS_CI ? milliseconds * ciMultiplier.getFactor() : milliseconds;
    }

    /**
     * Returns a timeout in milliseconds converted from seconds, doubled
     * if running on CI. Expects the base timeout in seconds to start
     * from.
     *
     * @param seconds the base timeout, in seconds, to start from
     * @return the timeout in milliseconds, doubled when running on CI
     */
    public static long ofSeconds(long seconds) {
        return ofSeconds(seconds, CiMultiplier.DOUBLE);
    }

    /**
     * Returns a timeout in milliseconds converted from seconds,
     * multiplied by the given factor if running on CI. Expects the base
     * timeout in seconds and the multiplier to apply on CI.
     *
     * @param seconds the base timeout, in seconds, to start from
     * @param ciMultiplier the multiplier to apply when running on CI
     * @return the timeout in milliseconds, multiplied by the given factor when running on CI
     */
    public static long ofSeconds(long seconds, CiMultiplier ciMultiplier) {
        long milliseconds = seconds * 1000;
        return ofMillis(milliseconds, ciMultiplier);
    }

    /**
     * Returns a timeout in milliseconds converted from minutes, doubled
     * if running on CI. Expects the base timeout in minutes to start
     * from.
     *
     * @param minutes the base timeout, in minutes, to start from
     * @return the timeout in milliseconds, doubled when running on CI
     */
    public static long ofMinutes(long minutes) {
        return ofMinutes(minutes, CiMultiplier.DOUBLE);
    }

    /**
     * Returns a timeout in milliseconds converted from minutes,
     * multiplied by the given factor if running on CI. Expects the base
     * timeout in minutes and the multiplier to apply on CI.
     *
     * @param minutes the base timeout, in minutes, to start from
     * @param ciMultiplier the multiplier to apply when running on CI
     * @return the timeout in milliseconds, multiplied by the given factor when running on CI
     */
    public static long ofMinutes(long minutes, CiMultiplier ciMultiplier) {
        long milliseconds = minutes * 60 * 1000;
        return ofMillis(milliseconds, ciMultiplier);
    }

    /**
     * Returns a Duration, doubled if running on CI. Expects the base
     * duration to start from, which can be null. Returns null if the
     * given duration was null.
     *
     * @param duration the base duration to start from, which may be null
     * @return the duration doubled when running on CI, the original duration otherwise, or null if the given duration was null
     */
    public static Duration adjust(Duration duration) {
        return adjust(duration, CiMultiplier.DOUBLE);
    }

    /**
     * Returns a Duration, multiplied by the given factor if running on
     * CI. Expects the base duration to start from, which can be null,
     * and the multiplier to apply on CI. Returns null if the given
     * duration was null.
     *
     * @param duration the base duration to start from, which may be null
     * @param ciMultiplier the multiplier to apply when running on CI
     * @return the duration multiplied by the given factor when running on CI, the original duration otherwise, or null if the given duration was null
     */
    public static Duration adjust(Duration duration, CiMultiplier ciMultiplier) {
        if (duration == null) {
            return null;
        }
        return IS_CI ? duration.multipliedBy(ciMultiplier.getFactor()) : duration;
    }
}
