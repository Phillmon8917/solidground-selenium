# ElementActionException

Package: `io.github.phillmon.selenium.modulars.element`

Thrown when [ElementActions](ElementActions.md) can't find an element in
the state it needs — visible, clickable, present, or invisible — within
the configured timeout.

## Related classes

- [ElementActions](ElementActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `ElementActionException(String message)` | No underlying cause. |
| `ElementActionException(String message, Throwable cause)` | Wraps the original Selenium `TimeoutException` or `InterruptedException`. |

[Back to element index](README.md) · [Docs home](../../README.md)
