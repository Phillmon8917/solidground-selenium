# CalendarActions

Package: `io.github.phillmon.selenium.modulars.calendar`

Drives a date-picker calendar widget: reads which month is currently
shown, clicks the next/previous month buttons until the right month is
reached, and clicks the day matching the requested date.

## Related classes

- [CalendarLocators](CalendarLocators.md) — describes the widget's next/previous buttons, caption, and day locator.
- [DateObject](DateObject.md) — the date type this class navigates to.
- [DateOffset](DateOffset.md) — a convenient way to build a relative `DateObject` for `selectDate(int, ...)`.
- [CalendarNavigationException](CalendarNavigationException.md) — thrown when navigation or parsing fails.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.calendarActions`.

## Usage example

```java
DateObject checkIn = DateObject.builder().withDay(15).withMonth("January").withYear(2027).build();
modulars.calendarActions.selectDate(checkIn, "check-in calendar", "selectCheckInDate");

// Or relative to today:
modulars.calendarActions.selectDate(7, "check-in calendar", "selectCheckInDateInAWeek");
```

Using a custom widget layout instead of the default day picker:

```java
CalendarLocators locators = CalendarLocators.builder()
        .withNextButton(By.cssSelector(".cal-next"))
        .withPreviousButton(By.cssSelector(".cal-prev"))
        .withCaption(By.cssSelector(".cal-caption"))
        .withDayLocator(date -> By.cssSelector("[data-date='" + date.toLocalDate() + "']"))
        .build();

CalendarActions calendar = new CalendarActions(driver, locators);
calendar.selectDate(DateOffset.today(), "booking calendar", "selectToday");
```

## Constructors

| Constructor | Description |
|---|---|
| `CalendarActions(WebDriver, CalendarLocators)` | Uses a default timeout of 30 seconds. |
| `CalendarActions(WebDriver, CalendarLocators, Duration timeout)` | Uses a custom timeout. |

## Methods

| Method | Description |
|---|---|
| `selectDate(DateObject, elementName, methodName)` | Navigates to the target month and clicks the day. Throws [CalendarNavigationException](CalendarNavigationException.md) if the month can't be reached or the caption can't be parsed. |
| `selectDate(int offsetDays, elementName, methodName)` | Selects the date `offsetDays` from today (see [DateOffset](DateOffset.md)). |

[Back to calendar index](README.md) · [Docs home](../../README.md)
