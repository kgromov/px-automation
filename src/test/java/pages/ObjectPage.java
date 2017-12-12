package pages;

import configuration.browser.PXDriver;
import elements.ElementManager;
import elements.SuperTypifiedElement;
import elements.menu.ListMenuElement;
import org.openqa.selenium.support.FindBy;
import pages.groups.Objectable;
import pages.locators.ElementLocators;
import px.objects.InstancesTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.DashboardPageLocators.*;
import static pages.locators.ElementLocators.DATA_FIELD_PARAMETERIZED_ELEMENT;

/**
 * Created by konstantin on 29.07.2017.
 */
public abstract class ObjectPage extends BasePage implements Objectable {
    @FindBy(xpath = ElementLocators.SETTING_MENU)
    protected ListMenuElement listMenuElement;

    public ObjectPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public ObjectPage navigateToItem(String optionText) {
        helper.waitUntilDisplayed(listMenuElement);
        listMenuElement.setByText(optionText);
        return this;
    }

    public ObjectPage navigateToItem(int optionIndex) {
        helper.waitUntilDisplayed(listMenuElement);
        listMenuElement.setByIndex(optionIndex);
        return this;
    }

    protected ObjectPage saveInstance(String containerLocator) {
        helper.click(containerLocator + SAVE_BUTTON);
        pxDriver.waitForAjaxComplete();
        return this;
    }

    @Override
    public Objectable saveInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Implementation is required in class " + this.getClass().getName());
    }

    public ObjectPage checkErrorMessage() {
        pxDriver.waitForAjaxComplete();
        if (helper.isElementPresent(ERROR_MESSAGE, 2)) {
            helper.scrollToElement(helper.getElement(ERROR_MESSAGE));
            try {
                pxDriver.printBrowserErrors();
            } catch (AssertionError e) {
                throw new Error("Console errors\n" + pxDriver.getLogEntryList());
            }
        }
        return this;
    }

    /*
    * 1) Grab all data-field name list from superSuperTypifiedElement list;
    * 2) Grab all errors on page (grab their preceding-sibling data-field-name);
    * 3) removeAll superSuperTypifiedElement data-field.
    * 3) Loop by errors, if preceding-sibling is not present in superSuperTypifiedElement - log error with label and data-field-name
     */
    public ObjectPage checkErrorMessage(List<SuperTypifiedElement> elementsWithError) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<String> actualFieldsWithErrors = helper.getElements(ELEMENT_WITH_ERROR_MESSAGE).stream()
                .map(element -> element.getAttribute("data-field-name")).collect(Collectors.toList());
        // missed errors
        for (SuperTypifiedElement element : elementsWithError) {
            // refresh element by proper type
            element = ElementManager.getElement(element);
            if (!helper.isElementPresent(helper.getElement(element.getWrappedElement(), ".."), ERROR_MESSAGE)) {
                hamcrest.assertThat(String.format("Error is missed for element with label '%s'\tEntered value = '%s'",
                        element.getLabel(), element.getValue()), false);
            } else {
                actualFieldsWithErrors.remove(element.getAttribute("data-field-name"));
            }
        }
        // extra errors
        actualFieldsWithErrors.forEach(extraError -> {
            SuperTypifiedElement element = ElementManager.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, extraError));
            hamcrest.assertThat(String.format("Unexpected error for element with label '%s'\tEntered value = '%s'",
                    element.getLabel(), element.getValue()), false);
        });
        hamcrest.assertAll();
        return this;
    }

    public ObjectPage checkErrorMessageByLocators(List<String> expectedFieldsWithErrors) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<String> actualFieldsWithErrors = helper.getElements(ELEMENT_WITH_ERROR_MESSAGE).stream()
                .map(element -> element.getAttribute("data-field-name")).collect(Collectors.toList());
        // locator -> field name
        expectedFieldsWithErrors = expectedFieldsWithErrors.stream().map(by ->
                by.substring(by.indexOf("'") + 1, by.lastIndexOf("'"))).collect(Collectors.toList());
        List<String> temp = new ArrayList<>(expectedFieldsWithErrors);
        // missed errors
        expectedFieldsWithErrors.removeAll(actualFieldsWithErrors);
        expectedFieldsWithErrors.forEach(missedError -> {
            SuperTypifiedElement element = ElementManager.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, missedError));
            hamcrest.assertThat(String.format("Error is missed for element with label '%s'\tEntered value = '%s'",
                    element.getLabel(), element.getValue()), false);
        });
        // extra errors
        actualFieldsWithErrors.removeAll(temp);
        actualFieldsWithErrors.forEach(extraError -> {
            SuperTypifiedElement element = ElementManager.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, extraError));
            hamcrest.assertThat(String.format("Unexpected error for element with label '%s'\tEntered value = '%s'",
                    element.getLabel(), element.getValue()), false);
        });
        hamcrest.assertAll();
        return this;
    }
}