# CoordinationOptions

Package: `io.github.phillmon.selenium.modulars.network`

Holds the settings for coordinating a set of page actions with the
network responses they're expected to trigger: which actions to run,
which responses to wait for, and how long to wait.
[NetworkValidationActions](NetworkValidationActions.md)`.coordinateActionsAndResponses`
uses this to run the actions and then confirm every expected response
arrived.

## Related classes

- [NetworkValidationActions](NetworkValidationActions.md) — the class that consumes this.
- [ResponseExpectation](ResponseExpectation.md) — the type held in `waitForResponses`.
- [NetworkDefaults](NetworkDefaults.md) — supplies the default timeout when none is given.
- [InvalidNetworkExpectationException](InvalidNetworkExpectationException.md) — thrown for an empty actions list.

## Usage example

```java
CoordinationOptions options = new CoordinationOptions(
        List.of(() -> modulars.elementActions.click(saveButton, "save button", "saveProfile")),
        List.of(new ResponseExpectation("/api/profile", "PUT", 200)),
        Duration.ofSeconds(15)
);

modulars.networkActions.coordinateActionsAndResponses(options);
```

## Constructors

| Constructor | Description |
|---|---|
| `CoordinationOptions(List<Runnable> actions)` | No responses to wait for; default timeout. |
| `CoordinationOptions(List<Runnable> actions, List<ResponseExpectation> waitForResponses)` | Default timeout. |
| `CoordinationOptions(List<Runnable> actions, List<ResponseExpectation> waitForResponses, Duration timeout)` | Custom timeout. |

Throws [InvalidNetworkExpectationException](InvalidNetworkExpectationException.md)
if `actions` is null or empty.

## Methods

| Method | Description |
|---|---|
| `getActions()` | The actions to run. |
| `getWaitForResponses()` | The responses to wait for and validate afterwards. |
| `getTimeout()` | How long to wait for those responses. |

[Back to network index](README.md) · [Docs home](../../README.md)
