# CalendarNavigationException

Package: `io.github.phillmon.selenium.modulars.calendar`

Thrown when [CalendarActions](CalendarActions.md) can't navigate the
calendar to the requested month, can't read the currently displayed
month, or can't find an element it needs within the expected time.

## Related classes

- [CalendarActions](CalendarActions.md) — the only class that throws this.

## Usage example

```java
try {
    modulars.calendarActions.selectDate(checkIn, "check-in calendar", "selectCheckInDate");
} catch (CalendarNavigationException e) {
    LoggerUtil.error("Calendar widget may have changed layout", e);
    throw e;
}
```

## Constructors

| Constructor | Description |
|---|---|
| `CalendarNavigationException(String message)` | No underlying cause. |
| `CalendarNavigationException(String message, Throwable cause)` | Wraps the original exception (usually a Selenium timeout or a date parse failure). |

[Back to calendar index](README.md) · [Docs home](../../README.md)
