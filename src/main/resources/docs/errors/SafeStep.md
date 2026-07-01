# SafeStep

Package: `io.github.phillmon.selenium.errors`

Runs a page-object step so callers don't have to write their own
try/catch. If the step throws, `SafeStep` takes a screenshot of the
current browser state, reports the failure through
[FaultReporter](FaultReporter.md), and rethrows the same failure so the
test still fails normally.

## Related classes

- [FaultReporter](FaultReporter.md) — receives the failure for logging.
- [FaultAnalyzer](FaultAnalyzer.md) — used to build the message for a failed screenshot capture.
- [StepExecutionException](StepExecutionException.md) — thrown when the caught exception wasn't already unchecked.
- [ThrowingRunnable](ThrowingRunnable.md) / [ThrowingSupplier](ThrowingSupplier.md) — the step shapes `SafeStep.run` accepts.
- [BasePage](../base/BasePage.md) — page objects that extend it typically call `SafeStep.run` from their methods.

## Usage example

```java
public void loadThePage() {
    SafeStep.run(driver, "loadThePage", () -> {
        modulars.browserActions.navigateToUrl(EnvLoader.getUrl(), "loadThePage");
        modulars.loadingIndicatorActions.waitForPageLoader("loadThePage");
    });
}
```

A label can be omitted — it's worked out automatically from the calling
method's name:

```java
public void loadThePage() {
    SafeStep.run(driver, () -> {
        modulars.browserActions.navigateToUrl(EnvLoader.getUrl(), "loadThePage");
        modulars.loadingIndicatorActions.waitForPageLoader("loadThePage");
    });
}
```

A step that returns a value:

```java
String title = SafeStep.run(driver, "readPageTitle", () -> modulars.browserActions.getPageTitle());
```

Several steps that should run in order, each labelled with its position:

```java
SafeStep.runSequence(driver, "submitForm", List.of(
        () -> modulars.elementActions.typeText(nameField, "Jane", "name field", "submitForm"),
        () -> modulars.elementActions.click(submitButton, "submit button", "submitForm")
));
```

## Methods

| Method | Description |
|---|---|
| `<T> T run(WebDriver, ThrowingSupplier<T>)` | Runs a value-returning step with an auto-derived label. |
| `<T> T run(WebDriver, String label, ThrowingSupplier<T>)` | Runs a value-returning step with the given label. |
| `void run(WebDriver, ThrowingRunnable)` | Runs a void step with an auto-derived label. |
| `void run(WebDriver, String label, ThrowingRunnable)` | Runs a void step with the given label. |
| `void runSequence(WebDriver, String label, List<ThrowingRunnable>)` | Runs several void steps in order, each tagged `label [step N]`. |

[Back to errors index](README.md) · [Docs home](../README.md)
