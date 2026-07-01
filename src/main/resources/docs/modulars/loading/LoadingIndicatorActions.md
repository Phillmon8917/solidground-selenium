# LoadingIndicatorActions

Package: `io.github.phillmon.selenium.modulars.loading`

Actions for waiting on loading indicators — spinners, skeleton
placeholders, and page loaders — to appear and disappear, so tests can
wait for them to clear before checking the real content underneath.

## Related classes

- [LoadingIndicatorException](LoadingIndicatorException.md) — thrown when an indicator doesn't disappear in time.
- [BrowserActions](../browser/BrowserActions.md) — typically used right after navigating, before waiting on a loader.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.loadingIndicatorActions`.
- [SafeStep](../../errors/SafeStep.md) — often wraps navigation + loader-wait together, as in the top-level docs example.

## Usage example

```java
modulars.browserActions.navigateToUrl(EnvLoader.getUrl(), "loadThePage");
modulars.loadingIndicatorActions.waitForPageLoader("loadThePage");

// A custom spinner:
modulars.loadingIndicatorActions.waitForSpinner(By.cssSelector(".table-spinner"), "table spinner", "loadOrdersTable");

// A spinner that appears briefly then disappears, tolerating it not appearing at all:
modulars.loadingIndicatorActions.waitForToAppearThenDisappear(
        saveSpinnerLocator, Duration.ofSeconds(2), "save spinner", "saveDraft");

// Several loaders on one page:
modulars.loadingIndicatorActions.waitForAllToDisappear("loadDashboard",
        List.of("chart spinner", "table spinner"),
        chartSpinnerLocator, tableSpinnerLocator);
```

## Constructors

| Constructor | Description |
|---|---|
| `LoadingIndicatorActions(WebDriver)` | Default timeout of 50 seconds. |
| `LoadingIndicatorActions(WebDriver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `waitForPageLoader(methodName)` | Waits for the default page loader to disappear. |
| `waitForSpinner(methodName)` | Waits for the default spinner to disappear. |
| `waitForSpinner(locator, elementName, methodName)` | Waits for a custom spinner to disappear. |
| `waitForSkeleton(methodName)` | Waits for the default skeleton placeholder to disappear. |
| `waitForSkeleton(locator, elementName, methodName)` | Waits for a custom skeleton to disappear. |
| `waitForToDisappear(locator, elementName, methodName)` | Waits for any indicator to disappear. |
| `waitForToAppearThenDisappear(locator, appearanceTimeout, elementName, methodName)` | Waits for it to appear first, then disappear; skips the disappearance wait if it never appears. |
| `waitForAllToDisappear(methodName, elementNames, By...)` | Waits for several indicators to disappear in turn. |
| `isLoading(locator, elementName, methodName)` | Whether an indicator is visible right now. |

All disappearance waits throw
[LoadingIndicatorException](LoadingIndicatorException.md) if the
indicator is still visible once the timeout runs out.

[Back to loading index](README.md) · [Docs home](../../README.md)
