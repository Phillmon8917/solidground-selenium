# FaultAnalyzer

Package: `io.github.phillmon.selenium.errors`

Turns a raised exception into a [FaultDetails](FaultDetails.md) record,
and reads plain information out of an exception such as its message and
stack trace. Does no logging or storage itself — it only reads and
formats data from a `Throwable`.

## Related classes

- [FaultDetails](FaultDetails.md) — the record type this class builds.
- [FaultReporter](FaultReporter.md) — the main caller of `createDetails`.
- [SafeStep](SafeStep.md) — calls `getMessage` when a screenshot capture itself fails.

## Usage example

```java
try {
    riskyStep();
} catch (Exception e) {
    FaultDetails details = FaultAnalyzer.createDetails(e, "riskyStep", "attempt 2 of 3");
    LoggerUtil.error(details.toLogString(), e);
}
```

Getting a safe, always-non-null message for logging:

```java
String message = FaultAnalyzer.getMessage(exception);
```

## Methods

| Method | Description |
|---|---|
| `FaultDetails createDetails(Throwable, String source, String context)` | Builds a [FaultDetails](FaultDetails.md) with the current time, source, context, exception type, message, and stack trace. |
| `String getMessage(Throwable)` | Returns the exception's own message, or its simple class name if the message is null or blank. |

[Back to errors index](README.md) · [Docs home](../README.md)
