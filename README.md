# SolidGround Selenium

A Selenium automation library for Java focused on reducing test flakiness.

Page objects extend a common `BasePage`, which wires up a full set of modular
action groups (element, browser, calendar, dropdown, iframe, keyboard,
loading, mouse, network, reader, toggle, wait, assertions) so a test never
talks to Selenium directly. Steps are wrapped with `SafeStep` and
`FaultReporter` instead of hand-written try/catch, giving consistent error
reporting across a suite.

## Features

- **BasePage / ActionsContainer** — one base class every page object extends,
  building one instance of every action group.
- **Modular actions** — element, browser, calendar, dropdown, iframe,
  keyboard, loading, mouse, network, reader (Excel/PDF/text/Word), toggle,
  wait, and assertions, each documented independently.
- **SafeStep + FaultReporter** — a drop-in replacement for repetitive
  try/catch blocks around page-object steps.
- **Env loading** — configuration from `.env`, environment variables, or
  system properties via `EnvLoader`.
- **Pluggable logging** — Allure, TestNG, or no-op reporters behind a shared
  logger.
- **CI-aware timeouts** — `TimeoutUtil` adjusts wait durations with extra
  buffer when running in CI.

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.phillmon8917</groupId>
    <artifactId>solidground-selenium</artifactId>
    <version>1.0.0</version>
</dependency>
```

Requires Java 17 and a Selenium 4.x runtime (`selenium-java` /
`selenium-devtools` are declared as `provided`, so bring your own version).

## Quick example

```java
public class LoginPage extends BasePage {
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void loginAsAdmin() {
        SafeStep.run(driver, "loginAsAdmin", () -> {
            modulars.elementActions.typeText(usernameField, EnvLoader.getAdminUsername(), "username field", "loginAsAdmin");
            modulars.elementActions.typeText(passwordField, EnvLoader.getAdminPassword(), true, "password field", "loginAsAdmin");
            modulars.elementActions.click(loginButton, "login button", "loginAsAdmin");
            modulars.loadingIndicatorActions.waitForPageLoader("loginAsAdmin");
        });
    }
}
```

## Documentation

Full reference docs for every class live under
[src/main/resources/docs](src/main/resources/docs/README.md), organized by
package with usage examples and cross-links between related classes.

## License

MIT — see [LICENSE](LICENSE).
