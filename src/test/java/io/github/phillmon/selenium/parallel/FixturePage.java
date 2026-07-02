package io.github.phillmon.selenium.parallel;

import io.github.phillmon.selenium.base.BasePage;
import io.github.phillmon.selenium.base.PageModularOptions;
import io.github.phillmon.selenium.modulars.toggle.ToggleStateResolver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the local parallel-test HTML fixture, exercising
 * element typing, dropdown selection, toggling a checkbox, clicking, and
 * reading text back, all through the library's own action groups instead
 * of raw Selenium calls.
 */
class FixturePage extends BasePage {
    private static final By INPUT = By.id("input-field");
    private static final By SELECT = By.id("select-field");
    private static final By CHECKBOX = By.id("checkbox-field");
    private static final By BUTTON = By.id("set-button");
    private static final By OUTPUT = By.id("output");

    FixturePage(WebDriver driver) {
        super(driver, PageModularOptions.builder()
                .withToggleStateResolver(ToggleStateResolver.checkboxSelected())
                .build());
    }

    void fillOutForm(String inputValue, String selectValue) {
        modulars.elementActions.typeText(INPUT, inputValue, "input-field", "fillOutForm");
        modulars.dropdownActions.selectByValue(SELECT, selectValue, "select-field", "fillOutForm");
        modulars.toggleActions.ensureToggledOn(CHECKBOX, "checkbox-field", "fillOutForm");
        modulars.elementActions.click(BUTTON, "set-button", "fillOutForm");
    }

    String readOutput() {
        return modulars.elementActions.getText(OUTPUT, "output", "readOutput");
    }
}
