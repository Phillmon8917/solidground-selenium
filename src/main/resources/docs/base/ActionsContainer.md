# ActionsContainer

Package: `io.github.phillmon.selenium.base`

Builds and holds one instance of every action group a page object can use.
[BasePage](BasePage.md) creates one of these per page object and exposes
it as `this.modulars`, so page objects call `modulars.elementActions...`,
`modulars.browserActions...`, and so on, instead of touching Selenium
directly.

## Related classes

- [BasePage](BasePage.md) — creates and owns the `ActionsContainer`.
- [PageModularOptions](PageModularOptions.md) — controls how the action groups inside are configured.
- [ReaderActionsContainer](ReaderActionsContainer.md) — the `reader` field, grouping the document readers.

## Fields (one action group per module)

| Field | Type | Package |
|---|---|---|
| `assertionActions` | `AssertionActions` | [modulars/assertions](../modulars/assertions/AssertionActions.md) |
| `browserActions` | `BrowserActions` | [modulars/browser](../modulars/browser/BrowserActions.md) |
| `calendarActions` | `CalendarActions` | [modulars/calendar](../modulars/calendar/CalendarActions.md) |
| `dropdownActions` | `SelectDropdownActions` | [modulars/dropdown](../modulars/dropdown/SelectDropdownActions.md) |
| `elementActions` | `ElementActions` | [modulars/element](../modulars/element/ElementActions.md) |
| `iframeActions` | `IframeActions` | [modulars/iframe](../modulars/iframe/IframeActions.md) |
| `keyboardActions` | `KeyboardActions` | [modulars/keyboard](../modulars/keyboard/KeyboardActions.md) |
| `loadingIndicatorActions` | `LoadingIndicatorActions` | [modulars/loading](../modulars/loading/LoadingIndicatorActions.md) |
| `mouseActions` | `MouseActions` | [modulars/mouse](../modulars/mouse/MouseActions.md) |
| `networkActions` | `NetworkValidationActions` | [modulars/network](../modulars/network/NetworkValidationActions.md) |
| `reader` | `ReaderActionsContainer` | [ReaderActionsContainer](ReaderActionsContainer.md) |
| `waitActions` | `SmartWaitActions` | [modulars/wait](../modulars/wait/SmartWaitActions.md) |
| `toggleActions` | `ToggleActions` | [modulars/toggle](../modulars/toggle/ToggleActions.md) |

## Usage example

You never construct this directly in a test — [BasePage](BasePage.md)
does it for you. From inside a page object:

```java
public void searchFor(String term) {
    modulars.elementActions.typeText(searchBox, term, "search box", "searchFor");
    modulars.elementActions.pressEnter(searchBox, "search box", "searchFor");
    modulars.loadingIndicatorActions.waitForSpinner("searchFor");
}
```

If you do need to build one directly (for example in a test helper that
isn't a page object), pass the driver and, optionally, custom options:

```java
ActionsContainer actions = new ActionsContainer(driver, PageModularOptions.builder()
        .withSoftAssert(sharedSoftAssert)
        .build());
```

## Constructors

| Constructor | Description |
|---|---|
| `ActionsContainer(WebDriver driver)` | Builds every action group using default options. Throws `IllegalArgumentException` if `driver` is null. |
| `ActionsContainer(WebDriver driver, PageModularOptions options)` | Builds every action group using the given options. Throws `IllegalArgumentException` if `driver` or `options` is null. |

[Back to base index](README.md) · [Docs home](../README.md)
