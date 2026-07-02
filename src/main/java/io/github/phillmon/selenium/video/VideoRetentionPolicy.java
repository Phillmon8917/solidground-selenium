package io.github.phillmon.selenium.video;

/**
 * Controls which video recordings VideoRecorder keeps on disk once a test
 * finishes, versus which ones it discards.
 */
public enum VideoRetentionPolicy {
    /** Keeps every recording, whether the test passed or failed. */
    ALWAYS,
    /** Keeps a recording only when the test it was recorded for failed. */
    ON_FAILURE_ONLY
}
