package elements.menu;

import elements.HelperSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static elements.ElementsHelper.getTextContainsToIgnoreCaseLocator;

/**
 * Created by konstantin on 05.10.2016.
 */
public class ListMenuElement extends MenuElement {

    public ListMenuElement(WebElement element) {
        super(element);
    }

    public void setByIndex(int itemIndex) {
        log.info(String.format("Select menu item by index '%d'", itemIndex));
        WebElement item = helper.getElement(getWrappedElement(), "(.//li)[" + itemIndex + "]");
        log.info(String.format("Menu item text - '%s'", item.getText()));
        item.click();
    }

    public WebElement getItem(String item) {
//        return helper.getElement(getWrappedElement(), ".//li[contains(text(), '" + item + "')]");
        try {
            return helper.getElement(getWrappedElement(), getTextContainsToIgnoreCaseLocator("li", item));
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("No item with text '%s'", item));
        }

    }

    public WebElement getItemByIndex(int itemIndex) {
        return HelperSingleton.getHelper().getElement(getWrappedElement(), By.xpath("(.//li)[" + itemIndex + "]"));
    }

    public String getItemTextByIndex(int itemIndex) {
        return getItemByIndex(itemIndex).getAttribute("textContent");
    }

    public int getItemsCount() {
        return helper.getElements(getWrappedElement(), ".//li").size();
    }

    @Override
    public List<WebElement> items() {
        return helper.getElements(getWrappedElement(), ".//li");
    }
}
