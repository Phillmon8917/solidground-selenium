package io.github.phillmon.selenium.modulars.assertions;

/**
 * Describes whether a value is expected to be null or not null. Used with
 * AssertionActions.assertNullState and softAssertNullState to say which
 * of the two states an assertion should check for.
 */
public enum NullState {
    NULL,
    NOT_NULL
}
