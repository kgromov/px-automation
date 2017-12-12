package px.objects.payouts.pages;

import configuration.browser.PXDriver;
import elements.SuperTypifiedElement;
import elements.input.InputElement;
import org.json.JSONException;
import org.openqa.selenium.WebElement;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.payouts.PayoutTestData;
import px.reports.dto.FieldFormatObject;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static configuration.helpers.PXDataHelper.getMatherByMetricType;
import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.DashboardPageLocators.SAVE_BUTTON;
import static pages.locators.ElementLocators.DATA_FIELD_PARAMETERIZED_ELEMENT;
import static pages.locators.ElementLocators.UPDATE_BUTTON;
import static px.objects.payouts.PayoutsPageLocators.BASIC_INFO_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.OFFER_PAYOUTS_CONTAINER;

/**
 * Created by kgr on 10/19/2016.
 */
public class EditPayoutsPage extends CreatePayoutsPage implements Editable {

    public EditPayoutsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditPayoutsPage editInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PayoutTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PayoutTestData testData = (PayoutTestData) pTestData;
        String description = testData.isOfferPayout()
                ? String.format("Edit offer payout with publisherID = '%s'", testData.getPublisherID())
                : String.format("Edit publisher payout with offerID = '%s'", testData.getOfferID());
        log.info(description);
        if (testData.isOfferPayout()) editInstance(BASIC_INFO_CONTAINER);
        createInstance(testData);
        return this;
    }

    public EditPayoutsPage checkDefaultValues(InstancesTestData pTestData) {
        if (!(pTestData instanceof PayoutTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PayoutTestData testData = (PayoutTestData) pTestData;
        log.info("Check fields with default values");
        helper.waitUntilDisplayed(testData.isOfferPayout() ? BASIC_INFO_CONTAINER : OFFER_PAYOUTS_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<FieldFormatObject> fields = testData.getFields();
        // parent offer data, missed in json details
        PayoutTestData.getOfferFields().forEach(field -> {
//            helper.waitUntilDisplayed(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, field)));
            InputElement inputElement = new InputElement(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, field)));
            FieldFormatObject header = FieldFormatObject.getFieldObjectFromListByName(fields, field);
            try {
                String expectedValue = String.valueOf(testData.getOfferObject().get(field));
                hamcrest.assertThat(String.format("%s value verification", inputElement.getLabel()),
                        inputElement.getText(), getMatherByMetricType(header, expectedValue));
            } catch (JSONException e) {
                hamcrest.assertThat(String.format("Key '%s' is missed in offer details\t%s", field, testData.getOfferObject()), false);
            }
        });
        // payout details data
        PayoutTestData.getPayoutFields().forEach(field -> {
            InputElement inputElement = new InputElement(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, field)));
            FieldFormatObject header = FieldFormatObject.getFieldObjectFromListByName(fields, field);
            try {
                String expectedValue = String.valueOf(testData.getPayoutObject().get(field));
                hamcrest.assertThat(String.format("%s value verification", inputElement.getLabel()),
                        testData.isOfferPayout() ? inputElement.getText() : inputElement.getValue(),
                        getMatherByMetricType(header, expectedValue));
            } catch (JSONException e) {
//                hamcrest.assertThat(String.format("Key '%s' is missed in payout details\t%s", field, testData.getPayoutObject()), false);
                log.info(String.format("Key '%s' is missed in payout details\t%s", field, testData.getPayoutObject()));
            }
        });
        // cause value render specific
//        String expectedValue = testData.isOfferPayout() ? testData.getTargetID() + " - " + testData.getTargetName() : testData.getTargetID();
        WebElement targetElement = helper.getElement(testData.isOfferPayout() ? BASIC_INFO_CONTAINER : OFFER_PAYOUTS_CONTAINER,
                String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, testData.isOfferPayout() ? "publisherId" : "offerId"));
        hamcrest.assertThat("Target object verification", new InputElement(targetElement).getText(),
                equalTo(testData.getTargetID() + " - " + testData.getTargetName()));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("No implementation");
    }

    @Override
    public EditPayoutsPage saveInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PayoutTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PayoutTestData testData = (PayoutTestData) pTestData;
        if (testData.isOfferPayout()) {
            helper.click(BASIC_INFO_CONTAINER + SAVE_BUTTON);
        } else if (testData.isPublisherPayout()) {
            helper.click(OFFER_PAYOUTS_CONTAINER + UPDATE_BUTTON);
        }
        pxDriver.waitForAjaxComplete();
        if (testData.isPositive()) {
            checkErrorMessage();
        } else {
            List<SuperTypifiedElement> elementsWithError = new ArrayList<>(Arrays.asList(payoutInput, percPayoutInput, revenueInput, percRevenueInput,
                    conversionCapInput, monthlyConversionInput, payoutCapInput, monthlyPayoutInput, revenueCapInput, monthlyRevenueInput));
            checkErrorMessage(elementsWithError);
        }
        return this;
    }
}