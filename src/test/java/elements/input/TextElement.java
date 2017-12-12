package elements.input;

import elements.SuperTypifiedElement;
import org.openqa.selenium.WebElement;

/**
 * Created by konstantin on 19.07.2017.
 */
public class TextElement extends SuperTypifiedElement {

    public TextElement(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public String getValue() {
        return helper.getElement(getWrappedElement(), ".//*[contains(@class, 'px-value')]").getText();
    }
}
