# KeyboardActions

Package: `io.github.phillmon.selenium.modulars.keyboard`

Actions for sending keyboard input, either to a specific element or to
the page as a whole: single keys, key combinations such as Ctrl+C,
shortcuts like copy/paste/undo, arrow key navigation, and holding a
modifier key down while clicking.

## Related classes

- [KeyboardActionException](KeyboardActionException.md) — thrown when the target element doesn't become visible in time.
- [ElementActions](../element/ElementActions.md) — the usual companion for locating and reading elements before/after sending keys.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.keyboardActions`.

## Usage example

```java
modulars.keyboardActions.pressEnter(searchBox, "search box", "runSearch");
modulars.keyboardActions.selectAll(commentBox, "comment box", "clearComment");
modulars.keyboardActions.backspace(commentBox, 5, "comment box", "trimTrailingCharacters");

// Ctrl+click to open a search result in a new tab:
modulars.keyboardActions.holdKeyWhileClicking(searchResultLink, Keys.CONTROL, "search result link", "openResultInNewTab");
```

## Constructors

| Constructor | Description |
|---|---|
| `KeyboardActions(WebDriver)` | Default timeout of 10 seconds. |
| `KeyboardActions(WebDriver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `pressKey(locator, Keys, elementName, methodName)` | Sends a single key press. |
| `pressKeys(locator, elementName, methodName, Keys...)` | Sends several key presses in order. |
| `pressChord(locator, modifier, key, elementName, methodName)` | Sends a combination like Ctrl+A. |
| `pressEnter` / `pressTab` / `pressEscape` | Convenience wrappers around `pressKey`. |
| `selectAll(locator, elementName, methodName)` | Ctrl+A. |
| `copy` / `cut` / `paste` / `undo` | Ctrl+C / Ctrl+X / Ctrl+V / Ctrl+Z. |
| `backspace(locator, times, elementName, methodName)` | Presses backspace `times` times. |
| `arrowDown` / `arrowUp` / `arrowLeft` / `arrowRight` | Presses an arrow key `times` times. |
| `pageGlobalKey(Keys, methodName)` | Sends a key to the page rather than an element. |
| `pageGlobalChord(modifier, key, methodName)` | Sends a chord to the page rather than an element. |
| `holdKeyWhileClicking(locator, modifier, elementName, methodName)` | Holds a modifier down while clicking, e.g. Ctrl+click. |

All element-targeted methods throw
[KeyboardActionException](KeyboardActionException.md) if the element
doesn't become visible within the configured timeout.

[Back to keyboard index](README.md) · [Docs home](../../README.md)
