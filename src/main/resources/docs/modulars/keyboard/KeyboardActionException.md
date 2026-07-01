# KeyboardActionException

Package: `io.github.phillmon.selenium.modulars.keyboard`

Thrown when [KeyboardActions](KeyboardActions.md) can't find the element
it needs to send keys to within the expected time.

## Related classes

- [KeyboardActions](KeyboardActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `KeyboardActionException(String message)` | No underlying cause. |
| `KeyboardActionException(String message, Throwable cause)` | Wraps the original Selenium timeout. |

[Back to keyboard index](README.md) · [Docs home](../../README.md)
