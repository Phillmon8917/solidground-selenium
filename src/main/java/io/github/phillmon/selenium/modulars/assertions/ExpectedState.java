package io.github.phillmon.selenium.modulars.assertions;

/**
 * Describes whether a boolean condition is expected to be true or false.
 * Used with AssertionActions.assertConditionState and
 * softAssertConditionState to say which of the two states an assertion
 * should check for.
 */
public enum ExpectedState {
    TRUE,
    FALSE
}
