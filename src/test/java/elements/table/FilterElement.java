package elements.table;

import elements.SuperTypifiedElement;
import elements.checkbox.MultipleCheckboxElement;
import elements.dropdown.DropDownElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.ElementLocators.*;
import static pages.locators.LoginPageLocators.LOGGED_USER_NAME;

/**
 * Created by konstantin on 21.10.2016.
 */
public class FilterElement extends SuperTypifiedElement {
    private static final String INPUT_ELEMENT = ".//input[@focus-trigger='menuVisible']";
    private static final By CLOSE_ELEMENT = By.cssSelector(".px-overview-config.medium");

    public FilterElement(WebElement wrappedElement) {
        super(wrappedElement);
    }

    // ================================ Search ================================
    public void setByText(String text) {
        log.info(String.format("Search by text '%s'", text));
        WebElement toggle = getToggle();
        toggle.click();
        helper.waitUntilDisplayed(INPUT_ELEMENT);
        helper.type(getInput(), text);
//        toggle.click();
//        helper.getElement(CLOSE_ELEMENT).click();
        helper.getElement(LOGGED_USER_NAME).click(); // workaround cause of strange toggle button behaviour
        helper.waitUntilToBeInvisible(ACTIONS_DROPDOWN);
    }

    public void setByText(String text, String searchBy) {
        log.info(String.format("Search by text '%s' and category '%s'", text, searchBy));
        WebElement toggle = getToggle();
        toggle.click();
        helper.waitUntilDisplayed(INPUT_ELEMENT);
        setSearchBy(searchBy);
        helper.type(getInput(), text);
//        toggle.click();
     /*   helper.waitUntilToBeClickable(CLOSE_ELEMENT);
        helper.getElement(CLOSE_ELEMENT).click();*/
        helper.getElement(LOGGED_USER_NAME).click(); // workaround cause of strange toggle button behaviour
        helper.waitUntilToBeInvisible(ACTIONS_DROPDOWN);
    }

    private void setSearchBy(String searchBy) {
        log.info(String.format("Select category '%s' to search by", searchBy));
        helper.click(getButton());
        helper.waitUntilDisplayed(FILTERED_DROPDOWN);
        DropDownElement selectElement = new DropDownElement(helper.getElement(FILTERED_DROPDOWN));
        selectElement.getItemByContainsText(searchBy).click();
        // cause of difference in text
//        selectElement.setByText(searchBy);
    }

    // ================================ Columns ================================
    public void setCheckboxes() {
        log.info("Select all checkboxes in actions dropdown");
        MultipleCheckboxElement multipleCheckboxElement = new MultipleCheckboxElement(
                helper.getElement(getWrappedElement(), MULTI_SELECT_DROPDOWN));
        List<WebElement> unchecked = multipleCheckboxElement.getInputs().stream()
                .filter(input -> !Boolean.parseBoolean(input.getAttribute("checked")))
                .collect(Collectors.toList());
        if (unchecked.isEmpty()) return;
        WebElement toggle = getToggle();
        toggle.click();
        helper.waitUntilDisplayed(MULTI_SELECT_DROPDOWN);
        unchecked.forEach(WebElement::click);
//        toggle.click();
//        helper.getElement(CLOSE_ELEMENT).click();
        helper.getElement(LOGGED_USER_NAME).click(); // workaround cause of strange toggle button behaviour
        helper.waitUntilToBeInvisible(MULTI_SELECT_DROPDOWN);
    }

    public void setCheckboxes(List<String> names) {
        log.info("Select all checkboxes in actions dropdown");
        MultipleCheckboxElement multipleCheckboxElement = new MultipleCheckboxElement(
                helper.getElement(getWrappedElement(), MULTI_SELECT_DROPDOWN));
        // filter by names
        List<WebElement> checkboxes = multipleCheckboxElement.getInputs().stream()
                .filter(checkbox -> names.contains(checkbox.getAttribute("data-field-name")))
                .collect(Collectors.toList());
        if (multipleCheckboxElement.isAllItemsChecked(checkboxes)) return;
        getToggle().click();
        helper.waitUntilDisplayed(MULTI_SELECT_DROPDOWN);
        multipleCheckboxElement.setAll(checkboxes);
//        toggle.click();
        helper.getElement(CLOSE_ELEMENT).click();
        helper.waitUntilToBeInvisible(MULTI_SELECT_DROPDOWN);
    }

    private WebElement getToggle() {
        return helper.getElement(getWrappedElement(), ".//i[not(contains(@class, 'px-checkbox'))]");
    }

    private WebElement getInput() {
        return helper.getElement(getWrappedElement(), INPUT_ELEMENT);
    }

    private WebElement getButton() {
        return helper.getElement(getWrappedElement(), ".//button");
    }
}