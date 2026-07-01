package io.github.phillmon.selenium.utils.timeout;

/**
 * How much a base timeout should be multiplied by when the tests are
 * running on CI, since CI environments are often slower or more loaded
 * than a local machine and need extra time before something is
 * considered timed out.
 */
public enum CiMultiplier {
    /** Leaves the base timeout unchanged. */
    SINGLE(1),
    /** Doubles the base timeout. */
    DOUBLE(2),
    /** Triples the base timeout. */
    TRIPLE(3);

    private final int factor;

    CiMultiplier(int factor) {
        this.factor = factor;
    }

    /**
     * Returns the number a base timeout should be multiplied by.
     *
     * @return the multiplier to apply to a base timeout
     */
    public int getFactor() {
        return factor;
    }
}
