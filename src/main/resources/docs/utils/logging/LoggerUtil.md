# LoggerUtil

Package: `io.github.phillmon.selenium.utils.logging`

The single place every action class in the framework sends its log
messages through. Every message is written to the slf4j logger, to a
fresh execution log file for the current thread, and to whichever
[TestReporter](TestReporter.md) instances are registered on that thread
([AllureReporter](AllureReporter.md) by default), so the same log line
ends up in the console, on disk, and in the test report all at once.

Reporter registration is thread-local. Calling `setReporter` or `addReporter`
in one parallel test thread does not change reporters used by another thread.
Execution logs are written under `target/logs` with one file per thread.

## Related classes

- [TestReporter](TestReporter.md) — the interface every registered reporter implements.
- [AllureReporter](AllureReporter.md) / [TestNgReporter](TestNgReporter.md) / [NoOpReporter](NoOpReporter.md) — the built-in reporter implementations.
- [FaultReporter](../../errors/FaultReporter.md) — logs structured fault records through this class.
- Every `modulars.*Actions` class — logs through this after each action.

## Usage example

Most code just logs directly:

```java
LoggerUtil.info("Navigated to checkout page");
LoggerUtil.warning("Retry limit reached, continuing anyway");
LoggerUtil.error("Failed to save profile", exception);
```

Switching reporters for the current thread, for example to use TestNG's
reporter instead of Allure:

```java
LoggerUtil.setReporter(new TestNgReporter());
```

Attaching an extra reporter alongside the current one:

```java
LoggerUtil.addReporter(new TestNgReporter());
```

Turning off test-report attachments entirely, keeping only the file log:

```java
LoggerUtil.setReporter(new NoOpReporter());
```

## Methods

| Method | Description |
|---|---|
| `static void setReporter(TestReporter)` | Replaces every reporter registered on the current thread with a single one; pass `null` to attach to none. |
| `static void addReporter(TestReporter)` | Adds an extra reporter alongside whatever is already registered on the current thread. |
| `static void info(String message)` | Logs an informational message. |
| `static void warning(String message)` | Logs a warning message. |
| `static void error(String message)` | Logs an error message with no exception. |
| `static void error(String message, Throwable)` | Logs an error message together with its exception. |

[Back to logging index](README.md) · [Docs home](../../README.md)
