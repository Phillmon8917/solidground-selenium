# NullState

Package: `io.github.phillmon.selenium.modulars.assertions` (enum: `NULL`, `NOT_NULL`)

Describes whether a value is expected to be null or not null. Used with
[AssertionActions](AssertionActions.md)`.assertNullState` and
`.softAssertNullState`.

## Related classes

- [AssertionActions](AssertionActions.md) — the class that consumes this enum.

## Usage example

```java
String errorBanner = modulars.elementActions.isPresent(errorBannerLocator, "error banner", "verifyNoErrors")
        ? modulars.elementActions.getText(errorBannerLocator, "error banner", "verifyNoErrors")
        : null;
modulars.assertionActions.assertNullState(errorBanner, NullState.NULL, "verifyNoErrors");
```

[Back to assertions index](README.md) · [Docs home](../../README.md)
