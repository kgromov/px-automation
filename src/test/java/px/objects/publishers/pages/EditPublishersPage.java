package px.objects.publishers.pages;

import configuration.browser.PXDriver;
import configuration.helpers.DataHelper;
import elements.RadioButtonElement;
import elements.dropdown.DropDownElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.groups.Editable;
import px.objects.ContactInfoPage;
import px.objects.InstancesTestData;
import px.objects.publishers.PublisherTestData;
import px.objects.users.ContactTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.ContactInfoLocators.*;
import static px.objects.brokers.BrokersPageLocators.DESCRIPTION_INPUT;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by kgr on 10/19/2016.
 */
public class EditPublishersPage extends CreatePublishersPage implements Editable {
    // general info section
    @FindBy(xpath = DESCRIPTION_INPUT)
    private InputElement descriptionInput;
    @FindBy(xpath = SPENDING_INPUT)
    protected InputElement spending;
    @FindBy(xpath = QUALITY_SCORE_INPUT)
    protected InputElement qualityScore;
    @FindBy(xpath = LEAD_PERCENTAGE_INPUT)
    protected InputElement leadPercentage;
    @FindBy(xpath = FRAUD_PERCENTAGE_INPUT)
    protected InputElement fraudPercentage;
    @FindBy(xpath = ACCOUNT_MANAGER_ID_SELECT)
    private SelectElement managerSelect;
    @FindBy(xpath = STATUS_SELECT)
    protected SelectElement status;
    // contact info
    @FindBy(xpath = FIRST_NAME_INPUT)
    private InputElement firstNameInput;
    @FindBy(xpath = LAST_NAME_INPUT)
    private InputElement lastNameInput;
    @FindBy(xpath = INTERNAL_EMAIL_INPUT)
    private InputElement internalEmailInput;
    @FindBy(xpath = GENDER_RADIO)
    private RadioButtonElement genderRadio;

    public EditPublishersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public EditPublishersPage editInstance(InstancesTestData oldData, InstancesTestData newData) {
        if (!(oldData instanceof PublisherTestData) || !(newData instanceof PublisherTestData))
            throw new IllegalArgumentException(String.format("Illegal test data class(es) - %s, %s",
                    oldData.getClass().getName(), newData.getClass().getName()));
        PublisherTestData testData2 = (PublisherTestData) newData;
        log.info(String.format("Edit publisher '%s'", oldData.getName()));
        // edit general info
        editGeneralInfo(testData2);
        // edit tracking data
        editTrackingData(testData2);
        // edit contact info
        editContactInfo(testData2);
        // edit address information
        editAddressInfo(testData2);
        return this;
    }

    public EditPublishersPage editGeneralInfo(PublisherTestData testData) {
        log.info("Edit 'General' data");
        editInstance(GENERAL_INFO_CONTAINER);
        // to get all field names
//        helper.getElements(GENERAL_INFO_CONTAINER + "//*[@data-field-name]").forEach(el -> log.info(el.getAttribute("data-field-name")));
        // filling in
        descriptionInput.setByText(testData.getDescription());
        super.filInGeneralInfo(testData);
        WebElement parentContainer = helper.getElement(GENERAL_INFO_CONTAINER);
        new InputElement(helper.getElement(parentContainer, SPENDING_INPUT)).setByText(testData.getSpending());
        new InputElement(helper.getElement(parentContainer, QUALITY_SCORE_INPUT)).setByText(testData.getQualityScore());
        new DropDownElement(helper.getElement(parentContainer, LEAD_PERCENTAGE_INPUT)).setByTitle(testData.getLeadPercentageType());
        helper.type(helper.getElement(LEAD_PERCENTAGE_INPUT, ".//input"), testData.getLeadPercentage());
//        new InputElement(helper.getElement(parentContainer, LEAD_PERCENTAGE_INPUT)).setByText(testData.getLeadPercentage());
        new InputElement(helper.getElement(parentContainer, FRAUD_PERCENTAGE_INPUT)).setByText(testData.getFraudPercentage());
        new SelectElement(helper.getElement(parentContainer, STATUS_SELECT)).setByText(testData.getStatus());
        // save filled data
        saveInstance(GENERAL_INFO_CONTAINER);
        // set error containers list
        if (!testData.isPositive()) {
            checkErrorMessageByLocators(Arrays.asList(PUBLISHER_NAME_INPUT, MARGIN_INPUT,
                    MIN_PRICE_INPUT, CHECK_PERCENTAGE_INPUT, START_BALANCE_INPUT,
                    DESCRIPTION_INPUT, SPENDING_INPUT, QUALITY_SCORE_INPUT,
                    LEAD_PERCENTAGE_INPUT, FRAUD_PERCENTAGE_INPUT,
                    OUTBID_PERCENTAGE_INPUT, BID_FLOOR_PERCENTAGE_INPUT, OUTBID_PERCENTAGE_PERCENTAGE
            ));
        } else checkErrorMessage();
        return this;
    }

    public EditPublishersPage editContactInfo(PublisherTestData testData) {
        log.info("Edit 'Contact' info");
        editInstance(CONTACT_INFO_CONTAINER);
        // filling in
        ContactTestData contactTestData = testData.getContactTestData();
        WebElement parentContainer = helper.getElement(CONTACT_INFO_CONTAINER);
        firstNameInput.setByText(contactTestData.getFirstName());
        new InputElement(helper.getElement(parentContainer, MIDDLE_NAME_INPUT)).setByText(contactTestData.getMiddleName());
        lastNameInput.setByText(contactTestData.getLastName());
        new InputElement(helper.getElement(parentContainer, INITIALS_INPUT)).setByText(contactTestData.getInitials());
        new InputElement(helper.getElement(parentContainer, BIRTH_DATE_INPUT)).setByText(contactTestData.getBirthDate());
        new InputElement(helper.getElement(parentContainer, COMPANY_NAME_INPUT)).setByText(testData.getCompanyName());
        emailInput.setByText(contactTestData.getEmail());
        genderRadio.setByText(contactTestData.getGender());
        new SelectElement(helper.getElement(parentContainer, MARITAL_STATUS_SELECT)).setByText(testData.getMaritalStatus());
        /*// find data at first
        new SelectElement(helper.getElement(parentContainer, ADDITIONAL_INFO_SELECT)).setByText(testData.getAdditionalContactInfo());
        // all these fields are filtered by additionalInfo
        new InputElement(helper.getElement(parentContainer, MOBILE_PHONE_INPUT)).setByText(testData.getMobilePhone());
        new InputElement(helper.getElement(parentContainer, BUSINESS_PHONE_INPUT)).setByText(testData.getBusinessPhone());
        new InputElement(helper.getElement(parentContainer, INTERNAL_EMAIL_INPUT)).setByText(testData.getInternalEmail());
        new InputElement(helper.getElement(parentContainer, WEB_INPUT)).setByText(testData.getWebSite());
        new SelectElement(helper.getElement(parentContainer, BILLING_CYCLE_SELECT)).setByText(testData.getBillingCycle());
        new SelectElement(helper.getElement(parentContainer, PAYMENT_TERMS_SELECT)).setByText(testData.getPaymentTerms());*/
        // save filled data
        saveInstance(CONTACT_INFO_CONTAINER);
        // set error containers list
        if (!testData.isPositive()) {
            checkErrorMessageByLocators(Arrays.asList(
                    FIRST_NAME_INPUT, LAST_NAME_INPUT, EMAIL_INPUT, MIDDLE_NAME_INPUT,
                    INITIALS_INPUT, BIRTH_DATE_INPUT, COMPANY_NAME_INPUT
            ));
        } else checkErrorMessage();
        return this;
    }

    public EditPublishersPage editTrackingData(PublisherTestData testData) {
        log.info("Edit 'Tracking data'");
        listMenuElement.setByText(TRACKING_DATA_ITEM);
        editInstance(TRACKING_DATA_CONTAINER);
        waitPageIsLoaded();
        managerSelect.setByText(testData.getManagerID());
        saveInstance(TRACKING_DATA_CONTAINER);
        checkErrorMessage();
        return this;
    }

    public EditPublishersPage editAddressInfo(PublisherTestData testData) {
        log.info("Edit 'Address' data");
        listMenuElement.setByText(ADDRESS_INFO_ITEM);
        editInstance(ADDRESS_INFO_CONTAINER);
        // filling in address
        new ContactInfoPage(pxDriver).fillInAddress(testData.getContactTestData());
        new InputElement(helper.getElement(STATE_SELECT)).setByText(testData.getContactTestData().getState());
        new InputElement(helper.getElement(COUNTRY_SELECT)).setByText(testData.getCountry());
        new InputElement(helper.getElement(EXTRA_INFO_INPUT)).setByText(testData.getExtraInfo());
        saveInstance(ADDRESS_INFO_CONTAINER);
        helper.pause(1000);
        if (testData.isPositive()) checkErrorMessage();
        else checkErrorMessage(Collections.singletonList(zipCodeInput));
        return this;
    }

    @Override
    public EditPublishersPage checkDefaultValues(InstancesTestData pTestData) {
        if (!(pTestData instanceof PublisherTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PublisherTestData testData = (PublisherTestData) pTestData;
        log.info("Check fields with created publisher values");
        helper.waitUntilDisplayed(GENERAL_INFO_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Name verification", nameInput.getText(), equalTo(testData.getName()));
        hamcrest.assertThat("Margin verification", DataHelper.getRoundedFloat(marginInput.getText()), equalTo(testData.getMargin()));
        hamcrest.assertThat("Escore Check Percentage verification", DataHelper.getRoundedFloat(checkPercentageInput.getText()), equalTo(testData.getEscorePercentage()));
        hamcrest.assertThat("Fixed pricing selected button", pricingRadio.getText(), equalTo(testData.getFixedPricing()));
        hamcrest.assertThat("Add upsell to balance selected button", upsellRadio.getText(), equalTo(testData.getUpsellBalance()));
        hamcrest.assertThat("Tier verification", publisherTierSelect.getText(), equalTo(testData.getTier()));
        hamcrest.assertThat("Type verification", typeSelect.getText(), equalTo(testData.getType()));
        hamcrest.assertThat("Access mode verification", modeSelect.getText(), equalTo(testData.getAccessMode()));
        hamcrest.assertAll();
        return this;
    }


    @Override
    public Editable editInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}