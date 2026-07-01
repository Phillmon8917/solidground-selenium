# InvalidCalendarConfigurationException

Package: `io.github.phillmon.selenium.modulars.calendar` (extends `IllegalStateException`)

Thrown when a [CalendarLocators](CalendarLocators.md)`.Builder` is used to
build locators without setting all the required parts (next button,
previous button, caption, or day locator).

## Related classes

- [CalendarLocators](CalendarLocators.md) — the only class that throws this, from `build()`.

## Usage example

```java
CalendarLocators.builder()
        .withNextButton(By.cssSelector(".next"))
        // .withPreviousButton(...) missing!
        .withCaption(By.cssSelector(".caption"))
        .withDayLocator(date -> By.cssSelector("[data-day='" + date + "']"))
        .build(); // throws InvalidCalendarConfigurationException
```

## Constructor

| Constructor | Description |
|---|---|
| `InvalidCalendarConfigurationException(String message)` | Describes which part of the configuration is missing. |

[Back to calendar index](README.md) · [Docs home](../../README.md)
