package px.objects.payouts.pages;

import configuration.browser.PXDriver;
import elements.SuperTypifiedElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.payouts.PayoutTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.DashboardPageLocators.SAVE_BUTTON;
import static pages.locators.ElementLocators.CREATE_BUTTON;
import static px.objects.payouts.PayoutsPageLocators.*;
import static px.objects.publishers.PublishersPageLocators.OFFER_PAYOUTS_CONTAINER;

/**
 * Created by kgr on 10/19/2016.
 */
public class CreatePayoutsPage extends ObjectPage implements Creatable {
    @FindBy(xpath = PAYOUT_INPUT)
    InputElement payoutInput;
    @FindBy(xpath = PERCENTAGE_PAYOUT_INPUT)
    InputElement percPayoutInput;
    @FindBy(xpath = REVENUE_INPUT)
    InputElement revenueInput;
    @FindBy(xpath = PERCENTAGE_REVENUE_INPUT)
    InputElement percRevenueInput;
    @FindBy(xpath = CONVERSION_CAP_INPUT)
    InputElement conversionCapInput;
    @FindBy(xpath = MONTHLY_CONVERSION_INPUT)
    InputElement monthlyConversionInput;
    @FindBy(xpath = PAYOUT_CAP_INPUT)
    InputElement payoutCapInput;
    @FindBy(xpath = MONTHLY_PAYOUT_INPUT)
    InputElement monthlyPayoutInput;
    @FindBy(xpath = REVENUE_CAP_INPUT)
    InputElement revenueCapInput;
    @FindBy(xpath = MONTHLY_REVENUE_INPUT)
    InputElement monthlyRevenueInput;
    @FindBy(xpath = PUBLISHER_ID_SELECT)
    SelectElement publisherIDSelect;
    @FindBy(xpath = OFFER_ID_SELECT)
    SelectElement offerIDSelect;

    public CreatePayoutsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreatePayoutsPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PayoutTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PayoutTestData testData = (PayoutTestData) pTestData;
        // try last cause publishers are loading too much time
        helper.waitUntilDisplayed(testData.isOfferPayout() ? BASIC_INFO_CONTAINER : OFFER_PAYOUTS_CONTAINER);
        payoutInput.setByText(testData.getPayout());
        percPayoutInput.setByText(testData.getPercentagePayout());
        revenueInput.setByText(testData.getRevenue());
        percRevenueInput.setByText(testData.getPercentageRevenue());
        conversionCapInput.setByText(testData.getConversionCap());
        monthlyConversionInput.setByText(testData.getMonthlyConversion());
        payoutCapInput.setByText(testData.getPayoutCap());
        monthlyPayoutInput.setByText(testData.getMonthlyPayout());
        revenueCapInput.setByText(testData.getRevenueCap());
        monthlyRevenueInput.setByText(testData.getMonthlyRevenue());
        return this;
    }

    @Override
    public CreatePayoutsPage checkDefaultValues() {
        log.info("Check fields with default values");
        helper.waitUntilDisplayed(BASIC_INFO_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Payout default value", payoutInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Percentage Payout default value", percPayoutInput.getValue(), equalTo("0 %"));
        hamcrest.assertThat("Revenue default value", revenueInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Percentage Revenue default value", percRevenueInput.getValue(), equalTo("0 %"));
        hamcrest.assertThat("Conversion Cap default value", conversionCapInput.getValue(), equalTo("0"));
        hamcrest.assertThat("Monthly Conversion Cap default value", monthlyConversionInput.getValue(), equalTo("0"));
        hamcrest.assertThat("Payout Cap default value", payoutCapInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Monthly Payout Cap default value", monthlyPayoutInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Revenue Cap default value", revenueCapInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Monthly Revenue Cap default value", monthlyRevenueInput.getValue(), containsString("0,00"));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public CreatePayoutsPage saveInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PayoutTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PayoutTestData testData = (PayoutTestData) pTestData;
        if (testData.isOfferPayout()) {
            publisherIDSelect.setByText(testData.getPublisherID() + " - " + testData.getPublisherName());
            helper.click(BASIC_INFO_CONTAINER + SAVE_BUTTON);
        } else if (testData.isPublisherPayout()) {
            offerIDSelect.setByText(testData.getOfferID() + " - " + testData.getOfferName());
            helper.click(OFFER_PAYOUTS_CONTAINER + CREATE_BUTTON);
            waitPageIsLoaded();
        }
        pxDriver.waitForAjaxComplete();
        if (testData.isPositive()) {
            checkErrorMessage();
            if (testData.isOfferPayout())
                helper.waitUntilToBeInvisible(BASIC_INFO_CONTAINER);
        } else {
            List<SuperTypifiedElement> elementsWithError = new ArrayList<>(Arrays.asList(payoutInput, percPayoutInput, revenueInput, percRevenueInput,
                    conversionCapInput, monthlyConversionInput, payoutCapInput, monthlyPayoutInput, revenueCapInput, monthlyRevenueInput));
            // check available or already filtered
            if (testData.isDuplicatedTargetID() && testData.isOfferPayout()) elementsWithError.add(publisherIDSelect);
            if (testData.isDuplicatedTargetID() && testData.isPublisherPayout()) elementsWithError.add(offerIDSelect);
            checkErrorMessage(elementsWithError);
        }
        return this;
    }
}