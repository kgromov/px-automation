package elements.dropdown;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static elements.HelperSingleton.getHelper;
import static pages.locators.ElementLocators.DROPDOWN_OPTION;

/**
 * Created by kgr on 10/7/2016.
 */

public class FilteredDropDown extends SelectElement {
    public FilteredDropDown(WebElement element) {
        super(element);
    }

    public void resetFilter() {
        expand();
        log.info(String.format("Reset filter, current value '%s'", getLabel()));
        resetButton().click();
        helper.waitForAjaxComplete();
        // different behaviour for different filters
       /* if(helper.isElementPresent(".//button[@id='']"))
        button.click();*/
    }

    public void setByText(List<String> text) {
        log.info(String.format("Select in dropdown options by text '%s'", text));
        expand();
        text.forEach(option -> {
            try {
                log.info(String.format("Type and select in dropdown filtered option by text '%s'", option));
                helper.type(input(), option);
                helper.getElement(getWrappedElement(), DROPDOWN_OPTION).click();
                helper.waitForAjaxComplete();
            } catch (ElementNotVisibleException | NoSuchElementException e) {
                throw new Error(String.format("The item with text '%s' is missed in drop down", text));
            }
        });
        expand();
    }

    public void setByTitle(List<String> titles) {
        log.info(String.format("Select in dropdown options by title '%s'", titles));
        expand();
        titles.forEach(option -> {
            try {
                log.info(String.format("Type and select in dropdown filtered option by title '%s'", option));
                helper.type(input(), option);
                helper.getElement(getWrappedElement(), ".//*[@title='" + option + "']").click();
                helper.waitForAjaxComplete();
            } catch (ElementNotVisibleException | NoSuchElementException e) {
                throw new Error(String.format("The item with text '%s' is missed in drop down", titles));
            }
        });
        expand();
    }

    public void setByIndex(int index) {
        log.info(String.format("Select in dropdown options by title '%d'", index));
        expand();
        try {
            getHelper().getElement(getWrappedElement(), String.format("(%s)[%d]", DROPDOWN_OPTION, index)).click();
            getHelper().waitForAjaxComplete();
        } catch (ElementNotVisibleException | NoSuchElementException e) {
            throw new Error(String.format("The no item by index '%d' in drop down", index));
        }
        expand();
    }

    public List<String> getMultiValue() {
        return helper.getElements(getWrappedElement(), ".//input[@checked]/ancestor-or-self::div[@class='acol']").stream()
//                .map(item -> item.getText().trim()).collect(Collectors.toList());
                .map(item -> item.getAttribute("title").trim()).collect(Collectors.toList());
    }

    protected WebElement resetButton() {
        return helper.getElement(getWrappedElement(), By.cssSelector("button.helperButton"));
    }

    protected WebElement closeButton() {
        return helper.getElement(getWrappedElement(), ".//button[@id='']");
    }

    @Override
    public String getLabel() {
        return getValue();
    }
}