# Solidground Selenium Framework Docs

This is the reference documentation for the classes under
`src/main/java/io/github/phillmon/selenium`. Every class has its own page with
a description, a usage example, and links to the other classes it works
with, so you can click through from one class to the next instead of
searching the source tree.

## How the framework fits together

A test talks to a **page object** that extends
[BasePage](base/BasePage.md). BasePage builds one
[ActionsContainer](base/ActionsContainer.md), which in turn builds one
instance of every action group below. A page object never talks to
Selenium directly — it calls into `this.modulars.xxxActions` instead.

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

## Package index

| Package | What it's for |
|---|---|
| [base](base/README.md) | The base classes every page object builds on: `BasePage`, `ActionsContainer`, `PageModularOptions`, `ReaderActionsContainer`. |
| [errors](errors/README.md) | `SafeStep` and `FaultReporter` — the replacement for hand-written try/catch around page-object steps. |
| [modulars/assertions](modulars/assertions/README.md) | TestNG assertions with logging built in. |
| [modulars/browser](modulars/browser/README.md) | Whole-browser actions: navigation, windows, tabs, screenshots. |
| [modulars/calendar](modulars/calendar/README.md) | Driving date-picker calendar widgets and representing dates. |
| [modulars/dropdown](modulars/dropdown/README.md) | Native HTML `<select>` dropdowns. |
| [modulars/element](modulars/element/README.md) | The main element actions: click, type, read text/attributes, wait. |
| [modulars/iframe](modulars/iframe/README.md) | Switching into and out of iframes. |
| [modulars/keyboard](modulars/keyboard/README.md) | Keyboard input and shortcuts. |
| [modulars/loading](modulars/loading/README.md) | Waiting for spinners, skeletons, and page loaders to disappear. |
| [modulars/mouse](modulars/mouse/README.md) | Mouse gestures: hover, drag and drop, scrolling. |
| [modulars/network](modulars/network/README.md) | Verifying real network requests and responses via Chrome DevTools. |
| [modulars/reader](modulars/reader/README.md) | Downloading and reading Excel, PDF, text, and Word documents. |
| [modulars/toggle](modulars/toggle/README.md) | Switch/checkbox style toggle elements. |
| [modulars/wait](modulars/wait/README.md) | Waiting for a value to stop changing (stabilize). |
| [utils/env](utils/env/README.md) | Reading configuration from `.env`, environment variables, or system properties. |
| [utils/logging](utils/logging/README.md) | The shared logger and its pluggable reporters (Allure, TestNG, no-op). |
| [utils/timeout](utils/timeout/README.md) | Adjusting timeouts so CI runs get extra buffer. |

## Where to start

- New to the framework? Start with [BasePage](base/BasePage.md), then
  [ActionsContainer](base/ActionsContainer.md) to see every action group
  in one place.
- Wrapping a page-object step in error handling? Start with
  [SafeStep](errors/SafeStep.md).
- Looking for how to click, type, or read something on the page? Start
  with [ElementActions](modulars/element/ElementActions.md).
