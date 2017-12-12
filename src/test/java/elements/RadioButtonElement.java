package elements;

import org.openqa.selenium.WebElement;

import java.util.List;

import static elements.ElementsHelper.getTextContainsToIgnoreCaseLocator;
import static pages.locators.ElementLocators.RADIO_BUTTON;
import static pages.locators.ElementLocators.TEXT_VALUE;

/**
 * Created by kgr on 10/6/2016.
 */
public class RadioButtonElement extends SuperTypifiedElement {
    public RadioButtonElement(WebElement element) {
        super(element);
    }

    public void setByIndex(int index) {
        log.info(String.format("Select radio button by index '%s'", index));
        helper.getElement(getWrappedElement(), String.format(".//span[%d]", index)).click();
    }

    public List<WebElement> buttons() {
        return helper.getElements(getWrappedElement(), ".//span");
    }

    public String getText() {
        return helper.getElement(getWrappedElement(), TEXT_VALUE).getText();
    }

    public boolean hasSelectedButton() {
        for (WebElement button : buttons()) {
            if (button.getAttribute("class").contains("active")) return true;
        }
        log.error(String.format("There are no selected button with label '%s'", label));
        return false;
    }

    @Override
    public void setByText(String text) {
        log.info(String.format("Select radio button by text '%s'", text));
//        helper.getElement(getWrappedElement(), ".//span[text()='" + text + "']").click();
        helper.getElement(getWrappedElement(), getTextContainsToIgnoreCaseLocator("span", text)).click();
    }

    @Override
    public String getValue() {
        if (hasSelectedButton()) {
            return helper.getElement(getWrappedElement(), ".//span[contains(@class, 'active')]").getText();
        }
        return null;
    }
}
