package px.objects.offers.pages;

import configuration.browser.PXDriver;
import elements.RadioButtonElement;
import elements.dropdown.SelectElement;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.offers.OfferTestData;
import utils.SoftAssertionHamcrest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.ElementLocators.CREATE_TOGGLE;
import static px.objects.offers.OffersPageLocators.*;

/**
 * Created by kgr on 10/13/2016.
 */
public class EditOffersPage extends CreateOffersPage implements Editable {

    public EditOffersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditOffersPage editInstance(OfferTestData oldData, OfferTestData newData) {
        log.info(String.format("Edit publisher '%s'", oldData.getName()));
        log.info("Edit 'General' data");
        editInstance(GENERAL_EDIT_CONTAINER);
        createInstance(newData);
        return this;
    }

    public EditOffersPage checkOfferGroup(OfferTestData testData) {
        helper.waitUntilDisplayed(OFFER_GROUPS_CONTAINER);
//        editInstance(OFFER_GROUPS_CONTAINER);
        if (!helper.isElementAccessible(OFFER_GROUPS_SELECT)) {
            helper.click(CREATE_TOGGLE);
        }
        if (!testData.isAssignedToGroup()) {
            SelectElement groupsSelect = new SelectElement(helper.getElement(OFFER_GROUPS_SELECT));
            groupsSelect.setByText(testData.getOfferGroup());
            groupsSelect.expand();
            saveInstance(OFFER_GROUPS_CONTAINER);
            checkErrorMessage();
            // check that returns to edit state
            assertThat(String.format("Group '%s' is assigned to offer '%s'", testData.getOfferGroup(), testData.getName()),
                    groupsSelect.getValue(), equalTo(testData.getOfferGroup()));
        } else {
            // check that returns to edit state
            assertThat(String.format("Group '%s' is already assigned to offer '%s'", testData.getOfferGroup(), testData.getName()),
                    new SelectElement(helper.getElement(OFFER_GROUPS_SELECT)).getValue(), containsString(testData.getOfferGroup()));
        }
        return this;
    }

    public EditOffersPage checkOfferTargeting(OfferTestData testData) {
        helper.waitUntilDisplayed(OFFER_TARGETING_CONTAINER);
        if (!helper.isElementAccessible(TARGETING_ACTION_RADIO)) {
            helper.click(CREATE_TOGGLE);
//            waitPageIsLoaded();
            helper.waitUntilDisplayed(TARGETING_ACTION_RADIO);
        }
        new RadioButtonElement(helper.getElement(TARGETING_DEVICE_TYPE_RADIO)).setByIndex(testData.getDeviceTypeIndex());
        new RadioButtonElement(helper.getElement(TARGETING_ACTION_RADIO)).setByText(testData.getAction());
        helper.waitForAjaxComplete();
        helper.waitUntilDisplayed(TARGETING_DEVICE_SELECT);
        SelectElement targetDeviceSelect = new SelectElement(helper.getElement(TARGETING_DEVICE_SELECT));
        targetDeviceSelect.setByTitle(testData.getDeviceBrand(), true);
        // create targeting?
        helper.click(OFFER_TARGETING_CONTAINER + CREATE_TARGETING);
        helper.waitForAjaxComplete();
        checkErrorMessage();
        return this;
    }

    @Override
    public EditOffersPage checkDefaultValues(InstancesTestData pTestData) {
        if (!(pTestData instanceof OfferTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OfferTestData testData = (OfferTestData) pTestData;
        log.info("Check create offer page with default values");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        waitPageIsLoaded();
        helper.waitUntilDisplayed(GENERAL_EDIT_CONTAINER);
        hamcrest.assertThat("Name verification", nameInput.getText(), equalTo(testData.getName()));
        hamcrest.assertThat("PreviewURL verification", previewURLInput.getText(), equalTo(testData.getPreviewURL()));
        if (testData.getOfferCategories() != null)
            hamcrest.assertThat("OfferCategories verification", offerCategoriesSelect.getText(), equalTo(testData.getOfferCategories()));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("No implementation");
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("No implementation");
    }
}