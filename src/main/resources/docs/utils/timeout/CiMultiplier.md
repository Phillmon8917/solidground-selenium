# CiMultiplier

Package: `io.github.phillmon.selenium.utils.timeout` (enum: `SINGLE`, `DOUBLE`, `TRIPLE`)

How much a base timeout should be multiplied by when the tests are
running on CI, since CI environments are often slower or more loaded than
a local machine and need extra time before something is considered timed
out.

## Related classes

- [TimeoutUtil](TimeoutUtil.md) — every overload that takes a `CiMultiplier` uses this enum.

## Usage example

```java
Duration generousTimeout = TimeoutUtil.adjust(Duration.ofSeconds(10), CiMultiplier.TRIPLE);
```

## Values

| Value | Factor |
|---|---|
| `SINGLE` | 1 (no change on CI) |
| `DOUBLE` | 2 (the default used by most `TimeoutUtil` overloads) |
| `TRIPLE` | 3 |

## Method

| Method | Description |
|---|---|
| `getFactor()` | The number a base timeout should be multiplied by. |

[Back to timeout index](README.md) · [Docs home](../../README.md)
