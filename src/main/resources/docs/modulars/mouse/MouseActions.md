# MouseActions

Package: `io.github.phillmon.selenium.modulars.mouse`

Actions for mouse movements and gestures using Selenium's `Actions`
builder: clicking, double clicking, right clicking, hovering, dragging
and dropping, and scrolling — useful for pages that react to hover
states, drag targets, or precise mouse positioning.

## Related classes

- [MouseActionException](MouseActionException.md) — thrown when an element doesn't reach the needed state, or gesture arguments don't match up.
- [ElementActions](../element/ElementActions.md) — for plain clicks and typing that don't need gesture support.
- [ActionsContainer](../../base/ActionsContainer.md) — exposes this as `modulars.mouseActions`.

## Usage example

```java
modulars.mouseActions.hover(userMenuTrigger, "user menu trigger", "openUserMenu");
modulars.mouseActions.hoverAndClick(userMenuTrigger, "user menu trigger", signOutMenuItem, "sign out menu item", "signOut");

modulars.mouseActions.dragAndDrop(taskCard, "task card", inProgressColumn, "in progress column", "moveTaskToInProgress");

// A widget that needs gradual movement to register the drag:
modulars.mouseActions.dragAndDropSlowly(sliderHandle, "slider handle", sliderTrackEnd, "slider track end", 20, "setVolumeToMax");
```

Hovering through a multi-level menu:

```java
modulars.mouseActions.hoverChain("openReportsSubmenu",
        List.of("menu item", "reports submenu trigger"),
        menuItemLocator, reportsSubmenuTriggerLocator);
```

## Constructors

| Constructor | Description |
|---|---|
| `MouseActions(WebDriver)` | Default timeout of 10 seconds. |
| `MouseActions(WebDriver, Duration timeout)` | Custom timeout. |

## Methods

| Method | Description |
|---|---|
| `click` / `doubleClick` / `rightClick` | Click variants via the `Actions` builder. |
| `clickAndHold(locator, elementName, methodName)` | Presses and holds the mouse button. |
| `release(methodName)` | Releases the mouse button. |
| `hover(locator, elementName, methodName)` | Moves the mouse over an element. |
| `hoverAndClick(hoverLocator, hoverElementName, clickLocator, clickElementName, methodName)` | Hovers to reveal a menu, then clicks. |
| `hoverChain(methodName, elementNames, By...)` | Hovers through several elements in sequence. |
| `clickAtOffset(locator, xOffset, yOffset, elementName, methodName)` | Clicks at a pixel offset from an element's centre. |
| `moveByOffset(xOffset, yOffset, methodName)` | Moves the mouse pointer by an offset. |
| `dragAndDrop(sourceLocator, sourceElementName, targetLocator, targetElementName, methodName)` | Drags one element onto another. |
| `dragAndDropByOffset(sourceLocator, xOffset, yOffset, sourceElementName, methodName)` | Drags an element by an offset. |
| `dragAndDropSlowly(sourceLocator, sourceElementName, targetLocator, targetElementName, steps, methodName)` | Drags in several small steps. |
| `scrollToElement(locator, elementName, methodName)` | Scrolls until an element is in view. |
| `scrollByAmount(deltaX, deltaY, methodName)` | Scrolls by an amount. |
| `scrollFromElement(locator, deltaX, deltaY, elementName, methodName)` | Scrolls starting from an element's position. |

All wait-based methods throw
[MouseActionException](MouseActionException.md) if the element doesn't
reach the needed state in time, or if chained-gesture arguments (like
`hoverChain`) don't match up.

[Back to mouse index](README.md) · [Docs home](../../README.md)
