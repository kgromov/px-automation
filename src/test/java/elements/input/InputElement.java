package elements.input;

import elements.SuperTypifiedElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchContextException;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

import static pages.locators.ElementLocators.TEXT_VALUE;

/**
 * Created by kgr on 10/12/2016.
 */
public class InputElement extends SuperTypifiedElement {

    public InputElement(WebElement element) {
        super(element);
    }

    @Override
    public void setByText(String text) {
        log.info(String.format("Type '%s' into '%s' input text", text, getLabel()));
        helper.type(getInput(), text);
    }

    public void setByText(String... args) {
        log.info(String.format("Type '%s' into '%s' input text", Arrays.toString(args), getLabel()));
        helper.type(getInput(), StringUtils.join(args, " "));
    }

    @Override
    public String getValue() {
        return getInput().getAttribute("value");
    }

    @Override
    public String getText() {
        return helper.getElement(getWrappedElement(), TEXT_VALUE).getText();
    }

    public WebElement getInput() {
        try {
            return helper.getElement(getWrappedElement(), ".//input");
        } catch (NoSuchContextException e) {
            throw new NoSuchContextException("No input element inside " + getBy());
        }
    }
}