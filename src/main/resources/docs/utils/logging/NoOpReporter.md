# NoOpReporter

Package: `io.github.phillmon.selenium.utils.logging`

A reporter that does nothing with any message it receives. Use this with
[LoggerUtil](LoggerUtil.md)`.setReporter` when only the file log is
wanted, with no messages attached to any test report.

## Related classes

- [TestReporter](TestReporter.md) — the interface this class implements.
- [LoggerUtil](LoggerUtil.md) — registers reporters like this one.

## Usage example

```java
LoggerUtil.setReporter(new NoOpReporter());
```

## Methods

| Method | Description |
|---|---|
| `info(String message)` | Does nothing. |
| `warning(String message)` | Does nothing. |
| `error(String message)` | Does nothing. |
| `error(String message, Throwable)` | Does nothing. |

[Back to logging index](README.md) · [Docs home](../../README.md)
