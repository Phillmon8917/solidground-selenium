# AuthSnapshot

Package: `io.github.phillmon.selenium.auth` (package-private)

Stores the browser state needed to make a new WebDriver session look logged in:
cookies, `localStorage`, and `sessionStorage`. It is created and used by
[SharedAuthState](SharedAuthState.md), so tests and page objects normally never
construct it directly.

## Related classes

- [SharedAuthState](SharedAuthState.md) - captures and applies snapshots by
  profile name.
- [LoggerUtil](../utils/logging/LoggerUtil.md) - logs storage or cookie restore
  warnings.

## Behaviour

`capture(WebDriver)` reads the current browser cookies and, if the driver is a
`JavascriptExecutor`, reads both browser storage areas.

`applyTo(WebDriver)` clears existing cookies, adds the saved cookies, restores
saved storage, and refreshes the page so the application can pick up the
restored session.

[Back to auth index](README.md) - [Docs home](../README.md)
