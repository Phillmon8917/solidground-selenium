# TestReporter

Package: `io.github.phillmon.selenium.utils.logging` (interface)

Describes a destination that test-execution messages can be sent to,
such as Allure, TestNG's own reporter, or a customer's own reporting
tool. [LoggerUtil](LoggerUtil.md) sends every message it logs to
whichever reporters are registered, in addition to always writing to the
file log.

## Related classes

- [LoggerUtil](LoggerUtil.md) — registers and dispatches to implementations of this interface.
- [AllureReporter](AllureReporter.md), [TestNgReporter](TestNgReporter.md), [NoOpReporter](NoOpReporter.md) — the built-in implementations.

## Usage example

Implementing a custom reporter, for example one that posts errors to a
Slack webhook:

```java
public class SlackReporter implements TestReporter {
    @Override public void info(String message) { }
    @Override public void warning(String message) { }

    @Override
    public void error(String message) {
        postToSlack("[ERROR] " + message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        postToSlack("[ERROR] " + message + "\n" + throwable.getMessage());
    }
}

LoggerUtil.addReporter(new SlackReporter());
```

## Methods

| Method | Description |
|---|---|
| `void info(String message)` | Records an informational message. |
| `void warning(String message)` | Records a warning message. |
| `void error(String message)` | Records an error message with no exception. |
| `void error(String message, Throwable)` | Records an error message together with its exception. |

[Back to logging index](README.md) · [Docs home](../../README.md)
