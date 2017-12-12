package elements.dropdown;

import elements.SuperTypifiedElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static elements.ElementsHelper.getAttributeContainsToIgnoreCaseLocator;
import static elements.ElementsHelper.getTextContainsToIgnoreCaseLocator;
import static pages.locators.ElementLocators.DROPDOWN_OPTION;

/**
 * Created by konstantin on 05.10.2016.
 */
// multiple/single choices
public class DropDownElement extends SuperTypifiedElement {
    protected By optionBy = By.xpath(DROPDOWN_OPTION);

    public DropDownElement(WebElement element) {
        super(element);
    }

    public void setByTitle(String title) {
        log.info(String.format("Select drop down option by title '%s'", title));
        getWrappedElement().click();
        try {
//            helper.getElement(getWrappedElement(), ".//*[@title='" + title + "']").click();
            helper.getElement(getWrappedElement(), getAttributeContainsToIgnoreCaseLocator("*", "title", title)).click();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("No item with title = '%s' inside '%s'", title, getBy()), e);
        }
    }

    @Override
    public void setByText(String text) {
        log.info(String.format("Select drop down option by text '%s'", text));
        helper.getElement(getWrappedElement(), getTextContainsToIgnoreCaseLocator("*", text)).click();
    }

    public void setOptionBy(By optionBy) {
        this.optionBy = optionBy;
    }

    public List<WebElement> getSelectedOptions() {
        return options().stream().filter(option -> Boolean.parseBoolean(option.getAttribute("checked"))).collect(Collectors.toList());
    }

    public List<String> getItems() {
        return options().stream().map(element -> element.getText().trim()).collect(Collectors.toList());
    }

    public List<String> getCollapsedItems() {
        return options().stream().map(element -> StringUtils.normalizeSpace(element.getAttribute("textContent"))).collect(Collectors.toList());
    }

    public WebElement getItemByContainsText(String filterValue) {
        for (WebElement option : options()) {
            if (option.getText().toLowerCase().contains(filterValue.toLowerCase())) {
                return option;
            }
        }
        throw new NoSuchElementException(String.format("There are now element contains text '%s'", filterValue));
    }

    public String getValue() {
        return helper.getElement(getWrappedElement(), By.tagName("input")).getAttribute("value").trim().replaceAll("\\s{2,}", " ");
    }

    protected List<WebElement> options() {
        return helper.getElements(getWrappedElement(), optionBy);
    }
}