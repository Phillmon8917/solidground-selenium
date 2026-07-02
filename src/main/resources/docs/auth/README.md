# auth

Shared browser authentication state for tests that should not repeat the
login flow every time.

- [SharedAuthState](SharedAuthState.md) - the public API for saving, applying,
  reusing, and clearing cached auth profiles.
- [AuthSnapshot](AuthSnapshot.md) - the package-private capture of cookies and
  browser storage behind each saved profile.

[Back to docs index](../README.md)
