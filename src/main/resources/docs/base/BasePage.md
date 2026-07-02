# BasePage

Package: `io.github.phillmon.selenium.base`

The abstract class every page object in the project extends. It holds the
`WebDriver` for the page and builds one [ActionsContainer](ActionsContainer.md),
so a page object gets access to every action group (clicking, typing,
waiting, reading documents, and so on) without creating any of them
itself. It implements `AutoCloseable`; calling `close()` releases resources
owned by action groups, such as a network validation DevTools session. It does
not quit the WebDriver.

## Related classes

- [ActionsContainer](ActionsContainer.md) — built automatically by the constructor and exposed as `this.modulars`.
- [PageModularOptions](PageModularOptions.md) — pass a custom one to change how the action groups behave.
- [SafeStep](../errors/SafeStep.md) — typically used inside page-object methods to wrap a step in error handling.

## Usage example

```java
public class LoginPage extends BasePage {
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void loginAsAdmin() {
        modulars.elementActions.typeText(usernameField, EnvLoader.getAdminUsername(), "username field", "loginAsAdmin");
        modulars.elementActions.typeText(passwordField, EnvLoader.getAdminPassword(), true, "password field", "loginAsAdmin");
        modulars.elementActions.click(loginButton, "login button", "loginAsAdmin");
    }
}
```

If the page needs custom behaviour, such as a non-default calendar layout
or a specific download folder, pass a [PageModularOptions](PageModularOptions.md)
into the other constructor:

```java
public class ReportsPage extends BasePage {
    public ReportsPage(WebDriver driver, Path downloadDirectory) {
        super(driver, PageModularOptions.builder()
                .withDownloadDirectory(downloadDirectory)
                .build());
    }
}
```

For a short-lived page object, `try-with-resources` can release action resources
automatically:

```java
try (ReportsPage reportsPage = new ReportsPage(driver, Path.of("build/downloads"))) {
    reportsPage.downloadMonthlyReport();
}
```

## Constructors

| Constructor | Description |
|---|---|
| `BasePage(WebDriver driver)` | Builds the page using default options for every action group. Throws `IllegalArgumentException` if `driver` is null. |
| `BasePage(WebDriver driver, PageModularOptions options)` | Builds the page with custom options. Throws `IllegalArgumentException` if `driver` is null. |

## Methods

| Method | Description |
|---|---|
| `close()` | Releases resources opened by action groups. Does not quit the WebDriver. |

## Fields available to subclasses

| Field | Description |
|---|---|
| `driver` | The `WebDriver` for the current browser session. |
| `modulars` | The [ActionsContainer](ActionsContainer.md) holding every action group. |

[Back to base index](README.md) · [Docs home](../README.md)
