package elements.menu;

import elements.HelperSingleton;
import elements.SuperTypifiedElement;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by konstantin on 05.10.2016.
 */
public class MenuElement extends SuperTypifiedElement {

    public MenuElement(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public void setByText(String text) {
        getItem(text).click();
    }

    public WebElement getItem(String item) {
        String xpath = ".//a[@href='/" + item + "/']";
        try {
            helper.waitUntilPresent(xpath);
            return helper.getElement(getWrappedElement(), xpath);
        } catch (NoSuchElementException | ElementNotVisibleException e) {
            throw new NoSuchElementException(String.format("Element by locator '%s' is not present or visible", xpath));
        }
    }

    public WebElement getSelectedItem() {
        for (WebElement item : items()) {
            if (item.getAttribute("class").contains("active")) {
                return item;
            }
        }
        return null;
    }

    public List<WebElement> items() {
        return HelperSingleton.getHelper().getElements(getWrappedElement(), ".//a[contains(@class, 'px-menu-')]");
    }

    public boolean hasSelectedItem() {
        for (WebElement item : items()) {
            if (item.getAttribute("class").contains("active")) {
                return true;
            }
        }
        return false;
    }
}
