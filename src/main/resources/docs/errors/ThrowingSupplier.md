# ThrowingSupplier

Package: `io.github.phillmon.selenium.errors` (functional interface)

Describes a step that returns a value and is allowed to throw any
exception — the same idea as [ThrowingRunnable](ThrowingRunnable.md), but
for steps that need to hand a result back to the caller.

## Related classes

- [SafeStep](SafeStep.md) — accepts this as the step shape for its value-returning `run` overloads.
- [ThrowingRunnable](ThrowingRunnable.md) — the equivalent shape for a step with no return value.

## Usage example

```java
ThrowingSupplier<String> readTitle = () -> modulars.browserActions.getPageTitle();
String title = SafeStep.run(driver, "readPageTitle", readTitle);
```

Usually written inline:

```java
String title = SafeStep.run(driver, "readPageTitle", () -> modulars.browserActions.getPageTitle());
```

## Method

| Method | Description |
|---|---|
| `T get() throws Exception` | Runs the step and returns its result. Any exception is caught and handled by whichever `SafeStep` method is running it. |

[Back to errors index](README.md) · [Docs home](../README.md)
