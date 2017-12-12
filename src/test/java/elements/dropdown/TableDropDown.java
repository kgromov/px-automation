package elements.dropdown;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.ElementLocators.DROPDOWN_TABLE_CONTAINER;

/**
 * Created by kgr on 10/7/2016.
 */
@FindBy(xpath = DROPDOWN_TABLE_CONTAINER)
public class TableDropDown extends DropDownElement {

    public TableDropDown(WebElement element) {
        super(element);
        setOptionBy(By.xpath(".//a"));
    }

    @Override
    public List<String> getItems() {
        return options().stream().map(element -> element.getAttribute("textContent").trim()).collect(Collectors.toList());
    }
}