# PageModularOptions

Package: `io.github.phillmon.selenium.base`

Holds the settings that control how the action groups inside
[ActionsContainer](ActionsContainer.md) behave: which locators the
calendar uses, how a toggle's on/off state is read, which `SoftAssert` to
share, where downloaded documents get saved, and optional timeout overrides
for several action groups. Build one with `Builder`, or call `defaults()` for a
plain configuration.

## Related classes

- [ActionsContainer](ActionsContainer.md) / [BasePage](BasePage.md) — take a `PageModularOptions` to customise their action groups.
- [CalendarLocators](../modulars/calendar/CalendarLocators.md) — the type returned by `getCalendarLocators()`.
- [ToggleStateResolver](../modulars/toggle/ToggleStateResolver.md) — the type returned by `getToggleStateResolver()`.
- [DocumentDownloader](../modulars/reader/DocumentDownloader.md) — created internally by `createDocumentDownloader`.
- [AssertionActions](../modulars/assertions/AssertionActions.md) - uses the configured `SoftAssert`, or the current thread's default shared one.

## Usage example

```java
PageModularOptions options = PageModularOptions.builder()
        .withCalendarLocators(CalendarLocators.builder()
                .withNextButton(By.cssSelector(".cal-next"))
                .withPreviousButton(By.cssSelector(".cal-prev"))
                .withCaption(By.cssSelector(".cal-caption"))
                .withDayLocator(date -> By.cssSelector("[data-date='" + date.toLocalDate() + "']"))
                .build())
        .withDownloadDirectory(Path.of("build/downloads"))
        .withDownloadTimeout(Duration.ofSeconds(60))
        .withElementTimeout(Duration.ofSeconds(20))
        .withWaitTimeout(Duration.ofSeconds(15))
        .build();

public class BookingPage extends BasePage {
    public BookingPage(WebDriver driver) {
        super(driver, options);
    }
}
```

If nothing needs to be customised, just use the default:

```java
PageModularOptions defaults = PageModularOptions.defaults();
```

When using the default shared `SoftAssert`, reset it at the start of each test
method and assert it at the end:

```java
@BeforeMethod
public void resetSoftAssertions() {
    PageModularOptions.resetCurrentThreadSoftAssert();
}

@AfterMethod
public void assertAllSoftAssertions() {
    PageModularOptions.currentThreadSoftAssert().assertAll();
}
```

## Static factory methods

| Method | Description |
|---|---|
| `static PageModularOptions defaults()` | Returns a configuration with every setting at its default value. |
| `static Builder builder()` | Returns a new `Builder` for setting only the options that need to change. |
| `static SoftAssert currentThreadSoftAssert()` | Returns the default `SoftAssert` shared by page objects on the current thread. |
| `static void resetCurrentThreadSoftAssert()` | Replaces the current thread's default shared `SoftAssert` with a new one. |

## Builder methods

| Method | Description |
|---|---|
| `Builder withCalendarLocators(CalendarLocators)` | Sets custom [CalendarLocators](../modulars/calendar/CalendarLocators.md). Throws `IllegalArgumentException` if null. |
| `Builder withToggleStateResolver(ToggleStateResolver)` | Sets a custom [ToggleStateResolver](../modulars/toggle/ToggleStateResolver.md). Throws `IllegalArgumentException` if null. |
| `Builder withSoftAssert(SoftAssert)` | Sets a shared TestNG `SoftAssert` instance. Throws `IllegalArgumentException` if null. |
| `Builder withDownloadDirectory(Path)` | Sets where downloaded documents are saved. Throws `IllegalArgumentException` if null. |
| `Builder withDownloadTimeout(Duration)` | Sets how long the reader actions wait for a download. Throws `IllegalArgumentException` if null. |
| `Builder withElementTimeout(Duration)` | Sets the timeout used by `ElementActions`. Throws `IllegalArgumentException` if null. |
| `Builder withIframeTimeout(Duration)` | Sets the timeout used by `IframeActions`. Throws `IllegalArgumentException` if null. |
| `Builder withMouseTimeout(Duration)` | Sets the timeout used by `MouseActions`. Throws `IllegalArgumentException` if null. |
| `Builder withKeyboardTimeout(Duration)` | Sets the timeout used by `KeyboardActions`. Throws `IllegalArgumentException` if null. |
| `Builder withDropdownTimeout(Duration)` | Sets the timeout used by `SelectDropdownActions`. Throws `IllegalArgumentException` if null. |
| `Builder withWaitTimeout(Duration)` | Sets the timeout used by `SmartWaitActions`. Throws `IllegalArgumentException` if null. |
| `PageModularOptions build()` | Creates the final, immutable options object. |

[Back to base index](README.md) · [Docs home](../README.md)
