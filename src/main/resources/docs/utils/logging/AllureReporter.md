# AllureReporter

Package: `io.github.phillmon.selenium.utils.logging`

Sends log messages to the Allure test report as attachments, so
information messages, warnings, and errors logged during a test show up
alongside the test's result in the Allure report. This is the default
reporter [LoggerUtil](LoggerUtil.md) uses out of the box.

## Related classes

- [TestReporter](TestReporter.md) — the interface this class implements.
- [LoggerUtil](LoggerUtil.md) — registers one of these automatically at startup.
- [SafeStep](../../errors/SafeStep.md) — also attaches failure screenshots to Allure directly.

## Usage example

Usually you don't construct this yourself — it's already registered.
Explicitly restoring it after switching away is one case where you would:

```java
LoggerUtil.setReporter(new AllureReporter());
```

## Methods

| Method | Description |
|---|---|
| `info(String message)` | Attaches the message under an INFO label. |
| `warning(String message)` | Attaches the message under a WARNING label. |
| `error(String message)` | Attaches the message under an ERROR label. |
| `error(String message, Throwable)` | Attaches the message and the exception's message under an ERROR label. |

[Back to logging index](README.md) · [Docs home](../../README.md)
