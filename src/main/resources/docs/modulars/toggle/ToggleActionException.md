# ToggleActionException

Package: `io.github.phillmon.selenium.modulars.toggle`

Thrown when [ToggleActions](ToggleActions.md) can't confirm the expected
on/off state of a toggle, either because it didn't change after being
clicked, or because it didn't become visible within the expected time.

## Related classes

- [ToggleActions](ToggleActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `ToggleActionException(String message)` | No underlying cause. |
| `ToggleActionException(String message, Throwable cause)` | Wraps the original Selenium timeout. |

[Back to toggle index](README.md) · [Docs home](../../README.md)
