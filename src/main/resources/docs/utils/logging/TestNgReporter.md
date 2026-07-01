# TestNgReporter

Package: `io.github.phillmon.selenium.utils.logging`

Sends log messages to TestNG's own reporter, so they show up in TestNG's
HTML report output. Use this with [LoggerUtil](LoggerUtil.md)`.setReporter`
as an alternative to the default Allure reporter, or alongside it with
`addReporter`.

## Related classes

- [TestReporter](TestReporter.md) — the interface this class implements.
- [LoggerUtil](LoggerUtil.md) — registers reporters like this one.
- [AllureReporter](AllureReporter.md) — the default reporter this one is often swapped in for.

## Usage example

```java
LoggerUtil.setReporter(new TestNgReporter());

// Or use both at once:
LoggerUtil.addReporter(new TestNgReporter());
```

## Methods

| Method | Description |
|---|---|
| `info(String message)` | Logs with an `[INFO]` prefix. |
| `warning(String message)` | Logs with a `[WARNING]` prefix. |
| `error(String message)` | Logs with an `[ERROR]` prefix. |
| `error(String message, Throwable)` | Logs the message and the exception's message with an `[ERROR]` prefix. |

[Back to logging index](README.md) · [Docs home](../../README.md)
