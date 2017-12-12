package elements.checkbox;

import configuration.helpers.DataHelper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.SuperTypifiedElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.ElementLocators.CHECKBOX_CONTAINER;

/**
 * Created by kgr on 10/7/2016.
 */
public class MultipleCheckboxElement extends SuperTypifiedElement {
    private List<String> checkedTextList;
    @FindBy(xpath = ".")
    private CheckboxElement checkboxElement;

    @FindBy(xpath = CHECKBOX_CONTAINER)
    private List<CheckboxElement> checkboxElementList;

    public MultipleCheckboxElement(WebElement wrappedElement) {
        super(wrappedElement);
    }

    public void setByText(List<String> textList) {
        for (String text : textList) {
            checkboxElement.setByText(text);
        }
    }

    public void setByText(String text) {
        checkboxElementList.stream().filter(checkboxElement -> checkboxElement.getText().equals(text)).forEach(CheckboxElement::setByText);
    }

    public void setByText() {
        List<WebElement> checkBoxes = getCheckBoxes();
        int toCheckCount = DataHelper.getRandomInt(1, checkBoxes.size());
        checkedTextList = new ArrayList<>(toCheckCount);
        List<Integer> containerIndexList = DataHelper.getRandUniqueListFromRange(0, checkBoxes.size() - 1, toCheckCount);
        for (int containerIndex : containerIndexList) {
            checkBoxes.get(containerIndex).findElement(By.xpath("../input")).click();
            checkedTextList.add(checkBoxes.get(containerIndex).findElement(By.xpath("..")).getText());
        }
        log.info(String.format("Select checkboxes with text '%s'", checkedTextList));
    }

    public void setAll() {
        log.info("Select all checkboxes");
        getInputs().stream().filter(input -> !Boolean.parseBoolean(input.getAttribute("checked"))).forEach(WebElement::click);
    }

    public void setAll(List<WebElement> checkboxes) {
        log.info("Select specific checkboxes");
        ElementsHelper helper = HelperSingleton.getHelper();
        checkboxes.stream().filter(input -> !Boolean.parseBoolean(input.getAttribute("checked"))).forEach(WebElement::click);
    }

    public List<WebElement> getCheckBoxes() {
        setHelper();
        return helper.getElements(getWrappedElement(), ".//input[@type = 'checkbox']/../i");
    }

    public List<WebElement> getInputs() {
        setHelper();
        return helper.getElements(getWrappedElement(), ".//input[@type = 'checkbox']");
    }

    private List<WebElement> getChecked() {
        setHelper();
        return helper.getElements(getWrappedElement(), ".//input")
                .parallelStream()
                .filter(input -> Boolean.parseBoolean(input.getAttribute("checked")))
                .collect(Collectors.toList());
    }

    public boolean isAllItemsChecked() {
        setHelper();
        return !helper.getElements(getWrappedElement(), ".//input")
                .stream()
                .anyMatch(input -> !Boolean.parseBoolean(input.getAttribute("checked")));
//        return getCheckBoxes().size() == getChecked().size();
    }

    public boolean isAllItemsChecked(List<WebElement> checkboxes) {
        checkboxes.forEach(input -> log.info(String.format("'%s' checked = %s",
                input.getAttribute("data-field-name"), Boolean.parseBoolean(input.getAttribute("checked")))));
        return !checkboxes
                .stream()
                .anyMatch(input -> !Boolean.parseBoolean(input.getAttribute("checked")));
    }

    public List<String> getCheckedTextList() {
        return checkedTextList;
    }
}