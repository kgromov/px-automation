package elements;

import elements.checkbox.CheckboxElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import elements.input.TextAreaElement;
import elements.input.TextElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import static elements.ElementTypesEnum.*;
import static pages.locators.ElementLocators.INPUT_ELEMENT;

/**
 * Created by kgr on 10/27/2016.
 */
public class ElementManager {
    private static final Logger log = Logger.getLogger(ElementManager.class);

    public static SuperTypifiedElement getElement(SuperTypifiedElement element) {
        // refresh element
        ElementsHelper helper = HelperSingleton.getHelper();
        WebElement wrappedElement = helper.getElement(element.getBy());
        String classType = wrappedElement.getAttribute("class");
        log.info(String.format("DEBUG\tBy = '%s', class = '%s'", element.getBy(), classType));
        if (classType.contains(INPUT.getValue()) || helper.isElementPresent(wrappedElement, INPUT_ELEMENT)) {
            return new InputElement(wrappedElement);
        } else if (classType.contains(TEXT_AREA.getValue())) {
            return new TextAreaElement(wrappedElement);
        } else if (classType.contains(TEXT.getValue())) {
            return new TextElement(wrappedElement);
        } else if (classType.contains(SELECT.getValue())) {
            return new SelectElement(wrappedElement);
        } else if (classType.contains(RADIO_BUTTON.getValue())) {
            return new RadioButtonElement(wrappedElement);
        } else if (classType.contains(CHECKBOX.getValue())) {
            return new CheckboxElement(wrappedElement);
        }
        System.out.println(String.format("WARNING:\tUnknown element type - '%s'", classType));
        return element;
    }

    public static SuperTypifiedElement getElement(String by) {
        ElementsHelper helper = HelperSingleton.getHelper();
        WebElement element = helper.getElement(by);
        String classType = element.getAttribute("class");
        log.info(String.format("DEBUG\tBy = '%s', class = '%s'", by, classType));
        if (classType.contains(INPUT.getValue()) || helper.isElementPresent(element, INPUT_ELEMENT)) {
            return new InputElement(element);
        } else if (classType.contains(TEXT_AREA.getValue())) {
            return new TextAreaElement(element);
        } else if (classType.contains(TEXT.getValue())) {
            return new TextElement(element);
        } else if (classType.contains(SELECT.getValue())) {
            return new SelectElement(element);
        } else if (classType.contains(RADIO_BUTTON.getValue())) {
            return new RadioButtonElement(element);
        } else if (classType.contains(CHECKBOX.getValue())) {
            return new CheckboxElement(element);
        }
        throw new IllegalArgumentException(String.format("WARNING:\tUnknown element type - '%s'", classType));
    }
}
