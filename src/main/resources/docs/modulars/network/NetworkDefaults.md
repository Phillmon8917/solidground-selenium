# NetworkDefaults

Package: `io.github.phillmon.selenium.modulars.network`

Holds the default timing values used by the network validation classes
when a caller doesn't supply its own. Every value already has
[TimeoutUtil](../../utils/timeout/TimeoutUtil.md)'s CI adjustment applied.

## Related classes

- [CoordinationOptions](CoordinationOptions.md) / [SubmitAndFetchOptions](SubmitAndFetchOptions.md) — fall back to these constants when a timing argument is omitted or null.
- [TimeoutUtil](../../utils/timeout/TimeoutUtil.md) — provides the CI-aware adjustment applied to each constant.

## Constants

| Constant | Default value | Used for |
|---|---|---|
| `DEFAULT_TIMEOUT` | 30 seconds | Waiting for a response overall. |
| `FETCH_RETRY_DELAY` | 3 seconds | How long to wait before retrying a fetch that hasn't shown up yet. |
| `RELOAD_TIMEOUT` | 30 seconds | How long to wait for the fetch response after refreshing the page. |

[Back to network index](README.md) · [Docs home](../../README.md)
