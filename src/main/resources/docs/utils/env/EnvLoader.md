# EnvLoader

Package: `io.github.phillmon.selenium.utils.env`

Resolves configuration values needed to run the tests, such as the admin
username, password, and base url. Looks in the project's `.env` file
first, then the operating system's environment variables, then Java
system properties, so the same code works whether a value is set locally
in a `.env` file or injected by a CI pipeline. A missing `.env` file is fine;
the loader continues on to environment variables and system properties.

## Related classes

- [BasePage](../../base/BasePage.md) — page objects typically call `EnvLoader.getUrl()` when navigating.
- [SafeStep](../../errors/SafeStep.md) — the top-level docs example uses `EnvLoader.getAdminUsername()`/`getAdminPassword()` inside a wrapped step.

## Usage example

```java
modulars.browserActions.navigateToUrl(EnvLoader.getUrl(), "loadThePage");

modulars.elementActions.typeText(usernameField, EnvLoader.getAdminUsername(), "username field", "loginAsAdmin");
modulars.elementActions.typeText(passwordField, EnvLoader.getAdminPassword(), true, "password field", "loginAsAdmin");
```

Reading any other configured property by name:

```java
String apiKey = EnvLoader.get("THIRD_PARTY_API_KEY");
```

## Methods

| Method | Description |
|---|---|
| `static String getAdminUsername()` | The admin username, looked up under `ADMIN_USERNAME`. |
| `static String getAdminPassword()` | The admin password, looked up under `ADMIN_PASSWORD`. |
| `static String getUrl()` | The base test url, looked up under `URL`. |
| `static String get(String property)` | Looks up any named property: `.env` first, then environment variables, then system properties. |

All methods throw `IllegalStateException` if the property isn't found in
any of the three places.

[Back to env index](README.md) · [Docs home](../../README.md)
