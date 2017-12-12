package px.objects.brokers.pages;

import configuration.browser.PXDriver;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.brokers.BrokerTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static pages.locators.LoginPageLocators.LOGGED_USER_NAME;
import static px.objects.brokers.BrokersPageLocators.*;

/**
 * Created by kgr on 10/19/2016.
 */
public class CreateBrokersPage extends ObjectPage implements Creatable {
    @FindBy(xpath = BROKER_NAME_INPUT)
    protected InputElement nameInput;
    @FindBy(xpath = DESCRIPTION_INPUT)
    protected InputElement descriptionInput;
    @FindBy(xpath = BROKER_TYPE_SELECT)
    protected SelectElement brokerTypeSelect;

    public CreateBrokersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateBrokersPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof BrokerTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        BrokerTestData testData = (BrokerTestData) pTestData;
        log.info(String.format("Create broker '%s'", testData.getName()));
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        nameInput.setByText(testData.getName());
        descriptionInput.setByText(testData.getDescription());
        // workaround for overlapped tooltip (reproduced on staging only probably cause of screen resolution)
        helper.getElement(LOGGED_USER_NAME).click();
        brokerTypeSelect.setByText(testData.getBrokerType());
        return this;
    }

    @Override
    public CreateBrokersPage checkDefaultValues() {
        log.info("Check fields with default values");
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Name default value is empty", nameInput.getValue(), isEmptyOrNullString());
        hamcrest.assertThat("Description default value is empty", descriptionInput.getValue(), isEmptyOrNullString());
        hamcrest.assertAll();
        return this;
    }

    @Override
    public CreateBrokersPage saveInstance(InstancesTestData testData) {
        saveInstance(GENERAL_CONTAINER);
        if (testData.isPositive()) {
            checkErrorMessage();
            helper.waitUntilToBeInvisible(GENERAL_CONTAINER);
        } else checkErrorMessage(Arrays.asList(nameInput, descriptionInput));
        return this;
    }
}
