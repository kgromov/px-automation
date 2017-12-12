package elements.dropdown;

import configuration.helpers.HTMLHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static pages.locators.ElementLocators.MULTI_SELECT_DROPDOWN;

/**
 * Created by kgr on 10/7/2016.
 */
@FindBy(xpath = MULTI_SELECT_DROPDOWN)
public class MultipleChoicesDropDown extends DropDownElement {

    public MultipleChoicesDropDown(WebElement element) {
        super(element);
        setOptionBy(By.xpath(".//input"));
    }

    public void setByText(List<String> textList) {
        log.info(String.format("Select the following options in drop down\t %s", textList));
        options().stream().filter(option -> textList.contains(HTMLHelper.cleanHTML(
                helper.getElement(option, "..").getAttribute("innerHTML"))))
                .forEach(WebElement::click);
    }
}