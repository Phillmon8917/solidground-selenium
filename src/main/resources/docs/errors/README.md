# errors

The replacement for hand-written try/catch around page-object steps.

- [SafeStep](SafeStep.md) — runs a step, screenshots and reports on failure, then rethrows.
- [FaultReporter](FaultReporter.md) — the central place failures get logged.
- [FaultAnalyzer](FaultAnalyzer.md) — turns a `Throwable` into a `FaultDetails` record.
- [FaultDetails](FaultDetails.md) — the structured record of one failure.
- [FaultCache](FaultCache.md) — stops the same failure being logged twice.
- [StepExecutionException](StepExecutionException.md) — the unchecked exception `SafeStep` throws when wrapping a checked one.
- [ThrowingRunnable](ThrowingRunnable.md) — a void step body that is allowed to throw.
- [ThrowingSupplier](ThrowingSupplier.md) — a value-returning step body that is allowed to throw.

[Back to docs index](../README.md)
