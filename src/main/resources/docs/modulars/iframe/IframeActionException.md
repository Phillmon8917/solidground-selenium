# IframeActionException

Package: `io.github.phillmon.selenium.modulars.iframe`

Thrown when [IframeActions](IframeActions.md) can't switch into a
requested frame, when switching to a parent frame is attempted from the
top-level page, or when nested-frame arguments don't match up.

## Related classes

- [IframeActions](IframeActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `IframeActionException(String message)` | No underlying cause. |
| `IframeActionException(String message, Throwable cause)` | Wraps the original Selenium timeout. |

[Back to iframe index](README.md) · [Docs home](../../README.md)
