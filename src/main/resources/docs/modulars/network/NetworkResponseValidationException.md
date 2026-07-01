# NetworkResponseValidationException

Package: `io.github.phillmon.selenium.modulars.network`

Thrown when [NetworkResponseValidator](NetworkResponseValidator.md) can't
confirm that an expected network response arrived in time, or when a
response arrives but doesn't match the expected status code or body
content.

## Related classes

- [NetworkResponseValidator](NetworkResponseValidator.md) — the only class that throws this.
- [ResponseExpectation](ResponseExpectation.md) — the description the response was checked against.

## Constructors

| Constructor | Description |
|---|---|
| `NetworkResponseValidationException(String message)` | No underlying cause. |
| `NetworkResponseValidationException(String message, Throwable cause)` | Wraps the original Selenium timeout. |

[Back to network index](README.md) · [Docs home](../../README.md)
