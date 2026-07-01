# MouseActionException

Package: `io.github.phillmon.selenium.modulars.mouse`

Thrown when [MouseActions](MouseActions.md) can't find an element in the
state it needs within the expected time, when a chained gesture such as
`hoverChain` is given mismatched arguments, or when a slow drag and drop
is interrupted.

## Related classes

- [MouseActions](MouseActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `MouseActionException(String message)` | No underlying cause. |
| `MouseActionException(String message, Throwable cause)` | Wraps the original Selenium timeout or interruption. |

[Back to mouse index](README.md) · [Docs home](../../README.md)
