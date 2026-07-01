# ThrowingRunnable

Package: `io.github.phillmon.selenium.errors` (functional interface)

Describes a step that doesn't return a value but is allowed to throw any
exception. [SafeStep](SafeStep.md) uses this so it can wrap ordinary
page-object calls, which may throw checked or unchecked exceptions,
without the caller needing its own try/catch.

## Related classes

- [SafeStep](SafeStep.md) — accepts this as the step shape for its void `run` and `runSequence` overloads.
- [ThrowingSupplier](ThrowingSupplier.md) — the equivalent shape for a step that returns a value.

## Usage example

```java
ThrowingRunnable clickSubmit = () -> modulars.elementActions.click(submitButton, "submit button", "submitForm");
SafeStep.run(driver, "submitForm", clickSubmit);
```

Usually written inline as a lambda rather than assigned to a variable:

```java
SafeStep.run(driver, "submitForm", () ->
        modulars.elementActions.click(submitButton, "submit button", "submitForm"));
```

## Method

| Method | Description |
|---|---|
| `void run() throws Exception` | Runs the step. Any exception is caught and handled by whichever `SafeStep` method is running it. |

[Back to errors index](README.md) · [Docs home](../README.md)
