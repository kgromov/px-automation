package px.objects.campaigns.pages.buyer;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Editable;
import pages.groups.Objectable;
import px.objects.InstancesTestData;
import px.reports.campaigns.CampaignDetailsUnderBuyerTestData;

import java.util.Collections;

import static px.objects.campaigns.CampaignsPageLocators.*;


/**
 * Created by kgr on 10/19/2016.
 */
public class EditCampaignsUnderBuyerPage extends ObjectPage implements Editable {
    @FindBy(xpath = DAILY_CAP_INPUT)
    InputElement dailyCap;

    public EditCampaignsUnderBuyerPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditCampaignsUnderBuyerPage editInstance(CampaignDetailsUnderBuyerTestData testData) {
        log.info(String.format("Edit campaign '%s'", testData.getCampaignName()));
        log.info("Edit 'General' data");
        editInstance(GENERAL_CONTAINER);
        dailyCap.setByText(testData.getDailyCap());
        return this;
    }


    public EditCampaignsUnderBuyerPage saveInstance(CampaignDetailsUnderBuyerTestData testData) {
        saveInstance(VOLUME_CONTAINER);
        waitPageIsLoaded();
        if (testData.isPositive()) checkErrorMessage();
        else checkErrorMessage(Collections.singletonList(dailyCap));
        return this;
    }

    @Override
    public EditCampaignsUnderBuyerPage checkDefaultValues() {
        throw new UnsupportedOperationException("Irrelevant method");
    }

    @Override
    public Objectable saveInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }

    @Override
    public Editable editInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}