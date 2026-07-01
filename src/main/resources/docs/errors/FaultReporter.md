# FaultReporter

Package: `io.github.phillmon.selenium.errors`

The central place every caught failure in the framework gets logged
through. Builds a structured fault record, makes sure the same failure
isn't logged twice, and falls back to a simple message if the structured
logging itself fails.

## Related classes

- [SafeStep](SafeStep.md) — calls `captureFault` whenever a wrapped step fails.
- [FaultAnalyzer](FaultAnalyzer.md) — builds the [FaultDetails](FaultDetails.md) record that gets logged.
- [FaultCache](FaultCache.md) — used internally to avoid duplicate reports.
- [StepExecutionException](StepExecutionException.md) — the exception type created by `logAndThrow` and `log`.
- [LoggerUtil](../utils/logging/LoggerUtil.md) — where the structured fault text actually gets written.

## Usage example

```java
try {
    driver.findElement(By.id("balance")).getText();
} catch (NoSuchElementException e) {
    FaultReporter.captureFault(e, "readAccountBalance", "balance widget was not on the page");
}
```

Stopping the test with a clear, logged reason:

```java
if (!EnvLoader.getUrl().startsWith("https://")) {
    FaultReporter.logAndThrow("loadThePage", "Refusing to run against a non-HTTPS environment");
}
```

Logging something without stopping the test:

```java
FaultReporter.log("cleanupTempFiles", "Could not delete one temp file, continuing anyway");
```

Clearing the dedupe cache between test cases, so a recurring issue is
reported for every test rather than just the first:

```java
@BeforeMethod
public void resetFaultCache() {
    FaultReporter.clearFaultCache();
}
```

## Methods

| Method | Description |
|---|---|
| `void captureFault(Throwable, String source)` | Reports a failure with no extra context. |
| `void captureFault(Throwable, String source, String context)` | Reports a failure with extra context about what was happening. |
| `void logAndThrow(String source, String message)` | Logs a new failure built from `message` and then throws it. |
| `void log(String source, String message)` | Logs a failure built from `message` without throwing. |
| `void clearFaultCache()` | Forgets every failure reported so far. |

[Back to errors index](README.md) · [Docs home](../README.md)
