package io.github.phillmon.selenium.utils.timeout;

/**
 * How much a base timeout should be multiplied by when the tests are
 * running on CI, since CI environments are often slower or more loaded
 * than a local machine and need extra time before something is
 * considered timed out.
 */
public enum CiMultiplier {
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3);

    private final int factor;

    CiMultiplier(int factor) {
        this.factor = factor;
    }

    /**
     * Returns the number a base timeout should be multiplied by.
     */
    public int getFactor() {
        return factor;
    }
}
