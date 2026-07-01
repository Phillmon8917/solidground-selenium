# ExpectedState

Package: `io.github.phillmon.selenium.modulars.assertions` (enum: `TRUE`, `FALSE`)

Describes whether a boolean condition is expected to be true or false.
Used with [AssertionActions](AssertionActions.md)`.assertConditionState`
and `.softAssertConditionState`.

## Related classes

- [AssertionActions](AssertionActions.md) — the class that consumes this enum.

## Usage example

```java
boolean isCheckboxTicked = modulars.elementActions.isSelected(agreeCheckbox, "agree checkbox", "acceptTerms");
modulars.assertionActions.assertConditionState(isCheckboxTicked, ExpectedState.TRUE, "acceptTerms");
```

[Back to assertions index](README.md) · [Docs home](../../README.md)
