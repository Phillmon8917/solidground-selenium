# WaitStabilizationException

Package: `io.github.phillmon.selenium.modulars.wait`

Thrown when [SmartWaitActions](SmartWaitActions.md) can't confirm that a
value, element position, or file download has settled into a stable
state within the expected time.

## Related classes

- [SmartWaitActions](SmartWaitActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `WaitStabilizationException(String message)` | No underlying cause. |
| `WaitStabilizationException(String message, Throwable cause)` | Wraps the original date-parse failure or thread interruption. |

[Back to wait index](README.md) · [Docs home](../../README.md)
