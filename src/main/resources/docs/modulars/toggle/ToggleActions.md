# ToggleActions

Package: `io.github.phillmon.selenium.modulars.toggle`

Actions for working with toggle-style elements, such as switches and
checkboxes, that can be turned on or off. Uses a
[ToggleStateResolver](ToggleStateResolver.md) to decide whether an
element currently counts as on, since different widgets show that state
differently.

## Related classes

- [ToggleStateResolver](ToggleStateResolver.md) — supplied at construction time to read the on/off state.
- [ToggleActionException](ToggleActionException.md) — thrown when a toggle doesn't reach or confirm the expected state.
- [PageModularOptions](../../base/PageModularOptions.md) — `withToggleStateResolver` configures the resolver used by `modulars.toggleActions`.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.toggleActions`.

## Usage example

```java
modulars.toggleActions.ensureToggledOn(darkModeSwitch, "dark mode switch", "enableDarkMode");
modulars.toggleActions.assertToggledOn(darkModeSwitch, "dark mode switch", "verifyDarkModeEnabled");

boolean isEmailOptIn = modulars.toggleActions.isOn(emailOptInSwitch, "email opt-in switch", "checkEmailPreference");
```

## Constructors

| Constructor | Description |
|---|---|
| `ToggleActions(WebDriver, ToggleStateResolver)` | Default timeout of 10 seconds. |
| `ToggleActions(WebDriver, ToggleStateResolver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `ensureToggledOn(locator, elementName, methodName)` | Clicks the toggle only if it's currently off, then confirms it's on. |
| `ensureToggledOff(locator, elementName, methodName)` | Clicks the toggle only if it's currently on, then confirms it's off. |
| `assertToggledOn(locator, elementName, methodName)` | Asserts the toggle is on, without clicking it. |
| `assertToggledOff(locator, elementName, methodName)` | Asserts the toggle is off, without clicking it. |
| `isOn(locator, elementName, methodName)` | Returns whether the toggle is currently on. |

All methods throw [ToggleActionException](ToggleActionException.md) if
the toggle doesn't become visible in time, or (for `ensure*`) if it
doesn't actually change state after being clicked, or (for `assert*`) if
it isn't in the expected state.

[Back to toggle index](README.md) · [Docs home](../../README.md)
