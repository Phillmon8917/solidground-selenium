# StepExecutionException

Package: `io.github.phillmon.selenium.errors`

Unchecked exception thrown when a step run through
[SafeStep](SafeStep.md) or [FaultReporter](FaultReporter.md) fails and the
original error wasn't already a `RuntimeException`. Wrapping it this way
means callers never have to declare or catch checked exceptions just to
use `SafeStep`.

## Related classes

- [SafeStep](SafeStep.md) — throws this when a wrapped step's failure was a checked exception.
- [FaultReporter](FaultReporter.md) — uses this as the exception type behind `logAndThrow` and `log`.

## Usage example

```java
try {
    SafeStep.run(driver, "parseConfirmationNumber", () -> Integer.parseInt(rawText));
} catch (StepExecutionException e) {
    // e.getCause() is the original NumberFormatException
}
```

## Constructors

| Constructor | Description |
|---|---|
| `StepExecutionException(String message)` | No underlying cause to attach, such as a manually logged failure. |
| `StepExecutionException(String message, Throwable cause)` | Wraps the original exception so the real cause stays visible in the stack trace. |

[Back to errors index](README.md) · [Docs home](../README.md)
