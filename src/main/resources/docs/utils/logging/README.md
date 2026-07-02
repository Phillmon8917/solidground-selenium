# utils/logging

The shared logger every action class writes through, and its pluggable
reporters. Reporter configuration and execution log files are scoped per
thread, which keeps parallel tests from changing each other's reporting setup.

- [LoggerUtil](LoggerUtil.md) — the single place every log message goes through.
- [TestReporter](TestReporter.md) — the interface a reporter implements.
- [AllureReporter](AllureReporter.md) — sends messages to the Allure report (default).
- [TestNgReporter](TestNgReporter.md) — sends messages to TestNG's own reporter.
- [NoOpReporter](NoOpReporter.md) — discards every message.

[Back to utils index](../README.md) · [Docs home](../../README.md)
