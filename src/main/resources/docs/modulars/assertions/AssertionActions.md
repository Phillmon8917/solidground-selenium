# AssertionActions

Package: `io.github.phillmon.selenium.modulars.assertions`

Wraps TestNG's `Assert` and `SoftAssert` calls with logging, so every
assertion made in a test also shows up in the execution log. A hard
assert (no "soft" in the name) stops the test immediately on failure. A
soft assert records the failure but lets the test keep running — the
failures only surface once [assertAll()](#methods) is called.

## Related classes

- [ExpectedState](ExpectedState.md) — used by `assertConditionState` / `softAssertConditionState`.
- [NullState](NullState.md) — used by `assertNullState` / `softAssertNullState`.
- [EmptyState](EmptyState.md) — used by `assertEmptyState`.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.assertionActions`.
- [PageModularOptions](../../base/PageModularOptions.md) — lets you share one `SoftAssert` across page objects via `withSoftAssert`.

## Usage example

```java
modulars.assertionActions.assertEqualsTo(actualTitle, "Dashboard", "verifyDashboardLoaded");

// Soft assertions collect failures instead of stopping the test:
modulars.assertionActions.softAssertEqualsTo(actualTotal, expectedTotal, "verifyOrderTotal");
modulars.assertionActions.softAssertContains(pageSource, "Order Confirmed", "verifyOrderTotal");

// Call this once, usually at the end of the test, to surface soft failures:
modulars.assertionActions.assertAll();
```

## Constructor

| Constructor | Description |
|---|---|
| `AssertionActions(SoftAssert softAssert)` | Uses the given `SoftAssert` for every soft assertion made through this instance. |

## Methods

| Method | Description |
|---|---|
| `assertEqualsTo(actual, expected, methodName)` | Hard assert equality. |
| `softAssertEqualsTo(actual, expected, methodName)` | Soft assert equality. |
| `assertNotEqualsTo(actual, expected, methodName)` | Hard assert inequality. |
| `softAssertNotEqualsTo(actual, expected, methodName)` | Soft assert inequality. |
| `assertConditionState(condition, ExpectedState, methodName)` | Hard assert a boolean matches [ExpectedState](ExpectedState.md). |
| `softAssertConditionState(condition, ExpectedState, methodName)` | Soft assert a boolean matches [ExpectedState](ExpectedState.md). |
| `assertNullState(actual, NullState, methodName)` | Hard assert null/not-null via [NullState](NullState.md). |
| `softAssertNullState(actual, NullState, methodName)` | Soft assert null/not-null via [NullState](NullState.md). |
| `assertContains(actual, expectedSubstring, methodName)` | Hard assert a string contains a substring. |
| `softAssertContains(actual, expectedSubstring, methodName)` | Soft assert a string contains a substring. |
| `assertEqualsIgnoreCase(actual, expected, methodName)` | Hard assert equality ignoring case. |
| `softAssertEqualsIgnoreCase(actual, expected, methodName)` | Soft assert equality ignoring case. |
| `failTest(methodName, reason)` | Hard fail immediately with a reason. |
| `softFailTest(methodName, reason)` | Soft fail with a reason. |
| `assertNotContains(actual, unexpectedSubstring, methodName)` | Hard assert a string does not contain a substring. |
| `softAssertNotContains(actual, unexpectedSubstring, methodName)` | Soft assert a string does not contain a substring. |
| `<T extends Comparable<T>> assertGreaterThan(actual, threshold, methodName)` | Hard assert `actual > threshold`. |
| `<T extends Comparable<T>> assertLessThan(actual, threshold, methodName)` | Hard assert `actual < threshold`. |
| `assertSizeEquals(Collection, expectedSize, methodName)` | Hard assert a collection's size. |
| `assertEmptyState(actual, EmptyState, methodName)` | Hard assert empty/not-empty via [EmptyState](EmptyState.md). |
| `assertAll()` | Checks every soft assertion made so far and fails the test if any failed. |

[Back to assertions index](README.md) · [Docs home](../../README.md)
