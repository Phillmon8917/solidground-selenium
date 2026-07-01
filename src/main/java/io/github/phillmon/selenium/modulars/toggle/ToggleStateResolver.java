package io.github.phillmon.selenium.modulars.toggle;

import org.openqa.selenium.WebElement;

/**
 * Describes how to tell whether a toggle-style element is currently on,
 * since different widgets show that state differently: an aria-checked
 * attribute, a checkbox's own selected state, a custom attribute value, a
 * CSS class, or matching text content. ToggleActions uses one of these to
 * read and confirm toggle state without needing to know how the widget
 * itself is built.
 */
@FunctionalInterface
public interface ToggleStateResolver {
    /**
     * Returns whether the given element currently counts as on.
     *
     * @param element the toggle-style element to inspect
     * @return true if the element currently counts as on, false otherwise
     */
    boolean isOn(WebElement element);

    /**
     * Returns a resolver that reads the element's aria-checked attribute,
     * treating "true" (ignoring case) as on.
     *
     * @return a resolver based on the aria-checked attribute
     */
    static ToggleStateResolver ariaChecked() {
        return element -> "true".equalsIgnoreCase(element.getAttribute("aria-checked"));
    }

    /**
     * Returns a resolver that uses a checkbox's own selected state,
     * suitable for plain HTML checkboxes and radio buttons.
     *
     * @return a resolver based on the element's selected state
     */
    static ToggleStateResolver checkboxSelected() {
        return WebElement::isSelected;
    }

    /**
     * Returns a resolver that reads a custom attribute and treats it as
     * on when its value matches the given expected value, ignoring case.
     * Expects the attribute name to read and the value that means on.
     *
     * @param attribute the name of the attribute to read
     * @param onValue the attribute value that means the element is on
     * @return a resolver based on the given attribute's value
     */
    static ToggleStateResolver attributeEquals(String attribute, String onValue) {
        return element -> onValue.equalsIgnoreCase(element.getAttribute(attribute));
    }

    /**
     * Returns a resolver that treats the element as on when a given CSS
     * class is present among its class attribute's classes. Expects the
     * class name that indicates the on state.
     *
     * @param onClass the CSS class name that indicates the on state
     * @return a resolver based on the presence of the given CSS class
     */
    static ToggleStateResolver cssClassPresent(String onClass) {
        return element -> {
            String classAttr = element.getAttribute("class");
            return classAttr != null && java.util.Arrays.asList(classAttr.trim().split("\\s+")).contains(onClass);
        };
    }

    /**
     * Returns a resolver that treats the element as on when its visible
     * text contains a marker, where the marker can come from an attribute
     * on the element itself or fall back to a fixed default if that
     * attribute is not set. Expects the name of the attribute that may
     * hold a custom marker, and the marker text to use when that
     * attribute is missing.
     *
     * @param onAttribute the name of the attribute that may hold a custom marker
     * @param defaultOnMarker the marker text to use when that attribute is missing
     * @return a resolver based on the element's visible text content
     */
    static ToggleStateResolver textContentMatchesOnAttribute(String onAttribute, String defaultOnMarker) {
        return element -> {
            String onMarker = element.getAttribute(onAttribute);
            String currentText = element.getText();
            String expectedMarker = (onMarker != null ? onMarker.trim() : defaultOnMarker);
            return currentText != null && currentText.trim().contains(expectedMarker);
        };
    }
}
