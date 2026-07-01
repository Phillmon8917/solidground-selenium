# CalendarLocators

Package: `io.github.phillmon.selenium.modulars.calendar`

Describes where to find the parts of a calendar widget: the next/previous
month buttons, the caption showing the current month and year, and a way
to build a locator for any given day. Build one with `Builder`, or use
`defaultDayPicker()` for a common layout.

## Related classes

- [CalendarActions](CalendarActions.md) — uses this to navigate and click the right day.
- [DateObject](DateObject.md) — passed into the day-locator function.
- [InvalidCalendarConfigurationException](InvalidCalendarConfigurationException.md) — thrown by `build()` if a required locator is missing.
- [PageModularOptions](../../base/PageModularOptions.md) — `withCalendarLocators` accepts an instance of this.

## Usage example

```java
CalendarLocators locators = CalendarLocators.builder()
        .withNextButton(By.xpath("//button[@aria-label='Next Month']"))
        .withPreviousButton(By.xpath("//button[@aria-label='Previous Month']"))
        .withCaption(By.cssSelector(".cal-caption"))
        .withCaptionFormat("MMMM yyyy")
        .withDayLocator(date -> By.cssSelector("button[data-day='" + date.toLocalDate() + "']"))
        .build();
```

Using the ready-made default instead:

```java
CalendarLocators locators = CalendarLocators.defaultDayPicker();
```

## Constructors / factories

| Method | Description |
|---|---|
| `static Builder builder()` | Returns a new `Builder`. |
| `static CalendarLocators defaultDayPicker()` | A ready-made configuration for a common day-picker layout. |

## Builder methods

| Method | Description |
|---|---|
| `withNextButton(By)` | Locator for the next-month button. |
| `withPreviousButton(By)` | Locator for the previous-month button. |
| `withCaption(By)` | Locator for the month/year caption. |
| `withCaptionFormat(String pattern)` | Pattern used to parse the caption text (default `"MMMM yyyy"`). |
| `withDayLocator(Function<DateObject, By>)` | Builds the locator for a given day's button. |
| `build()` | Creates the final `CalendarLocators`. Throws [InvalidCalendarConfigurationException](InvalidCalendarConfigurationException.md) if any required locator is missing. |

[Back to calendar index](README.md) · [Docs home](../../README.md)
