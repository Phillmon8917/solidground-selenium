# DateFormat

Package: `io.github.phillmon.selenium.modulars.calendar`
(enum: `LONG`, `SHORT_SLASH`, `SHORT_DASH`, `ISO`, `DAY_MONTH_YEAR`, `ABBREVIATED`)

A fixed set of common date patterns [DateObject](DateObject.md) can
format to and parse from, so callers can pick a well known format by name
instead of writing out a pattern string each time.

## Related classes

- [DateObject](DateObject.md) — consumes this enum in `fromString` and `format`.
- [SmartWaitActions](../wait/SmartWaitActions.md) — `waitForDateInputToStabilize` takes one of these to parse a stabilized input value.

## Usage example

```java
DateObject date = DateObject.fromString("2027-01-15", DateFormat.ISO);
String display = date.format(DateFormat.DAY_MONTH_YEAR); // "15 January 2027"
```

## Values and patterns

| Value | Pattern | Example |
|---|---|---|
| `LONG` | `MMMM d, yyyy` | January 15, 2027 |
| `SHORT_SLASH` | `MM/dd/yyyy` | 01/15/2027 |
| `SHORT_DASH` | `MM-dd-yyyy` | 01-15-2027 |
| `ISO` | `yyyy-MM-dd` | 2027-01-15 |
| `DAY_MONTH_YEAR` | `dd MMMM yyyy` | 15 January 2027 |
| `ABBREVIATED` | `MMM d, yyyy` | Jan 15, 2027 |

[Back to calendar index](README.md) · [Docs home](../../README.md)
