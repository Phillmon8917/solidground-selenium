# DateOffset

Package: `io.github.phillmon.selenium.modulars.calendar`

Small helper for getting a [DateObject](DateObject.md) relative to
today's date, so tests can select dates like "today" or "seven days from
now" without building a `LocalDate` by hand.

## Related classes

- [DateObject](DateObject.md) — the type this class returns.
- [CalendarActions](CalendarActions.md) — `selectDate(int offsetDays, ...)` uses `DateOffset.resolve` internally.

## Usage example

```java
DateObject today = DateOffset.today();
DateObject nextWeek = DateOffset.resolve(7);
DateObject lastWeek = DateOffset.resolve(-7);
```

## Methods

| Method | Description |
|---|---|
| `static DateObject resolve(int offsetDays)` | The date `offsetDays` days from today (negative for the past). |
| `static DateObject today()` | Today's date. |

[Back to calendar index](README.md) · [Docs home](../../README.md)
