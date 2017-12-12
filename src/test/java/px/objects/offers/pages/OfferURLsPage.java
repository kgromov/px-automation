package px.objects.offers.pages;

import configuration.browser.PXDriver;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.offers.OfferTestData;

import java.util.Arrays;

import static px.objects.offers.OffersPageLocators.*;

/**
 * Created by kgr on 1/25/2017.
 */
public class OfferURLsPage extends ObjectPage implements Creatable {

    public OfferURLsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public OfferURLsPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof OfferTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OfferTestData testData = (OfferTestData) pTestData;
        log.info(String.format("Create offer '%s'", testData.getUrlName()));
        helper.waitUntilDisplayed(CREATE_OFFER_URL_BUTTON);
        helper.click(CREATE_OFFER_URL_BUTTON);
        helper.waitUntilDisplayed(GENERAL_EDIT_CONTAINER);
        new InputElement(helper.getElement(OFFER_URL_NAME_INPUT)).setByText(testData.getUrlName());
        new InputElement(helper.getElement(URL_INPUT)).setByText(testData.getUrl());
        new InputElement(helper.getElement(PREVIEW_URL_INPUT)).setByText(testData.getUrlName());
        new SelectElement(helper.getElement(OFFER_URL_STATUS_SELECT)).setByText(testData.getUrlStatus());
        return this;
    }

    @Override
    public OfferURLsPage saveInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof OfferTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OfferTestData testData = (OfferTestData) pTestData;
        saveInstance(GENERAL_EDIT_CONTAINER);
        if (testData.isPositive()) checkErrorMessage();
        else {
            checkErrorMessage(Arrays.asList(
                    new InputElement(helper.getElement(OFFER_URL_NAME_INPUT)),
                    new InputElement(helper.getElement(URL_INPUT))
            ));
        }
        return this;
    }
}