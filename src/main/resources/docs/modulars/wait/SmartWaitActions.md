# SmartWaitActions

Package: `io.github.phillmon.selenium.modulars.wait`

Waits for a value to stop changing rather than just checking it once —
useful for values that update gradually, such as text being typed by a
script, an input still being auto-formatted, an element still animating
into position, or a file still being downloaded. A value only counts as
stable once it reads the same on several checks in a row.

## Related classes

- [WaitStabilizationException](WaitStabilizationException.md) — thrown when a value never settles in time.
- [DateObject](../calendar/DateObject.md) / [DateFormat](../calendar/DateFormat.md) — used by `waitForDateInputToStabilize`.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.waitActions`.

## Usage example

```java
String finalTotal = modulars.waitActions.waitForTextToStabilize(cartTotalLocator, "cart total", "verifyCartTotal");

DateObject checkIn = modulars.waitActions.waitForDateInputToStabilize(
        checkInDateInput, DateFormat.SHORT_SLASH, "check-in date input", "verifyAutoFilledDate");

modulars.waitActions.waitForElementToStopMoving(toastNotification, "toast notification", "dismissToast");

File downloadedReport = modulars.waitActions.waitForFileDownloadToComplete(
        Path.of("build/downloads"), "report-.*\\.pdf", "downloadReport");
```

## Constructors

| Constructor | Description |
|---|---|
| `SmartWaitActions(WebDriver)` | Poll every 250ms, 3 matching checks, 10-second timeout. |
| `SmartWaitActions(WebDriver, Duration timeout)` | Custom timeout, default polling. |
| `SmartWaitActions(WebDriver, Duration pollInterval, int requiredStableChecks, Duration timeout)` | Full control over polling behaviour. |

## Methods

| Method | Description |
|---|---|
| `waitForInputValueToStabilize(locator, elementName, methodName)` | Waits for an input's value to stop changing. |
| `waitForInputValueToStabilize(locator, sensitive, elementName, methodName)` | Same, with sensitive-value log masking. |
| `waitForTextToStabilize(locator, elementName, methodName)` | Waits for an element's visible text to stop changing. |
| `waitForAttributeToStabilize(locator, attribute, elementName, methodName)` | Waits for an attribute's value to stop changing. |
| `waitForElementCountToStabilize(locator, elementName, methodName)` | Waits for the number of matching elements to stop changing. |
| `waitForElementToStopMoving(locator, elementName, methodName)` | Waits for an element's position/size to stop changing. |
| `waitForDateInputToStabilize(locator, DateFormat, elementName, methodName)` | Waits for a date input to settle, then parses it. |
| `waitForFileDownloadToComplete(downloadDirectory, fileNamePattern, methodName)` | Waits for a matching file to appear and finish downloading. |

All methods throw
[WaitStabilizationException](WaitStabilizationException.md) if the value
never stabilizes within the configured timeout.

[Back to wait index](README.md) · [Docs home](../../README.md)
