package px.objects.publishers.pages;

import configuration.browser.PXDriver;
import elements.RadioButtonElement;
import elements.SuperTypifiedElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.publishers.PublisherTestData;
import px.objects.subIds.SubIdTestData;
import px.objects.users.ContactTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static pages.locators.ContactInfoLocators.*;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by kgr on 10/19/2016.
 */
public class CreatePublishersPage extends ObjectPage implements Creatable {
    @FindBy(xpath = PUBLISHER_NAME_INPUT)
    protected InputElement nameInput;
    @FindBy(xpath = FULL_NAME_INPUT)
    private InputElement fullNameInput;
    @FindBy(xpath = ZIP_CODE_INPUT)
    InputElement zipCodeInput;
    // filled in
    @FindBy(xpath = MARGIN_INPUT)
    protected InputElement marginInput;
    @FindBy(xpath = MIN_PRICE_INPUT)
    protected InputElement minPriceInput;
    @FindBy(xpath = CHECK_PERCENTAGE_INPUT)
    protected InputElement checkPercentageInput;
    @FindBy(xpath = EMAIL_INPUT)
    protected InputElement emailInput;
    @FindBy(xpath = START_BALANCE_INPUT)
    protected InputElement startBalanceInput;
    @FindBy(xpath = MAX_NEGATIVE_BALANCE_INPUT)
    protected InputElement maxNegativeBalanceInput;
    @FindBy(xpath = FREE_TESTS_INPUT)
    protected InputElement freeTestsInput;
    @FindBy(xpath = USER_PASSWORD_INPUT)
    private InputElement passwordInput;

    @FindBy(xpath = PUBLISHER_TIER_SELECT)
    SelectElement publisherTierSelect;
    @FindBy(xpath = TYPE_SELECT)
    SelectElement typeSelect;
    @FindBy(xpath = ACCESS_MODE_SELECT)
    SelectElement modeSelect;
    @FindBy(xpath = LEADING_BALANCE_SELECT)
    SelectElement leadingBalanceSelect;
    @FindBy(xpath = PUBLISHER_MANAGER_ID_SELECT)
    private SelectElement managerSelect;

    @FindBy(xpath = PRICING_RADIO)
    RadioButtonElement pricingRadio;
    @FindBy(xpath = UPSELL_BALANCE_RADIO)
    RadioButtonElement upsellRadio;

    @FindBy(xpath = BID_FLOOR_PERCENTAGE_INPUT)
    protected InputElement bidFloor;
    @FindBy(xpath = OUTBID_PERCENTAGE_INPUT)
    protected InputElement outbidPercentage;
    @FindBy(xpath = OUTBID_PERCENTAGE_PERCENTAGE)
    protected InputElement outbidPercentagePerc;

    public CreatePublishersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreatePublishersPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PublisherTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PublisherTestData testData = (PublisherTestData) pTestData;
        helper.waitUntilDisplayed(GENERAL_INFO_CONTAINER);
        filInGeneralInfo(testData);
        filInContactInfo(testData);
        managerSelect.setByText(testData.getManagerID());
        return this;
    }

    protected CreatePublishersPage filInGeneralInfo(PublisherTestData testData) {
        // to get all field names
//        helper.getElements(GENERAL_INFO_CONTAINER + "//*[@data-field-name]").forEach(el -> log.info(el.getAttribute("data-field-name")));
        // sub id page does not have publisherName and publisherTier is unmodified
        if (!(testData instanceof SubIdTestData)) {
            nameInput.setByText(testData.getName());
            publisherTierSelect.setByText(testData.getTier());
        }
        typeSelect.setByText(testData.getType());
        modeSelect.setByText(testData.getAccessMode());
        // with default value
        marginInput.setByText(testData.getMargin());
        minPriceInput.setByText(testData.getMinPrice());
        checkPercentageInput.setByText(testData.getEscorePercentage());
        startBalanceInput.setByText(testData.getStartBalance());
        maxNegativeBalanceInput.setByText(testData.getMaxNegativeBalance());
        if (testData.isCreateMode() || testData instanceof SubIdTestData)
            freeTestsInput.setByText(testData.getFreeTests());
        // rest
        pricingRadio.setByText(testData.getFixedPricing());
        upsellRadio.setByText(testData.getUpsellBalance());
        leadingBalanceSelect.setByText(testData.getLeadingBalance());
        // ping post
        new SelectElement(helper.getElement(PING_POST_TYPE_SELECT)).setByText(testData.getPingPostType());
        new SelectElement(helper.getElement(BID_TYPE_SELECT)).setByText(testData.getBidType());
        new InputElement(helper.getElement(BID_FLOOR_PERCENTAGE_INPUT)).setByText(testData.getBidFloor());
        new InputElement(helper.getElement(OUTBID_PERCENTAGE_INPUT)).setByText(testData.getOutBidPerc());
        new InputElement(helper.getElement(OUTBID_PERCENTAGE_PERCENTAGE)).setByText(testData.getOutBidPercPerc());
        new SelectElement(helper.getElement(PING_TIMEOUT_SELECT)).setByText(testData.getPingTimeout());

        return this;
    }

    protected CreatePublishersPage filInContactInfo(PublisherTestData testData) {
        ContactTestData contactTestData = testData.getContactTestData();
        emailInput.setByText(contactTestData.getEmail());
        passwordInput.setByText(contactTestData.getPassword());
        fullNameInput.setByText(contactTestData.getFirstName(), contactTestData.getLastName());
        zipCodeInput.setByText(contactTestData.getZipCode());
        return this;
    }

    @Override
    public CreatePublishersPage checkDefaultValues() {
        log.info("Check fields with default values");
        helper.waitUntilDisplayed(GENERAL_INFO_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Margin default value", marginInput.getValue(), equalTo("85 %"));
        hamcrest.assertThat("Escore Check Percentage default value", checkPercentageInput.getValue(), equalTo("20 %"));
        hamcrest.assertThat("Min price default value", minPriceInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Start balance default value", startBalanceInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Max Negative Balance default value", maxNegativeBalanceInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Fixed pricing selected button", pricingRadio.getValue(), equalTo("No"));
        hamcrest.assertThat("Add upsell to balance selected button", upsellRadio.getValue(), equalTo("Yes"));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public CreatePublishersPage saveInstance(InstancesTestData instancesTestData) {
        saveInstance(GENERAL_INFO_CONTAINER);
        if (instancesTestData.isPositive()) {
            checkErrorMessage();
            helper.waitUntilToBeInvisible(GENERAL_INFO_CONTAINER);
        } else {
            List<SuperTypifiedElement> elementsWithError = Arrays.asList(nameInput, marginInput, minPriceInput,
                    checkPercentageInput, freeTestsInput, startBalanceInput,
                    fullNameInput, zipCodeInput, emailInput, passwordInput,
                    bidFloor, outbidPercentage, outbidPercentagePerc);
            checkErrorMessage(elementsWithError);
        }
        return this;
    }
}