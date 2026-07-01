# TimeoutUtil

Package: `io.github.phillmon.selenium.utils.timeout`

Adjusts timeouts so tests wait longer when running on CI. Whether the
tests are running on CI is decided once, by checking whether a `CI`
environment variable is set and non-empty. Every wait timeout used
throughout the framework passes through one of these methods so CI runs
get the extra buffer they typically need.

## Related classes

- [CiMultiplier](CiMultiplier.md) — the multiplier options accepted by the overloads that take one.
- Every `modulars.*Actions` constructor that accepts a `Duration timeout` — passes it through `TimeoutUtil.adjust` before storing it.

## Usage example

```java
Duration timeout = TimeoutUtil.adjust(Duration.ofSeconds(15)); // 30s on CI, 15s locally

ElementActions actions = new ElementActions(driver, TimeoutUtil.adjust(Duration.ofSeconds(20), CiMultiplier.TRIPLE));
```

Using the millisecond/second/minute helpers directly:

```java
long timeoutMs = TimeoutUtil.ofSeconds(10); // 20000 on CI, 10000 locally
```

## Methods

| Method | Description |
|---|---|
| `static long ofMillis(long)` | Milliseconds, doubled on CI. |
| `static long ofMillis(long, CiMultiplier)` | Milliseconds, multiplied by a custom factor on CI. |
| `static long ofSeconds(long)` | Seconds converted to milliseconds, doubled on CI. |
| `static long ofSeconds(long, CiMultiplier)` | Same, with a custom factor. |
| `static long ofMinutes(long)` | Minutes converted to milliseconds, doubled on CI. |
| `static long ofMinutes(long, CiMultiplier)` | Same, with a custom factor. |
| `static Duration adjust(Duration)` | Doubles a `Duration` on CI. Returns null if given null. |
| `static Duration adjust(Duration, CiMultiplier)` | Same, with a custom factor. |

[Back to timeout index](README.md) · [Docs home](../../README.md)
