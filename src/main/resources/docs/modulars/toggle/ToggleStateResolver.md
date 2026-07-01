# ToggleStateResolver

Package: `io.github.phillmon.selenium.modulars.toggle` (functional interface)

Describes how to tell whether a toggle-style element is currently on: an
`aria-checked` attribute, a checkbox's own selected state, a custom
attribute value, a CSS class, or matching text content.
[ToggleActions](ToggleActions.md) uses one of these to read and confirm
toggle state without needing to know how the widget itself is built.

## Related classes

- [ToggleActions](ToggleActions.md) — the class that uses a resolver instance.
- [PageModularOptions](../../base/PageModularOptions.md) — `withToggleStateResolver` sets the resolver used for a page object's `modulars.toggleActions`.

## Usage example

```java
// Default used unless overridden:
ToggleStateResolver resolver = ToggleStateResolver.ariaChecked();

// A plain HTML checkbox instead:
ToggleStateResolver checkboxResolver = ToggleStateResolver.checkboxSelected();

// A custom widget using a data attribute:
ToggleStateResolver customResolver = ToggleStateResolver.attributeEquals("data-state", "on");

PageModularOptions options = PageModularOptions.builder()
        .withToggleStateResolver(customResolver)
        .build();
```

A resolver based on visible text, such as a switch that shows "ON"/"OFF":

```java
ToggleStateResolver textResolver = ToggleStateResolver.textContentMatchesOnAttribute("data-on-label", "ON");
```

## Static factories

| Method | Description |
|---|---|
| `static ToggleStateResolver ariaChecked()` | Reads `aria-checked="true"` (default). |
| `static ToggleStateResolver checkboxSelected()` | Uses the element's own `isSelected()`. |
| `static ToggleStateResolver attributeEquals(String attribute, String onValue)` | Treats a matching attribute value as on. |
| `static ToggleStateResolver cssClassPresent(String onClass)` | Treats a present CSS class as on. |
| `static ToggleStateResolver textContentMatchesOnAttribute(String onAttribute, String defaultOnMarker)` | Treats matching visible text as on. |

## Method

| Method | Description |
|---|---|
| `boolean isOn(WebElement)` | Returns whether the given element currently counts as on. |

[Back to toggle index](README.md) · [Docs home](../../README.md)
