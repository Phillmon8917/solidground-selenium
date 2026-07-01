# DropdownActionException

Package: `io.github.phillmon.selenium.modulars.dropdown`

Thrown when [SelectDropdownActions](SelectDropdownActions.md) can't carry
out a requested action, such as selecting an option that doesn't exist,
or trying to deselect an option on a dropdown that isn't a multi-select.

## Related classes

- [SelectDropdownActions](SelectDropdownActions.md) — the only class that throws this.

## Constructors

| Constructor | Description |
|---|---|
| `DropdownActionException(String message)` | No underlying cause. |
| `DropdownActionException(String message, Throwable cause)` | Wraps the original Selenium exception. |

[Back to dropdown index](README.md) · [Docs home](../../README.md)
