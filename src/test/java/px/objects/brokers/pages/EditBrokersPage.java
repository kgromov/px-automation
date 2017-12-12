package px.objects.brokers.pages;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.brokers.BrokerTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static px.objects.brokers.BrokersPageLocators.*;

/**
 * Created by kgr on 10/19/2016.
 */
public class EditBrokersPage extends CreateBrokersPage implements Editable {
    @FindBy(xpath = DAILY_CAP_INPUT)
    private InputElement dailyCapInput;
    @FindBy(xpath = BROKER_TYPE_SELECT)
    private InputElement brokerTypeInput;

    public EditBrokersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public EditBrokersPage editInstance(InstancesTestData oldData, InstancesTestData newData) {
        if (!(oldData instanceof BrokerTestData) || !(newData instanceof BrokerTestData))
            throw new IllegalArgumentException(String.format("Illegal test data class(es) - %s, %s",
                    oldData.getClass().getName(), newData.getClass().getName()));
        BrokerTestData testData1 = (BrokerTestData) oldData;
        BrokerTestData testData2 = (BrokerTestData) newData;
        log.info(String.format("Edit broker '%s'", testData1.getName()));
        editInstance(GENERAL_CONTAINER);
        log.info(String.format("Update description from '%s' to '%s'", testData1.getDescription(), testData2.getDescription()));
        descriptionInput.setByText(testData2.getDescription());
        log.info(String.format("Update description from '%s' to '%s'", testData1.getBrokerType(), testData2.getBrokerType()));
        brokerTypeSelect.setByText(testData2.getBrokerType());
        dailyCapInput.setByText(testData2.getDailyCapacity());
        return this;
    }

    @Override
    public EditBrokersPage checkDefaultValues(InstancesTestData pTestData) {
        if (!(pTestData instanceof BrokerTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        BrokerTestData testData = (BrokerTestData) pTestData;
        log.info("Check fields with created broker values");
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Name verification", nameInput.getText(), equalTo(testData.getName()));
        hamcrest.assertThat("Description verification", descriptionInput.getText(), equalTo(testData.getDescription()));
//        hamcrest.assertThat("Broker type verification", brokerTypeInput.getText(), equalTo(testData.getBrokerType()));
        hamcrest.assertThat("Daily cap verification", dailyCapInput.getText(), equalTo(testData.getDailyCapacity()));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public EditBrokersPage saveInstance(InstancesTestData testData) {
        saveInstance(GENERAL_CONTAINER);
        if (testData.isPositive()) checkErrorMessage();
        else checkErrorMessage(Arrays.asList(dailyCapInput, descriptionInput));
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("No implementation" +
                "\teditInstance(InstancesTestData oldData, InstancesTestData newData) supported");
    }
}