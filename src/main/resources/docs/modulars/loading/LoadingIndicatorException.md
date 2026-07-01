# LoadingIndicatorException

Package: `io.github.phillmon.selenium.modulars.loading`

Thrown when [LoadingIndicatorActions](LoadingIndicatorActions.md) can't
confirm that a loading indicator disappeared within the expected time, or
when the arguments for waiting on several indicators at once don't match
up.

## Related classes

- [LoadingIndicatorActions](LoadingIndicatorActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `LoadingIndicatorException(String message)` | No underlying cause. |
| `LoadingIndicatorException(String message, Throwable cause)` | Wraps the original Selenium timeout. |

[Back to loading index](README.md) · [Docs home](../../README.md)
