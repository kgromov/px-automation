package px.objects.campaigns.pages;

import configuration.browser.PXDriver;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.campaigns.CampaignTestData;
import utils.SoftAssertionHamcrest;

import static org.hamcrest.CoreMatchers.equalTo;
import static px.objects.campaigns.CampaignsPageLocators.GENERAL_CONTAINER;


/**
 * Created by kgr on 10/19/2016.
 */
public class EditCampaignsPage extends CreateCampaignsPage implements Editable {

    public EditCampaignsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public EditCampaignsPage editInstance(InstancesTestData oldData, InstancesTestData newData) {
        if (!(oldData instanceof CampaignTestData) || !(newData instanceof CampaignTestData))
            throw new IllegalArgumentException(String.format("Illegal test data class(es) - %s, %s",
                    oldData.getClass().getName(), newData.getClass().getName()));
        CampaignTestData testData2 = (CampaignTestData) newData;
        log.info(String.format("Edit campaign '%s'", oldData.getName()));
        log.info("Edit 'General' data");
        editInstance(GENERAL_CONTAINER);
        createInstance(testData2);
        return this;
    }

    @Override
    public EditCampaignsPage checkDefaultValues(InstancesTestData pTestData) {
        if (!(pTestData instanceof CampaignTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        CampaignTestData testData = (CampaignTestData) pTestData;
        log.info("Check fields with created campaign values");
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Name verification", campaignNameInput.getText(), equalTo(testData.getName()));
        hamcrest.assertThat("DeliveryType verification", deliveryTypeSelect.getText(), equalTo(testData.getDeliveryType()));
        hamcrest.assertThat("BuyerCategory verification", buyerCategorySelect.getText(), equalTo(testData.getBuyerCategory()));
        hamcrest.assertThat("Vertical verification", verticalSelect.getText(), equalTo(testData.getVertical()));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}