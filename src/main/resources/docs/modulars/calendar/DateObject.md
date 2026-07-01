# DateObject

Package: `io.github.phillmon.selenium.modulars.calendar`

Represents a single calendar date used across the calendar actions. Wraps
a `LocalDate` but only allows years between 1940 and 2080, and offers
several ways to create one: from a `LocalDate`, from a formatted string,
or built up field by field.

## Related classes

- [CalendarActions](CalendarActions.md) — the type it navigates the calendar widget to.
- [DateFormat](DateFormat.md) — preset patterns usable with `fromString` and `format`.
- [DateOffset](DateOffset.md) — builds a `DateObject` relative to today.
- [InvalidDateException](InvalidDateException.md) — thrown for any invalid date, text, or range.
- [SmartWaitActions](../wait/SmartWaitActions.md) — `waitForDateInputToStabilize` returns a `DateObject`.

## Usage example

```java
DateObject fromText = DateObject.fromString("January 15, 2027");
DateObject fromIso = DateObject.fromString("2027-01-15", DateFormat.ISO);
DateObject builtUp = DateObject.builder().withDay(15).withMonth("January").withYear(2027).build();

String display = builtUp.format(DateFormat.LONG); // "January 15, 2027"
```

## Static factories

| Method | Description |
|---|---|
| `static Builder builder()` | For building a date field by field. |
| `static DateObject fromLocalDate(LocalDate)` | Wraps an existing `LocalDate`. |
| `static DateObject fromString(String)` | Parses using the default `"MMMM d, yyyy"` pattern. |
| `static DateObject fromString(String, String pattern)` | Parses using a custom pattern. |
| `static DateObject fromString(String, DateFormat)` | Parses using a [DateFormat](DateFormat.md) preset. |
| `static DateObject fromString(String, DateTimeFormatter)` | Parses using an already-built formatter. |

## Instance methods

| Method | Description |
|---|---|
| `getDay()` / `getMonth()` / `getMonthValue()` / `getYear()` | Read individual fields. |
| `toLocalDate()` | Returns the plain `java.time.LocalDate`. |
| `format(String pattern)` | Formats using a custom pattern. |
| `format(DateFormat)` | Formats using a [DateFormat](DateFormat.md) preset. |

All of the above throw [InvalidDateException](InvalidDateException.md) on
bad input (blank text, unparseable text, an out-of-range year, or an
impossible day/month/year combination).

[Back to calendar index](README.md) · [Docs home](../../README.md)
