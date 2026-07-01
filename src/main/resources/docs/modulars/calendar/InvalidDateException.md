# InvalidDateException

Package: `io.github.phillmon.selenium.modulars.calendar` (extends `IllegalArgumentException`)

Thrown when [DateObject](DateObject.md) is given a date value it can't
accept: unparseable text, a year outside the supported range (1940 to
2080), or a day/month/year combination that isn't a real date.

## Related classes

- [DateObject](DateObject.md) — the only class that throws this.

## Usage example

```java
try {
    DateObject.fromString("not a date");
} catch (InvalidDateException e) {
    LoggerUtil.warning("Test data has an unparseable date: " + e.getMessage());
}
```

## Constructors

| Constructor | Description |
|---|---|
| `InvalidDateException(String message)` | No underlying cause. |
| `InvalidDateException(String message, Throwable cause)` | Wraps the original parse failure. |

[Back to calendar index](README.md) · [Docs home](../../README.md)
