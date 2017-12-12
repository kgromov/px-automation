package elements.input;

import org.openqa.selenium.NoSuchContextException;
import org.openqa.selenium.WebElement;

/**
 * Created by kgr on 10/12/2016.
 */
public class TextAreaElement extends InputElement {

    public TextAreaElement(WebElement element) {
        super(element);
    }

    @Override
    public WebElement getInput() {
        try {
            return helper.getElement(getWrappedElement(), ".//textarea");
        } catch (NoSuchContextException e) {
            throw new NoSuchContextException("No textarea element inside " + getBy());
        }
    }
}