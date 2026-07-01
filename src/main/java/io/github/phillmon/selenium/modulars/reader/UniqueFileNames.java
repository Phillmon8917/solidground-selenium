package io.github.phillmon.selenium.modulars.reader;

import java.util.UUID;

/**
 * Small helper for building file names that are guaranteed not to clash
 * with each other, even when several downloads happen at the same time
 * from different threads.
 */
public final class UniqueFileNames {
    private UniqueFileNames() {
    }

    /**
     * Builds a unique file name combining the given prefix with the
     * current time, the current thread's id, a random unique id, and the
     * given extension. Expects a prefix describing what the file is for
     * and the file extension to use, without a leading dot. Returns the
     * finished file name, for example {@code "document-1719830000-1-<uuid>.pdf"}.
     *
     * @param prefix    a short label describing what the file is for
     * @param extension the file extension to use, without a leading dot
     * @return the finished, unique file name
     */
    @SuppressWarnings("deprecation") // Thread.threadId() needs Java 19+; this library targets 17
    public static String generate(String prefix, String extension) {
        return prefix + "-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId()
                + "-" + UUID.randomUUID() + "." + extension;
    }
}
