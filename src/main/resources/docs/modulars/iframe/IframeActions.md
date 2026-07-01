# IframeActions

Package: `io.github.phillmon.selenium.modulars.iframe`

Actions for switching the browser's focus into and out of iframes. Keeps
track of how many frames deep the browser is, so it can switch back to
the parent frame or refuse to when already at the top-level page.

## Related classes

- [IframeActionException](IframeActionException.md) — thrown when a frame switch fails or times out.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.iframeActions`.
- [ElementActions](../element/ElementActions.md) — typically used for the actual clicking/typing once inside the frame.

## Usage example

```java
modulars.iframeActions.switchToFrame(paymentFrameLocator, "payment iframe", "enterCardDetails");
modulars.elementActions.typeText(cardNumberField, "4111111111111111", "card number field", "enterCardDetails");
modulars.iframeActions.switchToDefaultContent("enterCardDetails");
```

Running an action inside a frame and automatically switching back out
afterwards, even if it throws:

```java
String balance = modulars.iframeActions.doInFrame(
        accountSummaryFrame, "account summary iframe",
        () -> modulars.elementActions.getText(balanceLocator, "balance", "readBalance"),
        "readBalance");
```

Nested frames, one level at a time:

```java
modulars.iframeActions.switchToNestedFrame("openChatWidget",
        List.of("chat wrapper iframe", "chat widget iframe"),
        chatWrapperFrame, chatWidgetFrame);
```

## Constructors

| Constructor | Description |
|---|---|
| `IframeActions(WebDriver)` | Default timeout of 10 seconds. |
| `IframeActions(WebDriver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `switchToFrame(By locator, elementName, methodName)` | Switches into a frame found by locator. |
| `switchToFrame(String nameOrId, methodName)` | Switches into a frame by name/id. |
| `switchToFrame(int index, methodName)` | Switches into a frame by position. |
| `switchToNestedFrame(methodName, elementNames, By...)` | Switches through several frames in order. |
| `switchToParentFrame(methodName)` | Switches up to the containing frame. |
| `switchToDefaultContent(methodName)` | Switches all the way back to the main page. |
| `<T> T doInFrame(locator, elementName, Supplier<T>, methodName)` | Runs an action inside a frame and switches back out afterwards, returning a value. |
| `doInFrame(locator, elementName, Runnable, methodName)` | Same, for an action with no return value. |
| `isInsideFrame()` | Whether the browser is currently inside any frame. |
| `getCurrentFrameDepth()` | How many frames deep the browser is focused. |
| `isFramePresent(locator, elementName, methodName)` | Whether a frame matching a locator exists right now. |
| `countFrames(methodName)` | Total `<iframe>` and `<frame>` elements on the page. |

All frame-switching methods throw
[IframeActionException](IframeActionException.md) if the frame doesn't
become available in time, or if `switchToParentFrame` is called while
already at the top-level document.

[Back to iframe index](README.md) · [Docs home](../../README.md)
