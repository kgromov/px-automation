package tests.objects.roles.buyer;

import configuration.dataproviders.BuyerCampaignDataProvider;
import org.testng.annotations.Test;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.campaigns.pages.buyer.CampaignDetailsUnderBuyerPage;
import px.reports.campaigns.CampaignsReportPage;
import px.reports.campaigns.CampaignDetailsUnderBuyerTestData;
import tests.LoginTest;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignDetailsUnderBuyerTest extends LoginTest {

    @Test
    public void checkCampaignsReadOnly() {
        CampaignsPage campaignsPage = new CampaignsPage(pxDriver);
        campaignsPage.navigateToObjects();
        campaignsPage.checkReadOnly();
    }

    @Test(dataProvider = "campaignUnderBuyerDetailsReportData", dataProviderClass = BuyerCampaignDataProvider.class)
    public void checkCampaignDetails(CampaignDetailsUnderBuyerTestData testData) {
        CampaignsReportPage reportPage = new CampaignsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose campaign in campaigns report and navigate to it details
        CampaignDetailsUnderBuyerPage campaignDetailsPage = new CampaignDetailsUnderBuyerPage(pxDriver);
        reportPage.navigateToObject(testData.getCampaignName());
        // check lead details with response
        campaignDetailsPage.checkCampaignDetails(testData);
    }
}