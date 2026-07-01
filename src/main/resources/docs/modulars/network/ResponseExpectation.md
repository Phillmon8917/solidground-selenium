# ResponseExpectation

Package: `io.github.phillmon.selenium.modulars.network`

Describes one network response a test expects to see: which url it
should come from, optionally which HTTP method, and optionally which
status code(s) and body content. [NetworkResponseValidator](NetworkResponseValidator.md)
uses this to find and check the right response among all the network
traffic the browser generates.

## Related classes

- [CoordinationOptions](CoordinationOptions.md) / [SubmitAndFetchOptions](SubmitAndFetchOptions.md) — both take `ResponseExpectation` instances.
- [NetworkResponseValidator](NetworkResponseValidator.md) — matches recorded responses against this description.
- [InvalidNetworkExpectationException](InvalidNetworkExpectationException.md) — thrown for a blank url or conflicting status arguments.

## Usage example

```java
ResponseExpectation saveProfile = new ResponseExpectation("/api/profile", "PUT", 200);

ResponseExpectation searchResults = new ResponseExpectation("/api/search", "GET")
        .withBodyValidator(body -> body.contains("\"results\""));

List<ResponseExpectation> anyOf200or201 = ResponseExpectation.forStatuses("/api/orders", "POST", 200, 201);
```

## Constructors

| Constructor | Description |
|---|---|
| `ResponseExpectation(String url)` | Matches any response whose url contains the given text. |
| `ResponseExpectation(String url, String method)` | Also requires a specific HTTP method. |
| `ResponseExpectation(String url, String method, Integer status)` | Also requires a specific status code. |
| `ResponseExpectation(String url, String method, List<Integer> expectedStatuses)` | Requires the status to be one of several acceptable codes. |

## Static factory

| Method | Description |
|---|---|
| `static List<ResponseExpectation> forStatuses(url, method, Integer...)` | Builds one expectation per status code, all matching the same url/method. |

## Instance methods

| Method | Description |
|---|---|
| `withBodyValidator(Predicate<String>)` | Returns a copy of this expectation that also checks the response body. |
| `getUrl()` / `getMethod()` / `getStatus()` / `getExpectedStatuses()` / `getBodyValidator()` / `hasBodyValidator()` | Read the configured fields. |

All constructors throw
[InvalidNetworkExpectationException](InvalidNetworkExpectationException.md)
for a blank url or for setting both a single status and a list of
expected statuses at once.

[Back to network index](README.md) · [Docs home](../../README.md)
