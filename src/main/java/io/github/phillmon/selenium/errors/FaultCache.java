package io.github.phillmon.selenium.errors;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Keeps track of which exception instances have already been reported, so
 * the same failure does not get logged more than once when it passes
 * through several nested SafeStep.run calls. The cache only lives in
 * memory for as long as the test process runs.
 */
final class FaultCache {
    private static final Set<Throwable> reported =
            Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));

    private FaultCache() {
    }

    /**
     * Checks whether the given error should be reported right now. It
     * expects an error, which can be null. When the error is null this
     * returns false. When the error has not been seen before, this
     * remembers it and returns true. When the same error instance has
     * already been seen, this returns false so it does not get reported
     * again. Tracks by true reference identity (via IdentityHashMap)
     * rather than System.identityHashCode alone, since identity hash
     * codes are not guaranteed unique and two unrelated exceptions
     * thrown on different threads during a parallel run could otherwise
     * collide, silently swallowing a genuine failure.
     */
    static boolean shouldReport(Throwable error) {
        return error != null && reported.add(error);
    }

    /**
     * Forgets every error that has been remembered so far. After this
     * runs, any error that was already reported will be treated as new
     * and can be reported again.
     */
    static void clearAll() {
        reported.clear();
    }
}
