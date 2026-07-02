# SharedAuthState

Package: `io.github.phillmon.selenium.auth`

Caches a logged-in browser session under a caller-chosen profile name, then
reuses that session in later WebDriver sessions. It captures cookies plus
`localStorage` and `sessionStorage` when the driver supports JavaScript.

This class does not perform login itself. The test supplies the login flow as a
`Runnable`; `SharedAuthState` only decides whether that flow needs to run and
stores the resulting browser state.

## Related classes

- [AuthSnapshot](AuthSnapshot.md) - the package-private saved cookies and
  storage used internally.
- [LoggerUtil](../utils/logging/LoggerUtil.md) - receives save, apply, and reuse
  log messages.

## Usage example

Run the login flow once for a profile, then reuse it for the rest of the run:

```java
SharedAuthState.ensureAuthenticated("admin", driver, EnvLoader.getUrl(), () -> {
    driver.get(EnvLoader.getUrl());
    modulars.elementActions.typeText(usernameField, EnvLoader.getAdminUsername(), "username field", "login");
    modulars.elementActions.typeText(passwordField, EnvLoader.getAdminPassword(), true, "password field", "login");
    modulars.elementActions.click(loginButton, "login button", "login");
});
```

Saving and applying a profile directly:

```java
loginAsAdmin(driver);
SharedAuthState.save("admin", driver);

WebDriver secondDriver = new ChromeDriver();
boolean applied = SharedAuthState.apply("admin", secondDriver, EnvLoader.getUrl());
```

Clearing stale profiles:

```java
SharedAuthState.forget("admin");
SharedAuthState.forgetAll();
```

## Methods

| Method | Description |
|---|---|
| `static boolean isCached(String profile)` | Returns whether a profile has already been saved. |
| `static void save(String profile, WebDriver driver)` | Captures the current driver's cookies and storage under the profile name. |
| `static boolean apply(String profile, WebDriver driver, String landingUrl)` | Applies a saved profile to the driver. Returns `false` if no profile is cached. |
| `static void ensureAuthenticated(String profile, WebDriver driver, String landingUrl, Runnable loginFlow)` | Reuses a saved profile or runs the login flow once and saves the result. |
| `static void forget(String profile)` | Removes one cached profile. |
| `static void forgetAll()` | Removes every cached profile. |

Profile names must be non-null and non-blank. The driver must be on the target
application's origin before cookies can be applied; pass `landingUrl` when the
helper should navigate there first.

[Back to auth index](README.md) - [Docs home](../README.md)
