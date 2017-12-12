package px.objects.pixels.pages;

import configuration.browser.PXDriver;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.brokers.BrokersPageLocators;
import px.objects.pixels.PixelTestData;

import static px.objects.payouts.PayoutsPageLocators.PUBLISHER_ID_SELECT;
import static px.objects.pixels.PixelPageLocators.GENERAL_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by kgr on 1/25/2017.
 */
public class EditPixelsPage extends CreatePixelsPage implements Editable {

    public EditPixelsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditPixelsPage editInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PixelTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PixelTestData testData = (PixelTestData) pTestData;
        log.info(String.format("Create offer pixel, offer id ='%s'", testData.getOfferID()));
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        editInstance(BrokersPageLocators.GENERAL_CONTAINER);
        helper.waitUntilDisplayed(CODE_INPUT);
        new SelectElement(helper.getElement(BrokersPageLocators.GENERAL_CONTAINER, OFFER_ID_SELECT))
                .setByText(testData.getOfferID() + " - " + testData.getOfferName());
        new SelectElement(helper.getElement(BrokersPageLocators.GENERAL_CONTAINER, PUBLISHER_ID_SELECT))
                .setByText(testData.getPublisher().getId() + " - " + testData.getPublisher().getName());
        new SelectElement(helper.getElement(BrokersPageLocators.GENERAL_CONTAINER, PIXEL_STATUS_SELECT)).setByText(testData.getPixelStatus());
        new SelectElement(helper.getElement(BrokersPageLocators.GENERAL_CONTAINER, TYPE_SELECT)).setByText(testData.getPixelType());
        new InputElement(helper.getElement(CODE_INPUT)).setByText(testData.getPixelCode());
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("No implementation");
    }

    @Override
    public EditPixelsPage saveInstance(InstancesTestData testData) {
        saveInstance(BrokersPageLocators.GENERAL_CONTAINER);
        waitPageIsLoaded();
        checkErrors(testData);
        return this;
    }
}