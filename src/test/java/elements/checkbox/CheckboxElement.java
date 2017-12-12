package elements.checkbox;

import elements.SuperTypifiedElement;
import org.openqa.selenium.WebElement;

/**
 * Created by kgr on 10/7/2016.
 */
public class CheckboxElement extends SuperTypifiedElement {

    public CheckboxElement(WebElement wrappedElement) {
        super(wrappedElement);
    }

    public String getText() {
        return helper.getElement(getWrappedElement(), ".//span").getText();
    }

    public boolean isChecked() {
        return Boolean.parseBoolean(helper.getElement(getWrappedElement(), ".//input[@type = 'checkbox']").getAttribute("checked"));
    }

    private WebElement getCheckboxByText(String text) {
        return helper.getElement(getWrappedElement(), ".//*[text()='" + text + "']");
    }

    public void setByText() {
//        log.info("Select checkbox with text: '" + getText() + "'");
        log.info("Select checkbox with text: '" + getWrappedElement().getText() + "'");
        helper.getElement(getWrappedElement(), ".//input[@type = 'checkbox']").click();
    }

    @Override
    public void setByText(String text) {
        log.info("Select checkbox with text: '" + text + "'");
        WebElement checkBoxText = getCheckboxByText(text);
        helper.getElement(checkBoxText, ".//input[@type = 'checkbox']").click();
    }
}
