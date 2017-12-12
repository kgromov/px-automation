package px.objects;

import configuration.browser.PXDriver;
import elements.ElementsHelper;
import elements.input.InputElement;
import px.objects.users.ContactTestData;

import static pages.locators.ContactInfoLocators.*;

/**
 * Created by kgr on 1/25/2017.
 */
public class ContactInfoPage {
    private PXDriver pxDriver;
    private ElementsHelper helper;

    public ContactInfoPage(PXDriver pxDriver) {
        this.pxDriver = pxDriver;
        this.helper = new ElementsHelper(pxDriver.getWrappedDriver(), pxDriver.getWait());
    }

    public void fillInAddress(ContactTestData testData) {
        new InputElement(helper.getElement(STREET_INPUT)).setByText(testData.getStreetAddress());
        new InputElement(helper.getElement(STREET_NUMBER_INPUT)).setByText(testData.getStreetNR());
        new InputElement(helper.getElement(CITY_INPUT)).setByText(testData.getCity());
        new InputElement(helper.getElement(ZIP_CODE_INPUT)).setByText(testData.getZipCode());
//        new SelectElement(helper.getElement(STATE_SELECT)).setByText(testData.getState());
    }

    public void fillInName(ContactTestData testData) {
        new InputElement(helper.getElement(FIRST_NAME_INPUT)).setByText(testData.getFirstName());
        new InputElement(helper.getElement(LAST_NAME_INPUT)).setByText(testData.getLastName());

    }

    public void fillInPhones(ContactTestData testData) {
        new InputElement(helper.getElement(MOBILE_PHONE_INPUT)).setByText(testData.getMobilePhone());
        new InputElement(helper.getElement(BUSINESS_PHONE_INPUT)).setByText(testData.getBusinessPhone());
    }

}
