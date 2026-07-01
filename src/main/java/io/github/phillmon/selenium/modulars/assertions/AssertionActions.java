package io.github.phillmon.selenium.modulars.assertions;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

/**
 * Wraps TestNG's Assert and SoftAssert calls with logging so every
 * assertion made in a test also shows up in the execution log. A hard
 * assert (the methods without "soft" in the name) stops the test straight
 * away when it fails. A soft assert records the failure but lets the test
 * keep running, and the failures only surface once assertAll is called.
 */
public class AssertionActions {
    private final SoftAssert softAssert;

    /**
     * Creates the assertion actions using the given SoftAssert instance,
     * so every soft assertion made through this class is collected on the
     * same SoftAssert and can be checked together later with assertAll.
     */
    public AssertionActions(SoftAssert softAssert) {
        this.softAssert = softAssert;
    }

    /**
     * Hard asserts that the actual value equals the expected value.
     * Expects the actual value, the expected value, and the name of the
     * calling method for logging. Stops the test immediately if the
     * values are not equal.
     */
    public <T> void assertEqualsTo(T actual, T expected, String methodName) {
        Assert.assertEquals(actual, expected, methodName + " - expected: " + expected + " but found: " + actual);
        LoggerUtil.info(methodName + " found the actual to equal the expected");
    }

    /**
     * Soft asserts that the actual value equals the expected value.
     * Expects the actual value, the expected value, and the name of the
     * calling method for logging. Records a failure without stopping the
     * test if the values are not equal.
     */
    public <T> void softAssertEqualsTo(T actual, T expected, String methodName) {
        softAssert.assertEquals(actual, expected, methodName + " - expected: " + expected + " but found: " + actual);
        LoggerUtil.info(methodName + " checked (soft) actual against expected");
    }

    /**
     * Hard asserts that the actual value does not equal the expected
     * value. Expects the actual value, the value it must not equal, and
     * the name of the calling method for logging. Stops the test
     * immediately if the values are equal.
     */
    public <T> void assertNotEqualsTo(T actual, T expected, String methodName) {
        Assert.assertNotEquals(actual, expected, methodName + " - did not expect: " + expected + " but found: " + actual);
        LoggerUtil.info(methodName + " found the actual to not equal the expected");
    }

    /**
     * Soft asserts that the actual value does not equal the expected
     * value. Expects the actual value, the value it must not equal, and
     * the name of the calling method for logging. Records a failure
     * without stopping the test if the values are equal.
     */
    public <T> void softAssertNotEqualsTo(T actual, T expected, String methodName) {
        softAssert.assertNotEquals(actual, expected, methodName + " - did not expect: " + expected + " but found: " + actual);
        LoggerUtil.info(methodName + " checked (soft) actual to not equal expected");
    }

    /**
     * Hard asserts that a boolean condition matches the expected state
     * (true or false). Expects the condition to check, the expected
     * state, and the name of the calling method for logging. Stops the
     * test immediately if the condition does not match the expected
     * state.
     */
    public void assertConditionState(boolean condition, ExpectedState expectedState, String methodName) {
        if (expectedState == ExpectedState.TRUE) {
            Assert.assertTrue(condition, methodName + " - expected condition to be true");
        } else {
            Assert.assertFalse(condition, methodName + " - expected condition to be false");
        }
        LoggerUtil.info(methodName + " found the condition to be " + expectedState.toString().toLowerCase());
    }

    /**
     * Soft asserts that a boolean condition matches the expected state
     * (true or false). Expects the condition to check, the expected
     * state, and the name of the calling method for logging. Records a
     * failure without stopping the test if the condition does not match.
     */
    public void softAssertConditionState(boolean condition, ExpectedState expectedState, String methodName) {
        if (expectedState == ExpectedState.TRUE) {
            softAssert.assertTrue(condition, methodName + " - expected condition to be true");
        } else {
            softAssert.assertFalse(condition, methodName + " - expected condition to be false");
        }
        LoggerUtil.info(methodName + " checked (soft) condition to be " + expectedState.toString().toLowerCase());
    }

    /**
     * Hard asserts whether the actual value is null or not null, based on
     * the expected state. Expects the actual value, the expected null
     * state, and the name of the calling method for logging. Stops the
     * test immediately if the actual value does not match the expected
     * state.
     */
    public void assertNullState(Object actual, NullState expectedState, String methodName) {
        if (expectedState == NullState.NULL) {
            Assert.assertNull(actual, methodName + " - expected null but found: " + actual);
        } else {
            Assert.assertNotNull(actual, methodName + " - expected non-null but found null");
        }
        LoggerUtil.info(methodName + " found the actual to be " + expectedState.toString().toLowerCase());
    }

    /**
     * Soft asserts whether the actual value is null or not null, based on
     * the expected state. Expects the actual value, the expected null
     * state, and the name of the calling method for logging. Records a
     * failure without stopping the test if the actual value does not
     * match.
     */
    public void softAssertNullState(Object actual, NullState expectedState, String methodName) {
        if (expectedState == NullState.NULL) {
            softAssert.assertNull(actual, methodName + " - expected null but found: " + actual);
        } else {
            softAssert.assertNotNull(actual, methodName + " - expected non-null but found null");
        }
        LoggerUtil.info(methodName + " checked (soft) actual to be " + expectedState.toString().toLowerCase());
    }

    /**
     * Hard asserts that the actual string contains the expected
     * substring. Expects the actual string, the substring it must
     * contain, and the name of the calling method for logging. Stops the
     * test immediately if the actual string is null or does not contain
     * the substring.
     */
    public void assertContains(String actual, String expectedSubstring, String methodName) {
        Assert.assertTrue(actual != null && actual.contains(expectedSubstring),
                methodName + " - expected actual to contain: " + expectedSubstring + " but found: " + actual);
        LoggerUtil.info(methodName + " found the actual to contain expected substring");
    }

    /**
     * Soft asserts that the actual string contains the expected
     * substring. Expects the actual string, the substring it must
     * contain, and the name of the calling method for logging. Records a
     * failure without stopping the test if the actual string is null or
     * does not contain the substring.
     */
    public void softAssertContains(String actual, String expectedSubstring, String methodName) {
        softAssert.assertTrue(actual != null && actual.contains(expectedSubstring),
                methodName + " - expected actual to contain: " + expectedSubstring + " but found: " + actual);
        LoggerUtil.info(methodName + " checked (soft) actual to contain expected substring");
    }

    /**
     * Hard asserts that the actual string equals the expected string,
     * ignoring differences in upper and lower case. Expects the actual
     * string, the expected string, and the name of the calling method for
     * logging. Stops the test immediately if the actual string is null or
     * does not match, ignoring case.
     */
    public void assertEqualsIgnoreCase(String actual, String expected, String methodName) {
        Assert.assertTrue(actual != null && actual.equalsIgnoreCase(expected),
                methodName + " - expected (ignoring case): " + expected + " but found: " + actual);
        LoggerUtil.info(methodName + " found the actual to equal expected (case-insensitive)");
    }

    /**
     * Soft asserts that the actual string equals the expected string,
     * ignoring differences in upper and lower case. Expects the actual
     * string, the expected string, and the name of the calling method for
     * logging. Records a failure without stopping the test if the actual
     * string is null or does not match, ignoring case.
     */
    public void softAssertEqualsIgnoreCase(String actual, String expected, String methodName) {
        softAssert.assertTrue(actual != null && actual.equalsIgnoreCase(expected),
                methodName + " - expected (ignoring case): " + expected + " but found: " + actual);
        LoggerUtil.info(methodName + " checked (soft) actual to equal expected (case-insensitive)");
    }

    /**
     * Fails the test immediately with the given reason. Expects the name
     * of the calling method and the reason the test should fail. Use this
     * when the code has already worked out that a test should fail,
     * rather than comparing two values.
     */
    public void failTest(String methodName, String reason) {
        LoggerUtil.info(methodName + " failed explicitly: " + reason);
        Assert.fail(methodName + " - " + reason);
    }

    /**
     * Records a soft failure with the given reason, without stopping the
     * test. Expects the name of the calling method and the reason for the
     * failure.
     */
    public void softFailTest(String methodName, String reason) {
        LoggerUtil.info(methodName + " failed explicitly (soft): " + reason);
        softAssert.fail(methodName + " - " + reason);
    }

    /**
     * Hard asserts that the actual string does not contain the given
     * substring. Expects the actual string, the substring it must not
     * contain, and the name of the calling method for logging. Stops the
     * test immediately if the actual string is null or contains the
     * substring.
     */
    public void assertNotContains(String actual, String unexpectedSubstring, String methodName) {
        Assert.assertTrue(actual != null && !actual.contains(unexpectedSubstring),
                methodName + " - did not expect actual to contain: " + unexpectedSubstring + " but found: " + actual);
        LoggerUtil.info(methodName + " found the actual to not contain unexpected substring");
    }

    /**
     * Soft asserts that the actual string does not contain the given
     * substring. Expects the actual string, the substring it must not
     * contain, and the name of the calling method for logging. Records a
     * failure without stopping the test if the actual string is null or
     * contains the substring.
     */
    public void softAssertNotContains(String actual, String unexpectedSubstring, String methodName) {
        softAssert.assertTrue(actual != null && !actual.contains(unexpectedSubstring),
                methodName + " - did not expect actual to contain: " + unexpectedSubstring + " but found: " + actual);
        LoggerUtil.info(methodName + " checked (soft) actual to not contain unexpected substring");
    }

    /**
     * Hard asserts that the actual value is greater than the given
     * threshold. Expects the actual value, the threshold to compare
     * against, and the name of the calling method for logging. Stops the
     * test immediately if the actual value is not greater than the
     * threshold.
     */
    public <T extends Comparable<T>> void assertGreaterThan(T actual, T threshold, String methodName) {
        Assert.assertTrue(actual.compareTo(threshold) > 0,
                methodName + " - expected " + actual + " to be greater than " + threshold);
        LoggerUtil.info(methodName + " found the actual to be greater than threshold");
    }

    /**
     * Hard asserts that the actual value is less than the given
     * threshold. Expects the actual value, the threshold to compare
     * against, and the name of the calling method for logging. Stops the
     * test immediately if the actual value is not less than the
     * threshold.
     */
    public <T extends Comparable<T>> void assertLessThan(T actual, T threshold, String methodName) {
        Assert.assertTrue(actual.compareTo(threshold) < 0,
                methodName + " - expected " + actual + " to be less than " + threshold);
        LoggerUtil.info(methodName + " found the actual to be less than threshold");
    }

    /**
     * Hard asserts that a collection has the expected number of items.
     * Expects the collection to check, the size it should have, and the
     * name of the calling method for logging. Stops the test immediately
     * if the collection's size does not match.
     */
    public void assertSizeEquals(java.util.Collection<?> actual, int expectedSize, String methodName) {
        Assert.assertEquals(actual.size(), expectedSize,
                methodName + " - expected size: " + expectedSize + " but found: " + actual.size());
        LoggerUtil.info(methodName + " found the actual size to equal expected size");
    }

    /**
     * Hard asserts whether the actual string is empty or not empty, based
     * on the expected state. Expects the actual string, the expected
     * empty state, and the name of the calling method for logging. Stops
     * the test immediately if the actual string is null or does not
     * match the expected state.
     */
    public void assertEmptyState(String actual, EmptyState expectedState, String methodName) {
        if (expectedState == EmptyState.EMPTY) {
            Assert.assertTrue(actual != null && actual.isEmpty(), methodName + " - expected empty but found: " + actual);
        } else {
            Assert.assertTrue(actual != null && !actual.isEmpty(), methodName + " - expected non-empty but found empty");
        }
        LoggerUtil.info(methodName + " found the actual to be " + expectedState.toString().toLowerCase());
    }

    /**
     * Checks every soft assertion made so far on the shared SoftAssert and
     * fails the test if any of them failed. Call this at the end of a test
     * once all soft assertions have been made, otherwise soft assertion
     * failures will never be reported.
     */
    public void assertAll() {
        softAssert.assertAll();
    }
}
