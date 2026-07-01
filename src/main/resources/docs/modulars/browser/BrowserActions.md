# BrowserActions

Package: `io.github.phillmon.selenium.modulars.browser`

Actions that work on the whole browser rather than a single element:
navigating pages, reading the current url and title, switching between
windows, tabs, and iframes, taking screenshots, and waiting for the page
to finish loading.

## Related classes

- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.browserActions`.
- [LoadingIndicatorActions](../loading/LoadingIndicatorActions.md) — used together after navigating, to wait for spinners to clear.
- [SafeStep](../../errors/SafeStep.md) — takes a screenshot via the same `TakesScreenshot` mechanism when a step fails.
- [EnvLoader](../../utils/env/EnvLoader.md) — typically supplies the url passed to `navigateToUrl`.

## Usage example

```java
modulars.browserActions.navigateToUrl(EnvLoader.getUrl(), "loadThePage");
modulars.browserActions.waitForDocumentReady(Duration.ofSeconds(20));

String title = modulars.browserActions.getPageTitle();

modulars.browserActions.openNewTab(Duration.ofSeconds(5), "openHelpCenter");
modulars.browserActions.switchToWindowByIndex(1, "openHelpCenter");
modulars.browserActions.closeCurrentTab("closeHelpCenter");
modulars.browserActions.switchToWindowByIndex(0, "closeHelpCenter");

modulars.browserActions.takeScreenshot("build/screenshots/dashboard.png", "captureDashboard");
```

## Constructor

| Constructor | Description |
|---|---|
| `BrowserActions(WebDriver driver)` | Creates the browser actions for the given driver. |

## Methods

| Method | Description |
|---|---|
| `navigateToUrl(url, methodName)` | Navigates to a url. |
| `refreshPage(methodName)` | Refreshes the current page. |
| `navigateBack(methodName)` | Goes back one page in history. |
| `navigateForward(methodName)` | Goes forward one page in history. |
| `getCurrentUrl()` | Returns the current page's url. |
| `getPageTitle()` | Returns the current page's title. |
| `waitForDocumentReady(Duration)` | Waits until `document.readyState` is `"complete"`. |
| `getCurrentWindowHandle(methodName)` | Returns the active window's handle. |
| `getAllWindowHandles(methodName)` | Returns every open window/tab handle. |
| `switchToWindow(handle, methodName)` | Switches focus to a window by handle. |
| `switchToWindowByIndex(index, methodName)` | Switches focus to a window by position. Throws `IndexOutOfBoundsException` if out of range. |
| `openNewTab(Duration, methodName)` | Opens and switches to a new, empty tab. |
| `closeCurrentTab(methodName)` | Closes the focused tab/window. |
| `switchToFrame(frameNameOrId, elementName, methodName)` | Switches into an iframe by name/id. |
| `switchToDefaultContent(methodName)` | Switches back out of any iframe. |
| `takeScreenshot(methodName)` | Returns a screenshot as raw PNG bytes. |
| `takeScreenshot(filePath, methodName)` | Saves a screenshot to disk. Throws `RuntimeException` if saving fails. |
| `maximizeWindow(methodName)` | Maximizes the browser window. |

[Back to browser index](README.md) · [Docs home](../../README.md)
