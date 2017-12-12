package tests.objects.roles.buyer;

import configuration.dataproviders.BuyerCampaignDataProvider;
import org.testng.annotations.Test;
import px.objects.campaigns.pages.buyer.CampaignDetailsUnderBuyerPage;
import px.objects.campaigns.pages.buyer.EditCampaignsUnderBuyerPage;
import px.reports.campaigns.CampaignDetailsUnderBuyerTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/11/2016.
 */
public class EditCampaignsUnderBuyerTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "editCampaignUnderBuyerData", dataProviderClass = BuyerCampaignDataProvider.class)
    public void editCampaignWithPositiveData(CampaignDetailsUnderBuyerTestData testData) {
        pxDriver.goToURL(url + "campaigns/" + testData.getCampaignGUID() + "/edit");
        login();
        EditCampaignsUnderBuyerPage editCampaignsPage = new EditCampaignsUnderBuyerPage(pxDriver);
        editCampaignsPage.editInstance(testData);
        CampaignDetailsUnderBuyerPage campaignDetailsPage = new CampaignDetailsUnderBuyerPage(pxDriver);
        campaignDetailsPage.checkCampaignFieldsEditable(testData);
        editCampaignsPage.saveInstance(testData);
    }

    @Test(dataProvider = "editCampaignUnderBuyerNegativeData", dataProviderClass = BuyerCampaignDataProvider.class)
    public void editCampaignWithNegativeData(CampaignDetailsUnderBuyerTestData testData) {
        pxDriver.goToURL(url + "campaigns/" + testData.getCampaignGUID() + "/edit");
        login();
        EditCampaignsUnderBuyerPage editCampaignsPage = new EditCampaignsUnderBuyerPage(pxDriver);
        editCampaignsPage.editInstance(testData);
        CampaignDetailsUnderBuyerPage campaignDetailsPage = new CampaignDetailsUnderBuyerPage(pxDriver);
        campaignDetailsPage.checkCampaignFieldsEditable(testData);
        editCampaignsPage.saveInstance(testData);
    }
}
