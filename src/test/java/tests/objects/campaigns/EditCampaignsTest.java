package tests.objects.campaigns;

import configuration.dataproviders.CampaignDataProvider;
import dto.CheckTestData;
import org.testng.annotations.Test;
import px.objects.campaigns.CampaignTestData;
import px.objects.campaigns.pages.EditCampaignsPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/11/2016.
 */
public class EditCampaignsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "editCampaignData", dataProviderClass = CampaignDataProvider.class)
    public void editCampaignWithPositiveData(CampaignTestData oldData, CampaignTestData newData) {
        pxDriver.goToURL(url + "campaigns/" + oldData.getGuid() + "/edit");
        login();
        new EditCampaignsPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData)
                .saveInstance(newData);
        // check edited fields
        CheckTestData.checkEditedCampaign(newData);
    }

    @Test(dataProvider = "editNegativeCampaignData", dataProviderClass = CampaignDataProvider.class)
    public void editCampaignWithNegativeData(CampaignTestData oldData, CampaignTestData newData) {
        pxDriver.goToURL(url + "campaigns/" + oldData.getGuid() + "/edit");
        login();
        new EditCampaignsPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData)
                .saveInstance(newData);
    }
}
