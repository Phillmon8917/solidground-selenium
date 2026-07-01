# modulars/network

Verifying real network requests and responses via Chrome DevTools,
available on a page object as `modulars.networkActions`. Only works with
ChromeDriver.

- [NetworkValidationActions](NetworkValidationActions.md) — the page-object facing entry point.
- [NetworkResponseValidator](NetworkResponseValidator.md) — the DevTools engine behind it (package-private).
- [CoordinationOptions](CoordinationOptions.md) — settings for "run these actions, wait for these responses".
- [SubmitAndFetchOptions](SubmitAndFetchOptions.md) — settings for the submit-then-fetch-with-retry pattern.
- [ResponseExpectation](ResponseExpectation.md) — describes one expected response (url/method/status/body).
- [NetworkDefaults](NetworkDefaults.md) — default timeout values.
- [InvalidNetworkExpectationException](InvalidNetworkExpectationException.md) — thrown for bad configuration.
- [NetworkResponseValidationException](NetworkResponseValidationException.md) — thrown when a response doesn't arrive or doesn't match.

[Back to modulars index](../README.md) · [Docs home](../../README.md)
