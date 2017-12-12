package elements.dropdown;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static elements.ElementsHelper.getAttributeContainsToIgnoreCaseLocator;
import static pages.locators.ElementLocators.DROPDOWN_OPTION;
import static pages.locators.ElementLocators.TEXT_VALUE;

/**
 * Created by konstantin on 12.10.2016.
 */
public class SelectElement extends DropDownElement {

    public SelectElement(WebElement element) {
        super(element);
    }

    public void expand() {
        log.info(String.format("Click on select '%s' waiting for drop down", getLabel()));
        button().click();
    }

    public void setByTitle(String title) {
        setByTitle(title, false);
    }

    public void setByTitle(String title, boolean type) {
        log.info(String.format("Select in dropdown option by title '%s'", title));
        expand();
        if (type) {
            log.info(String.format("Type and select in dropdown filtered option by text '%s'", title));
            helper.type(input(), title);
        }
        try {
            log.info(String.format("Number of items = '%d'", helper.getElements(getWrappedElement(), DROPDOWN_OPTION).size()));
//            helper.getElement(getWrappedElement(), By.xpath(".//*[@title='" + title + "']")).click();
            helper.getElement(getWrappedElement(), getAttributeContainsToIgnoreCaseLocator("*", "title", title)).click();
            helper.waitForAjaxComplete();
        } catch (ElementNotVisibleException | NoSuchElementException e) {
            throw new NoSuchElementException(String.format("The item with title '%s' is missed in drop down '%s'", title, getBy()), e);
        }
    }

    @Override
    public void setByText(String text) {
        log.info(String.format("Select in dropdown option by text '%s'", text));
        expand();
        log.info(String.format("Type and select in dropdown filtered option by text '%s'", text));
        helper.type(input(), text);
        log.info(String.format("Number of items = '%d'", helper.getElements(getWrappedElement(), DROPDOWN_OPTION).size()));
        try {
            helper.getElement(getWrappedElement(), DROPDOWN_OPTION).click();
            helper.waitForAjaxComplete();
        } catch (ElementNotVisibleException | NoSuchElementException e) {
            throw new NoSuchElementException(String.format("The item with text '%s' is missed in drop down", text));
        }
    }

    public String getText() {
        return helper.getElement(getWrappedElement(), TEXT_VALUE).getText();
    }

    @Override
    public String getValue() {
        return button().getText().trim().replaceAll("\\s{2,}", " ");
    }

    protected WebElement input() {
        return helper.getElement(getWrappedElement(), ".//input[contains(@class, 'inputFilter')]");
    }

    protected WebElement button() {
        return helper.getElement(getWrappedElement(), ".//button");
    }

    protected WebElement clearButton() {
        return helper.getElement(getWrappedElement(), ".//button[@class='clearButton']");
    }
}
