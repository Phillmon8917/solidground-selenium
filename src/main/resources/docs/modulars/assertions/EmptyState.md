# EmptyState

Package: `io.github.phillmon.selenium.modulars.assertions` (enum: `EMPTY`, `NOT_EMPTY`)

Describes whether a string is expected to be empty or not empty. Used
with [AssertionActions](AssertionActions.md)`.assertEmptyState`.

## Related classes

- [AssertionActions](AssertionActions.md) — the class that consumes this enum.

## Usage example

```java
String validationMessage = modulars.elementActions.getText(validationLocator, "validation message", "submitEmptyForm");
modulars.assertionActions.assertEmptyState(validationMessage, EmptyState.NOT_EMPTY, "submitEmptyForm");
```

[Back to assertions index](README.md) · [Docs home](../../README.md)
