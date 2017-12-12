package px.objects.buyers.pages;

import configuration.browser.PXDriver;
import elements.SuperTypifiedElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.ContactInfoPage;
import px.objects.InstancesTestData;
import px.objects.buyers.BuyerTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static pages.locators.ContactInfoLocators.*;
import static pages.locators.ElementLocators.INPUT_CONTAINER;
import static px.objects.buyers.BuyersPageLocators.*;
import static px.objects.campaigns.CampaignsPageLocators.MONTHLY_CAP_INPUT;
import static px.objects.publishers.PublishersPageLocators.STATUS_SELECT;

/**
 * Created by kgr on 10/19/2016.
 */
public class CreateBuyersPage extends ObjectPage implements Creatable {
    @FindBy(xpath = BUYER_NAME_INPUT)
    InputElement nameInput;
    @FindBy(xpath = FIRST_NAME_INPUT)
    InputElement firstNameInput;
    @FindBy(xpath = LAST_NAME_INPUT)
    InputElement lastNameInput;
    @FindBy(xpath = FUNCTION_TITLE_INPUT)
    InputElement functionInput;
    @FindBy(xpath = MOBILE_PHONE_INPUT)
    InputElement mobilePhoneInput;
    @FindBy(xpath = BUSINESS_PHONE_INPUT)
    InputElement businessPhoneInput;
    @FindBy(xpath = INTERNAL_EMAIL_INPUT)
    InputElement emailInput;
    @FindBy(xpath = WEB_INPUT)
    InputElement webInput;
    @FindBy(xpath = CREDIT_LIMITS_INPUT)
    InputElement creditLimitsInput;
    @FindBy(xpath = PAYMENT_TERMS_SELECT)
    SelectElement paymentTermsSelect;
    @FindBy(xpath = ADDITIONAL_INFO_SELECT)
    SelectElement contactInfoSelect;

    @FindBy(xpath = STREET_INPUT)
    InputElement streetInput;
    @FindBy(xpath = STREET_NUMBER_INPUT)
    InputElement streetNumberInput;
    @FindBy(xpath = CITY_INPUT)
    InputElement cityInput;
    @FindBy(xpath = ZIP_CODE_INPUT)
    InputElement zipCodeInput;
    @FindBy(xpath = STATE_SELECT)
    SelectElement stateSelect;
    @FindBy(xpath = COUNTRY_SELECT)
    SelectElement countrySelect;

    @FindBy(xpath = CUSTOMER_MANAGERS_SELECT)
    SelectElement customerManagerSelect;
    @FindBy(xpath = SALES_MANAGERS_SELECT)
    SelectElement salesManagerSelect;

    @FindBy(xpath = MONTHLY_CAP_INPUT)
    InputElement monthlyCapInput;

    public CreateBuyersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateBuyersPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof BuyerTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        BuyerTestData testData = (BuyerTestData) pTestData;
        // contact info
        helper.waitUntilDisplayed(CONTACT_INFO_CONTAINER);
        nameInput.setByText(testData.getName());
        if (testData.isEditMode()) {
            monthlyCapInput.setByText(testData.getMonthlyCap());
            new SelectElement(helper.getElement(STATUS_SELECT)).setByText(testData.getStatus());
        }
        ContactInfoPage contactInfoPage = new ContactInfoPage(pxDriver);
        // personal data
        contactInfoPage.fillInName(testData.getContactTestData());
        contactInfoPage.fillInPhones(testData.getContactTestData());
        emailInput.setByText(testData.getContactTestData().getEmail());
        webInput.setByText(testData.getWeb());
        creditLimitsInput.setByText(testData.getCreditLimits());
        // use only for edit to close popup
        testData.setPreviousPaymentTerms(paymentTermsSelect.getValue());
        paymentTermsSelect.setByText(testData.getPaymentTerms());
        // some strange request /api/buyerinfo/contactitem?contactInfoId=&parentObjectId=
        // with Request Method:OPTIONS
       /* contactInfoSelect.setByText(testData.getAdditionalContactInfo());
        // click on 'Add Additional info'
        contactInfoSelect.click();*/
        // address
        listMenuElement.setByText(COMPANY_ADDRESS_ITEM);
        contactInfoPage.fillInAddress(testData.getContactTestData());
        new SelectElement(helper.getElement(STATE_SELECT)).setByText(testData.getContactTestData().getState());
        countrySelect.setByText(testData.getCountry());
        // managers
        listMenuElement.setByText(MANAGER_SELECTION_ITEM);
        customerManagerSelect.setByText(testData.getCustomerManager());
        salesManagerSelect.setByText(testData.getSalesManager());
        return this;
    }

    @Override
    public CreateBuyersPage checkDefaultValues() {
        log.info("Check fields with default values");
        helper.waitUntilDisplayed(CONTACT_INFO_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // inputs inside
        for (WebElement element : helper.getElements(INPUT_CONTAINER)) {
            InputElement inputElement = new InputElement(element);
            hamcrest.assertThat(String.format("Default value is empty for input '%s'",
                    inputElement.getLabel()), inputElement.getValue(), isEmptyOrNullString());
        }
        hamcrest.assertAll();
        return this;
    }

    @Override
    public CreateBuyersPage saveInstance(InstancesTestData testData) {
        saveInstance(MANAGERS_CONTAINER);
        waitPageIsLoaded();
        if (testData.isPositive()) {
            checkErrorMessage();
            if (testData.isCreateMode())
                helper.waitUntilToBeInvisible(MANAGERS_CONTAINER);
        } else {
            List<SuperTypifiedElement> errorElements = new ArrayList<>(Arrays.asList(
                    nameInput, mobilePhoneInput, businessPhoneInput, emailInput,
                    webInput, creditLimitsInput, zipCodeInput, cityInput));
            if (testData.isEditMode())
                errorElements.add(monthlyCapInput);
            checkErrorMessage(errorElements);
        }
        return this;
    }
}
