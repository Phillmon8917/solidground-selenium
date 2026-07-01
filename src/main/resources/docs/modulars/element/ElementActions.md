# ElementActions

Package: `io.github.phillmon.selenium.modulars.element`

The main set of actions for working with individual page elements:
clicking, typing, reading text and attributes, checking element state,
scrolling, and waiting for elements to appear, disappear, or show
expected text. Every locator-based action waits for the element to be in
the right state first.

## Related classes

- [ElementActionException](ElementActionException.md) — thrown when a wait times out.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.elementActions`.
- [SafeStep](../../errors/SafeStep.md) — usually wraps a sequence of `ElementActions` calls inside a page-object method.
- [MouseActions](../mouse/MouseActions.md) — for gestures beyond a plain click (hover, drag and drop).
- [KeyboardActions](../keyboard/KeyboardActions.md) — for keyboard shortcuts on an element.

## Usage example

```java
modulars.elementActions.click(loginButton, "login button", "loginAsAdmin");
modulars.elementActions.typeText(usernameField, EnvLoader.getAdminUsername(), "username field", "loginAsAdmin");
modulars.elementActions.typeText(passwordField, EnvLoader.getAdminPassword(), true, "password field", "loginAsAdmin");

String errorText = modulars.elementActions.getText(errorBanner, "error banner", "verifyLoginError");
boolean isDisabled = !modulars.elementActions.isEnabled(submitButton, "submit button", "verifyFormState");

modulars.elementActions.scrollIntoView(footerLink, "footer link", "clickFooterLink");
modulars.elementActions.click(footerLink, "footer link", "clickFooterLink");

modulars.elementActions.waitForTextToBePresent(statusBanner, "Saved", "status banner", "verifySaved");
```

A click that falls back to JavaScript if something is visually
overlapping the element:

```java
modulars.elementActions.clickWithJsFallback(cookieBannerCloseButton, "cookie banner close button", "dismissCookieBanner");
```

## Constructors

| Constructor | Description |
|---|---|
| `ElementActions(WebDriver)` | Default timeout of 30 seconds. |
| `ElementActions(WebDriver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `click(locator, elementName, methodName)` | Waits for clickable, clicks normally. |
| `jsClick(locator, elementName, methodName)` | Clicks via JavaScript, bypassing overlaps. |
| `clickWithJsFallback(locator, elementName, methodName)` | Normal click, falls back to `jsClick` if intercepted. |
| `typeText(locator, text, elementName, methodName)` | Clears and types text. |
| `typeText(locator, text, sensitive, elementName, methodName)` | Same, with a sensitive-value log mask option. |
| `typeSequentially(locator, text, elementName, methodName)` | Types one character at a time (default 80ms delay). |
| `typeSequentially(locator, text, sensitive, elementName, methodName)` | Same, with sensitive masking. |
| `typeSequentially(locator, text, delayBetweenKeys, elementName, methodName)` | Same, with a custom delay. |
| `typeSequentially(locator, text, delayBetweenKeys, sensitive, elementName, methodName)` | Full control over delay and masking. |
| `appendText(locator, text, elementName, methodName)` | Types without clearing first. |
| `appendText(locator, text, sensitive, elementName, methodName)` | Same, with sensitive masking. |
| `clearText(locator, elementName, methodName)` | Clears an input via `WebElement.clear()`. |
| `clearTextWithKeys(locator, elementName, methodName)` | Clears via Ctrl+A then Delete. |
| `submit(locator, elementName, methodName)` | Submits the containing form. |
| `uploadFile(locator, filePath, elementName, methodName)` | Uploads a file through a file input. |
| `getText(locator, elementName, methodName)` | Visible text of an element. |
| `getAttribute(locator, attribute, elementName, methodName)` | An HTML attribute's value. |
| `getCssValue(locator, property, elementName, methodName)` | A computed CSS property's value. |
| `getAllText(locator, elementName, methodName)` | Visible text of every matching element. |
| `countElements(locator, elementName, methodName)` | Count of elements matching a locator right now. |
| `isDisplayed(locator, elementName, methodName)` | Whether an element is displayed (false if absent). |
| `isEnabled(locator, elementName, methodName)` | Whether an element is enabled. |
| `isSelected(locator, elementName, methodName)` | Whether an element (checkbox/radio) is selected. |
| `isPresent(locator, elementName, methodName)` | Whether an element exists right now. |
| `scrollIntoView(locator, elementName, methodName)` | Scrolls the element to the centre of the viewport. |
| `scrollBy(deltaX, deltaY, methodName)` | Scrolls the whole window by an amount. |
| `waitForInvisibility(locator, elementName, methodName)` | Waits for an element to disappear. |
| `waitForTextToBePresent(locator, expectedText, elementName, methodName)` | Waits for text to appear in an element. |

All wait-based methods throw
[ElementActionException](ElementActionException.md) if the expected state
isn't reached within the configured timeout.

[Back to element index](README.md) · [Docs home](../../README.md)
