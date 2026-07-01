# FaultCache

Package: `io.github.phillmon.selenium.errors` (package-private)

Keeps track of which exception instances have already been reported, so
the same failure doesn't get logged more than once as it passes through
several nested [SafeStep](SafeStep.md) calls. Not part of the public API —
used internally by [FaultReporter](FaultReporter.md).

## Related classes

- [FaultReporter](FaultReporter.md) — the only caller of this class; `clearFaultCache()` on it calls `clearAll()` here.

## Methods

| Method | Description |
|---|---|
| `boolean shouldReport(Throwable)` | Returns true the first time a given error instance is seen, false on repeats. Returns false for null. |
| `void clearAll()` | Forgets every error remembered so far. |

[Back to errors index](README.md) · [Docs home](../README.md)
