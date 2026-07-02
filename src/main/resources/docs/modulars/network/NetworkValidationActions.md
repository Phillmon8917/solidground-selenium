# NetworkValidationActions

Package: `io.github.phillmon.selenium.modulars.network`

The page-object facing entry point for verifying real network traffic
during a test, such as confirming that clicking a button actually sent
the expected request and got back the expected response. Wraps
[NetworkResponseValidator](NetworkResponseValidator.md), which does the
actual work of listening to Chrome DevTools. The DevTools session is opened
only on first use, so a page object can hold `networkActions` even when the
current test never validates network traffic.

## Related classes

- [NetworkResponseValidator](NetworkResponseValidator.md) — does the real work; this class just forwards to it.
- [CoordinationOptions](CoordinationOptions.md) — passed to `coordinateActionsAndResponses`.
- [SubmitAndFetchOptions](SubmitAndFetchOptions.md) — passed to `coordinateSubmitAndFetchWithReloadRecovery`.
- [ResponseExpectation](ResponseExpectation.md) — describes the responses used inside both options types.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.networkActions`.

## Usage example

```java
modulars.networkActions.coordinateActionsAndResponses(new CoordinationOptions(
        List.of(() -> modulars.elementActions.click(saveButton, "save button", "saveProfile")),
        List.of(new ResponseExpectation("/api/profile", "PUT", 200))
));
```

Submitting a form and then fetching the created record, with automatic
retry via a page refresh:

```java
modulars.networkActions.coordinateSubmitAndFetchWithReloadRecovery(new SubmitAndFetchOptions(
        List.of(() -> modulars.elementActions.click(createOrderButton, "create order button", "createOrder")),
        new ResponseExpectation("/api/orders", "POST", 201),
        new ResponseExpectation("/api/orders/latest", "GET", 200)
));
```

Closing the DevTools session once network validation is no longer
needed, typically in a test teardown or through `BasePage.close()`:

```java
modulars.networkActions.close();
```

## Constructor

| Constructor | Description |
|---|---|
| `NetworkValidationActions(WebDriver)` | Stores the driver. The Chrome DevTools session is opened lazily on first validation. |

## Methods

| Method | Description |
|---|---|
| `coordinateActionsAndResponses(CoordinationOptions)` | Runs actions and confirms every expected response arrived. |
| `coordinateSubmitAndFetchWithReloadRecovery(SubmitAndFetchOptions)` | Runs actions, confirms the submit response, then waits for the fetch response — refreshing and retrying once if it doesn't show up. |
| `close()` | Closes the underlying DevTools session if one was opened. Safe to call even if no network validation ran. |

[Back to network index](README.md) · [Docs home](../../README.md)
