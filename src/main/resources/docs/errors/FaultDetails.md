# FaultDetails

Package: `io.github.phillmon.selenium.errors` (Java `record`)

Holds everything collected about one failure: when it happened, where it
happened, what extra context was given, what kind of exception it was,
its message, and its stack trace.

## Related classes

- [FaultAnalyzer](FaultAnalyzer.md) — builds instances of this record.
- [FaultReporter](FaultReporter.md) — calls `toLogString()` and writes the result to the log.

## Usage example

```java
FaultDetails details = FaultAnalyzer.createDetails(exception, "submitOrder", "checkout flow");
LoggerUtil.error(details.toLogString(), exception);
```

## Components

| Component | Description |
|---|---|
| `timestamp` | When the fault was captured, as an ISO instant string. |
| `source` | The name of the place where the error happened. |
| `context` | Optional extra detail about what was going on, or null. |
| `exceptionType` | The full class name of the exception. |
| `message` | The exception's message (see [FaultAnalyzer.getMessage](FaultAnalyzer.md)). |
| `stackTrace` | The exception's full stack trace as text. |

## Methods

| Method | Description |
|---|---|
| `String toLogString()` | Formats every field into one plain text block ready to write to the log, including the context line only when a context was given. |

[Back to errors index](README.md) · [Docs home](../README.md)
