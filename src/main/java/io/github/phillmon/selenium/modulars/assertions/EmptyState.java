package io.github.phillmon.selenium.modulars.assertions;

/**
 * Describes whether a string is expected to be empty or not empty. Used
 * with AssertionActions.assertEmptyState to say which of the two states
 * an assertion should check for.
 */
public enum EmptyState {
    EMPTY,
    NOT_EMPTY
}
