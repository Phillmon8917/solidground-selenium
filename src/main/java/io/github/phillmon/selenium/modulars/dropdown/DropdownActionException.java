package io.github.phillmon.selenium.modulars.dropdown;

/**
 * Thrown when SelectDropdownActions cannot carry out a requested action,
 * such as selecting an option that does not exist, or trying to deselect
 * an option on a dropdown that is not a multi-select.
 */
public class DropdownActionException extends RuntimeException {
    /**
     * Creates the exception with just a message, for cases where there is
     * no underlying cause to attach.
     */
    public DropdownActionException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original exception
     * that caused it, so the real cause is still visible in the stack
     * trace.
     */
    public DropdownActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
