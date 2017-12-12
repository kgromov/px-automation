package px.objects.offers.pages;

import configuration.browser.PXDriver;
import configuration.helpers.DataHelper;
import elements.RadioButtonElement;
import elements.SuperTypifiedElement;
import elements.checkbox.MultipleCheckboxElement;
import elements.date.CalendarElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import elements.input.TextAreaElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import pages.groups.Objectable;
import px.objects.InstancesTestData;
import px.objects.offers.OfferSettingsEnum;
import px.objects.offers.OfferTestData;
import px.objects.offers.OffersPageLocators;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static px.objects.offers.OffersPageLocators.*;

/**
 * Created by kgr on 10/13/2016.
 */
public class CreateOffersPage extends ObjectPage implements Creatable {
    // general
    @FindBy(xpath = OFFER_NAME_INPUT)
    InputElement nameInput;
    @FindBy(xpath = DESCRIPTION)
    TextAreaElement descriptionInput;
    @FindBy(xpath = PREVIEW_URL_INPUT)
    InputElement previewURLInput;
    //    @FindBy(xpath = GENERAL_CONTAINER + OFFER_URL_INPUT)
    @FindBy(xpath = "(" + OFFER_URL_INPUT + ")[1]")
    InputElement offerURLInput;
    @FindBy(xpath = EXPIRATION_DATE_INPUT)
    InputElement expirationDateInput;
    //    @FindBy(xpath =CALENDAR_DROPDOWN)
    CalendarElement calendarElement;
    @FindBy(xpath = REF_ID_INPUT)
    InputElement refIDInput;
    @FindBy(xpath = NOTE_INPUT)
    InputElement noteInput;
    // selects
    @FindBy(xpath = CONVERSION_TRACKING_SELECT)
    SelectElement conversionSelect;
    @FindBy(xpath = STATUS_SELECT)
    SelectElement statusSelect;
    @FindBy(xpath = OFFER_CATEGORIES_SELECT)
    SelectElement offerCategoriesSelect;
    @FindBy(xpath = CURRENCY_SELECT)
    SelectElement currencyElement;
    // payout
    @FindBy(xpath = PAYOUT_METHOD_RADIO)
    RadioButtonElement payoutMethodRadio;
    @FindBy(xpath = PAYOUT_TYPE_SELECT)
    SelectElement payoutTypeSelect;
    // revenue
    @FindBy(xpath = REVENUE_METHOD_RADIO)
    RadioButtonElement revenueMethodRadio;
    @FindBy(xpath = REVENUE_TYPE_SELECT)
    SelectElement revenueTypeSelect;
    // tracking
    @FindBy(xpath = TRACKING_DOMAIN_SELECT)
    SelectElement trackingDomainSelect;
    @FindBy(xpath = REDIRECT_OFFER_SELECT)
    SelectElement redirectOfferSelect;
    @FindBy(xpath = CONVERSION_CAP_INPUT)
    InputElement capInput;
    //    @FindBy(xpath = TRACKING_CONTAINER)
    @FindBy(xpath = ".//*")
    MultipleCheckboxElement multipleCheckboxElement;
    @FindBy(xpath = START_SESSION_RADIO)
    RadioButtonElement sessionRadio;
    @FindBy(xpath = SESSION_HOURS_SELECT)
    SelectElement sessionHoursSelect;
    @FindBy(xpath = SESSION_IMPRESSION_HOURS_SELECT)
    SelectElement sessionImpressionHoursSelect;
    @FindBy(xpath = CUSTOM_SESSION_HOURS_INPUT)
    InputElement customHoursInput;
    @FindBy(xpath = CUSTOM_SESSION_IMPRESSION_HOURS_INPUT)
    InputElement customImpressionHoursInput;
    @FindBy(xpath = SECONDARY_OFFER_SELECT)
    SelectElement secondaryOfferSelect;
    @FindBy(xpath = NETWORK_OFFER_SELECT)
    SelectElement networkOfferSelect;
    //    @FindBy(xpath = TRACKING_CONTAINER + OFFER_URL_INPUT)
    @FindBy(xpath = SECONDARY_OFFER_URL_INPUT)
    InputElement offerTrackingURLInput;

    public CreateOffersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateOffersPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof OfferTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OfferTestData testData = (OfferTestData) pTestData;
        // general info
        if (testData.isCreateMode())
            helper.waitUntilDisplayed(OffersPageLocators.GENERAL_CONTAINER);
        nameInput.setByText(testData.getName());
        descriptionInput.setByText(testData.getDescription());
        previewURLInput.setByText(testData.getPreviewURL());
        offerURLInput.setByText(testData.getOfferURL());
        conversionSelect.setByText(testData.getProtocol());
        statusSelect.setByText(testData.getStatus());
        offerCategoriesSelect.setByText(testData.getOfferCategories());
        offerCategoriesSelect.expand();
        refIDInput.setByText(testData.getRefID());
        noteInput.setByText(testData.getNote());
        currencyElement.setByText(testData.getCurrency());
        if (testData.isPositive()) {
            expirationDateInput.getInput().click();
            calendarElement.setDate(testData.getExpirationDate());
            calendarElement.setTime(testData.getExpirationDate());
        }
        // payout
        if (testData.isCreateMode())
            listMenuElement.setByText(OfferSettingsEnum.PAYOUT.getValue());
        payoutTypeSelect.setByText(testData.getPayoutType());
        payoutMethodRadio.setByText(testData.getPayoutMethod());
        // dependent dynamic title
        if (testData.isPercentagePayout()) {
            if (testData.isDefaultPayoutMethod()) {
                new InputElement(helper.getElement(PERC_PAYOUT_INPUT)).setByText(testData.getDefaultPayout());
            } else {
                helper.waitUntilDisplayed(TIER_1_PERC_PAYOUT_INPUT);
                new InputElement(helper.getElement(TIER_1_PERC_PAYOUT_INPUT)).setByText(testData.getDefaultTier1Payout());
                new InputElement(helper.getElement(TIER_2_PERC_PAYOUT_INPUT)).setByText(testData.getDefaultTier2Payout());
                new InputElement(helper.getElement(TIER_3_PERC_PAYOUT_INPUT)).setByText(testData.getDefaultTier3Payout());
            }
        }
        if (testData.isConversionPayout()) {
            if (testData.isDefaultPayoutMethod()) {
                new InputElement(helper.getElement(DEFAULT_PAYOUT_INPUT)).setByText(testData.getDefaultPayout());
            } else {
                helper.waitUntilDisplayed(TIER_1_PAYOUT_INPUT);
                new InputElement(helper.getElement(TIER_1_PAYOUT_INPUT)).setByText(testData.getDefaultTier1Payout());
                new InputElement(helper.getElement(TIER_2_PAYOUT_INPUT)).setByText(testData.getDefaultTier2Payout());
                new InputElement(helper.getElement(TIER_3_PAYOUT_INPUT)).setByText(testData.getDefaultTier3Payout());
            }
        }
        // revenue
        if (testData.isCreateMode())
            listMenuElement.setByText(OfferSettingsEnum.REVENUE.getValue());
        revenueTypeSelect.setByText(testData.getRevenueType());
        revenueMethodRadio.setByText(testData.getRevenueMethod());
        // dependent dynamic title
        if (testData.isPercentageRevenue()) {
            if (testData.isDefaultRevenueMethod()) {
                new InputElement(helper.getElement(MAX_PERC_PAYOUT_INPUT)).setByText(testData.getMaxPayout());
            } else {
                helper.waitUntilDisplayed(TIER_1_MAX_PERC_PAYOUT_INPUT);
                new InputElement(helper.getElement(TIER_1_MAX_PERC_PAYOUT_INPUT)).setByText(testData.getMaxTier1Payout());
                new InputElement(helper.getElement(TIER_2_MAX_PERC_PAYOUT_INPUT)).setByText(testData.getMaxTier2Payout());
                new InputElement(helper.getElement(TIER_3_MAX_PERC_PAYOUT_INPUT)).setByText(testData.getMaxTier3Payout());
            }
        }
        if (testData.isConversionRevenue()) {
            if (testData.isDefaultRevenueMethod()) {
                new InputElement(helper.getElement(MAX_PAYOUT_INPUT)).setByText(testData.getMaxPayout());
            } else {
                helper.waitUntilDisplayed(TIER_1_MAX_PAYOUT_INPUT);
                new InputElement(helper.getElement(TIER_1_MAX_PAYOUT_INPUT)).setByText(testData.getMaxTier1Payout());
                new InputElement(helper.getElement(TIER_2_MAX_PAYOUT_INPUT)).setByText(testData.getMaxTier2Payout());
                new InputElement(helper.getElement(TIER_3_MAX_PAYOUT_INPUT)).setByText(testData.getMaxTier3Payout());
            }
        }
        // tracking
        if (testData.isCreateMode())
            listMenuElement.setByText(OfferSettingsEnum.TRACKING.getValue());
        redirectOfferSelect.setByText(testData.getRedirectOffer());
        capInput.setByText(testData.getConversionCap());
        multipleCheckboxElement.setByText();
        sessionRadio.setByText(testData.getSessionTracking()); // dependent dynamic title
        if (!testData.isClicksSessionTracking()) {
            helper.waitUntilDisplayed(SESSION_IMPRESSION_HOURS_SELECT);
            sessionImpressionHoursSelect.setByText(testData.getSessionImpressionHours());
            if (testData.isSessionHourCustom(testData.getSessionImpressionHours())) {
                helper.waitUntilDisplayed(CUSTOM_SESSION_IMPRESSION_HOURS_INPUT);
                customImpressionHoursInput.setByText(testData.getCustomSessionImpressionHours());
            }
        }
        sessionHoursSelect.setByText(testData.getSessionHours());
        if (testData.isSessionHourCustom(testData.getSessionHours())) {
            helper.waitUntilDisplayed(CUSTOM_SESSION_HOURS_INPUT);
            customHoursInput.setByText(testData.getCustomSessionHours());
        }
        if (testData.isConversionTrackingByPixel()) {
            secondaryOfferSelect.setByText(testData.getSecondaryOffer());
            if (testData.isSecondaryOfferByName()) {
                helper.waitUntilDisplayed(networkOfferSelect.getWrappedElement());
                networkOfferSelect.setByText(testData.getOfferByName());
            } else {
                helper.waitUntilDisplayed(offerTrackingURLInput.getWrappedElement());
                offerTrackingURLInput.setByText(testData.getOfferByURL());
            }
        }
        return this;
    }

    @Override
    public Objectable saveInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof OfferTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OfferTestData testData = (OfferTestData) pTestData;
        saveInstance(testData.isCreateMode() ? TRACKING_CONTAINER : GENERAL_EDIT_CONTAINER);
        if (testData.isPositive()) {
            if (testData.isCreateMode())
                helper.waitUntilToBeInvisible(TRACKING_CONTAINER);
            checkErrorMessage();
        } else {
            List<SuperTypifiedElement> elementsWithError = new ArrayList<>(Arrays.asList(nameInput,
                    previewURLInput, offerURLInput, refIDInput, noteInput, capInput));
            // payout
            if (testData.isPercentagePayout()) {
                if (testData.isDefaultPayoutMethod()) {
                    elementsWithError.add(new InputElement(helper.getElement(PERC_PAYOUT_INPUT)));
                } else {
                    elementsWithError.add(new InputElement(helper.getElement(TIER_1_PERC_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_2_PERC_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_3_PERC_PAYOUT_INPUT)));
                }
            }
            if (testData.isConversionPayout()) {
                if (testData.isDefaultPayoutMethod()) {
                    elementsWithError.add(new InputElement(helper.getElement(DEFAULT_PAYOUT_INPUT)));
                } else {
                    elementsWithError.add(new InputElement(helper.getElement(TIER_1_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_2_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_3_PAYOUT_INPUT)));
                }
            }
            // revenue
            if (testData.isPercentageRevenue()) {
                if (testData.isDefaultRevenueMethod()) {
                    elementsWithError.add(new InputElement(helper.getElement(MAX_PERC_PAYOUT_INPUT)));
                } else {
                    elementsWithError.add(new InputElement(helper.getElement(TIER_1_MAX_PERC_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_2_MAX_PERC_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_3_MAX_PERC_PAYOUT_INPUT)));
                }
            }
            if (testData.isConversionRevenue()) {
                if (testData.isDefaultRevenueMethod()) {
                    elementsWithError.add(new InputElement(helper.getElement(MAX_PAYOUT_INPUT)));
                } else {
                    elementsWithError.add(new InputElement(helper.getElement(TIER_1_MAX_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_2_MAX_PAYOUT_INPUT)));
                    elementsWithError.add(new InputElement(helper.getElement(TIER_3_MAX_PAYOUT_INPUT)));
                }
            }
            if (!testData.isConversionTrackingByPixel() && !testData.isSecondaryOfferByName())
                elementsWithError.add(offerTrackingURLInput);
            if (testData.isSessionHourCustom(testData.getCustomSessionHours())) elementsWithError.add(customHoursInput);
            if (testData.isSessionHourCustom(testData.getCustomSessionImpressionHours()))
                elementsWithError.add(customImpressionHoursInput);
            checkErrorMessage(elementsWithError);
        }
        return this;
    }

    @Override
    public CreateOffersPage checkDefaultValues() {
        log.info("Check create offer page with default values");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        helper.waitUntilDisplayed(OffersPageLocators.GENERAL_CONTAINER);
        Date expirationDate = DataHelper.getDateByMonthOffset(1);
        hamcrest.assertThat("Expiration Date default value", expirationDateInput.getValue(),
                equalTo(DataHelper.getDateByFormatSimple(DataHelper.EXPIRATION_DATE_ONLY_PATTERN, expirationDate) + ", 12:00:00 am"));
        hamcrest.assertThat("Payout method default value", payoutMethodRadio.getValue(), equalToIgnoringCase("Default"));
//        hamcrest.assertThat("Default payout default value", new InputElement(helper.getElement(DEFAULT_PAYOUT_INPUT)).getValue(), equalTo("0,00 $"));
        hamcrest.assertThat("Default payout default value", new InputElement(helper.getElement(DEFAULT_PAYOUT_INPUT)).getValue(), containsString("0,00"));
        hamcrest.assertThat("Revenue method default value", revenueMethodRadio.getValue(), equalTo("Default"));
//        hamcrest.assertThat("Max payout default value", new InputElement(helper.getElement(MAX_PAYOUT_INPUT)).getValue(), equalTo("0,00 $"));
        hamcrest.assertThat("Max payout default value", new InputElement(helper.getElement(MAX_PAYOUT_INPUT)).getValue(), containsString("0,00"));
        hamcrest.assertThat("Tracking domain default value", trackingDomainSelect.getValue(), equalTo("Default"));
        hamcrest.assertThat("Start session tracking default value", sessionRadio.getValue(), equalTo("Clicks"));
        hamcrest.assertAll();
        return this;
    }
}