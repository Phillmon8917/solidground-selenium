# PageModularOptions

Package: `io.github.phillmon.selenium.base`

Holds the settings that control how the action groups inside
[ActionsContainer](ActionsContainer.md) behave: which locators the
calendar uses, how a toggle's on/off state is read, which `SoftAssert` to
share, and where downloaded documents get saved. Build one with
`Builder`, or call `defaults()` for a plain configuration.

## Related classes

- [ActionsContainer](ActionsContainer.md) / [BasePage](BasePage.md) — take a `PageModularOptions` to customise their action groups.
- [CalendarLocators](../modulars/calendar/CalendarLocators.md) — the type returned by `getCalendarLocators()`.
- [ToggleStateResolver](../modulars/toggle/ToggleStateResolver.md) — the type returned by `getToggleStateResolver()`.
- [DocumentDownloader](../modulars/reader/DocumentDownloader.md) — created internally by `createDocumentDownloader`.

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

## Static factory methods

| Method | Description |
|---|---|
| `static PageModularOptions defaults()` | Returns a configuration with every setting at its default value. |
| `static Builder builder()` | Returns a new `Builder` for setting only the options that need to change. |

## Builder methods

| Method | Description |
|---|---|
| `Builder withCalendarLocators(CalendarLocators)` | Sets custom [CalendarLocators](../modulars/calendar/CalendarLocators.md). Throws `IllegalArgumentException` if null. |
| `Builder withToggleStateResolver(ToggleStateResolver)` | Sets a custom [ToggleStateResolver](../modulars/toggle/ToggleStateResolver.md). Throws `IllegalArgumentException` if null. |
| `Builder withSoftAssert(SoftAssert)` | Sets a shared TestNG `SoftAssert` instance. Throws `IllegalArgumentException` if null. |
| `Builder withDownloadDirectory(Path)` | Sets where downloaded documents are saved. Throws `IllegalArgumentException` if null. |
| `Builder withDownloadTimeout(Duration)` | Sets how long the reader actions wait for a download. Throws `IllegalArgumentException` if null. |
| `PageModularOptions build()` | Creates the final, immutable options object. |

[Back to base index](README.md) · [Docs home](../README.md)
