# SubmitAndFetchOptions

Package: `io.github.phillmon.selenium.modulars.network`

Holds the settings for the common pattern of submitting something and
then fetching the result, where the fetch might not show up immediately.
Describes the actions to run, the expected submit response, the expected
fetch response, and the timing to use, including a page refresh and retry
if the fetch response doesn't appear the first time.

## Related classes

- [NetworkValidationActions](NetworkValidationActions.md) — `coordinateSubmitAndFetchWithReloadRecovery` consumes this.
- [ResponseExpectation](ResponseExpectation.md) — the type held in `submitResponse` and `fetchResponse`.
- [NetworkDefaults](NetworkDefaults.md) — supplies the default timing when none is given.
- [InvalidNetworkExpectationException](InvalidNetworkExpectationException.md) — thrown for missing actions or response expectations.

## Usage example

```java
SubmitAndFetchOptions options = new SubmitAndFetchOptions(
        List.of(() -> modulars.elementActions.click(createOrderButton, "create order button", "createOrder")),
        new ResponseExpectation("/api/orders", "POST", 201),
        new ResponseExpectation("/api/orders/latest", "GET", 200),
        Duration.ofSeconds(20),
        Duration.ofSeconds(3),
        Duration.ofSeconds(20)
);

modulars.networkActions.coordinateSubmitAndFetchWithReloadRecovery(options);
```

## Constructors

| Constructor | Description |
|---|---|
| `SubmitAndFetchOptions(actions, submitResponse, fetchResponse)` | Every timing value at its default. |
| `SubmitAndFetchOptions(actions, submitResponse, fetchResponse, timeout)` | Custom submit timeout; default retry/reload timing. |
| `SubmitAndFetchOptions(actions, submitResponse, fetchResponse, timeout, fetchRetryDelay, reloadTimeout)` | Full control over every timing value. |

Throws [InvalidNetworkExpectationException](InvalidNetworkExpectationException.md)
if `actions` is empty or either response expectation is null.

## Methods

| Method | Description |
|---|---|
| `getActions()` | The actions to run. |
| `getSubmitResponse()` | The response expected to confirm the submit succeeded. |
| `getFetchResponse()` | The response expected when fetching the result. |
| `getTimeout()` | How long to wait for the submit response. |
| `getFetchRetryDelay()` | How long to wait for the fetch before refreshing and retrying. |
| `getReloadTimeout()` | How long to wait for the fetch after the refresh. |

[Back to network index](README.md) · [Docs home](../../README.md)
