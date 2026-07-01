package io.github.phillmon.selenium.modulars.network;

import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;

import java.time.Duration;

/**
 * Holds the default timing values used by the network validation classes
 * when a caller does not supply its own: how long to wait for a response
 * overall, how long to wait before retrying a fetch that has not shown up
 * yet, and how long to wait after refreshing the page during that retry.
 * Every value already has TimeoutUtil's CI adjustment applied.
 */
public final class NetworkDefaults {
    public static final Duration DEFAULT_TIMEOUT = TimeoutUtil.adjust(Duration.ofSeconds(30));
    public static final Duration FETCH_RETRY_DELAY = TimeoutUtil.adjust(Duration.ofSeconds(3));
    public static final Duration RELOAD_TIMEOUT = TimeoutUtil.adjust(Duration.ofSeconds(30));

    private NetworkDefaults() {
    }
}
