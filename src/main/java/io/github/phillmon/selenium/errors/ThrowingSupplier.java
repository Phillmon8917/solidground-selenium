package io.github.phillmon.selenium.errors;

/**
 * Describes a step that returns a value and is allowed to throw any
 * exception. This is the same idea as ThrowingRunnable, but for steps
 * that need to hand a result back to the caller.
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    /**
     * Runs the step and returns its result. Any exception thrown here is
     * caught and handled by whichever SafeStep method is running this
     * step.
     */
    T get() throws Exception;
}
