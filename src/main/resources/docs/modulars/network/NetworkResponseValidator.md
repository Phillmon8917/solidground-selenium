# NetworkResponseValidator

Package: `io.github.phillmon.selenium.modulars.network` (package-private)

Listens to the browser's real network traffic through Chrome DevTools and
checks that expected requests actually happened with the expected status
codes and, optionally, response bodies. This is the engine behind
[NetworkValidationActions](NetworkValidationActions.md) — not part of the
public API, so page objects never construct this directly.

## Related classes

- [NetworkValidationActions](NetworkValidationActions.md) — the public class that wraps this one.
- [ResponseExpectation](ResponseExpectation.md) — what each recorded response is matched against.
- [NetworkResponseValidationException](NetworkResponseValidationException.md) — thrown when a match doesn't arrive in time or fails validation.

## Methods

| Method | Description |
|---|---|
| `validateActionsAndResponses(actions, expectations, timeout)` | Clears prior traffic, runs the actions, then waits for and validates each expected response. |
| `validateSubmitAndFetch(actions, submit, fetch, timeout, fetchRetryDelay, reloadTimeout)` | Validates a submit response, then a fetch response — refreshing and retrying once if the fetch doesn't show up in time. |
| `logNetworkState(context)` | Logs every recorded request/response and its status, for diagnosing a timeout. |
| `clear()` | Forgets every request/response recorded so far. |
| `close()` | Closes the DevTools session. |

[Back to network index](README.md) · [Docs home](../../README.md)
