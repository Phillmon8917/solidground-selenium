# SelectDropdownActions

Package: `io.github.phillmon.selenium.modulars.dropdown`

Actions for working with native HTML select dropdowns, using Selenium's
`Select` wrapper underneath. Supports single selects and multi-selects.

## Related classes

- [DropdownActionException](DropdownActionException.md) — thrown when an option or a multi-select operation can't be found/performed.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.dropdownActions`.

## Usage example

```java
modulars.dropdownActions.selectByVisibleText(countryDropdown, "United Kingdom", "country dropdown", "chooseCountry");
modulars.dropdownActions.selectByValue(currencyDropdown, "GBP", "currency dropdown", "chooseCurrency");

boolean hasWales = modulars.dropdownActions.hasOptionWithText(regionDropdown, "Wales", "region dropdown", "chooseCountry");

// Multi-select:
modulars.dropdownActions.deselectByVisibleText(tagsDropdown, "Archived", "tags dropdown", "clearArchivedTag");
List<String> selectedTags = modulars.dropdownActions.getAllSelectedOptionsText(tagsDropdown, "tags dropdown", "verifyTags");
```

## Constructors

| Constructor | Description |
|---|---|
| `SelectDropdownActions(WebDriver)` | Default timeout of 10 seconds. |
| `SelectDropdownActions(WebDriver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `selectByVisibleText(locator, visibleText, elementName, methodName)` | Selects the option with matching visible text. |
| `selectByValue(locator, value, elementName, methodName)` | Selects the option with a matching `value` attribute. |
| `selectByIndex(locator, index, elementName, methodName)` | Selects the option at a position. |
| `selectByVisibleTextContains(locator, partialText, elementName, methodName)` | Selects the first option whose text contains `partialText`. |
| `deselectByVisibleText(locator, visibleText, elementName, methodName)` | Deselects an option (multi-select only). |
| `deselectByValue(locator, value, elementName, methodName)` | Deselects an option by value (multi-select only). |
| `deselectByIndex(locator, index, elementName, methodName)` | Deselects an option by position (multi-select only). |
| `deselectAll(locator, elementName, methodName)` | Clears every selection (multi-select only). |
| `getSelectedOptionText(locator, elementName, methodName)` | Visible text of the first selected option. |
| `getSelectedOptionValue(locator, elementName, methodName)` | Value attribute of the first selected option. |
| `getAllSelectedOptionsText(locator, elementName, methodName)` | Visible text of every selected option (multi-select only). |
| `getAllOptionsText(locator, elementName, methodName)` | Visible text of every option. |
| `getOptionsCount(locator, elementName, methodName)` | Total number of options. |
| `isMultiple(locator, elementName, methodName)` | Whether the dropdown allows multiple selections. |
| `hasOptionWithText(locator, visibleText, elementName, methodName)` | Whether an option with the given text exists. |

All methods throw [DropdownActionException](DropdownActionException.md)
when the dropdown doesn't become visible in time, the requested option
doesn't exist, or a deselect/multi-select method is used on a
single-select dropdown.

[Back to dropdown index](README.md) · [Docs home](../../README.md)
