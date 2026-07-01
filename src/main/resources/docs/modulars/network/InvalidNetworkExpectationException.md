# InvalidNetworkExpectationException

Package: `io.github.phillmon.selenium.modulars.network` (extends `IllegalArgumentException`)

Thrown when the network validation classes are configured with arguments
that don't make sense: an empty list of actions, a blank url, or setting
both a single status and a list of expected statuses at the same time.

## Related classes

- [ResponseExpectation](ResponseExpectation.md), [CoordinationOptions](CoordinationOptions.md), [SubmitAndFetchOptions](SubmitAndFetchOptions.md) — all throw this from their constructors.

## Constructor

| Constructor | Description |
|---|---|
| `InvalidNetworkExpectationException(String message)` | Describes what part of the configuration was invalid. |

[Back to network index](README.md) · [Docs home](../../README.md)
