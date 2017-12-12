package tests.reports.buyers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.objects.campaigns.pages.CampaignDetailsPage;
import px.reports.campaigns.CampaignDetailsTestData;
import px.reports.campaigns.CampaignsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignDetailsTest extends LoginTest {
 /*   {
        url = "http://rvmd-12094-ui.stagingpx.com/";
    }*/

    @Test(dataProvider = "campaignDetailsReportData", dataProviderClass = ReportsDataProvider.class, invocationCount = 5)
    public void checkCampaignDetails(CampaignDetailsTestData testData) {
        CampaignsReportPage reportPage = new CampaignsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose campaign in campaigns report and navigate to it details
        CampaignDetailsPage campaignDetailsPage = reportPage.navigateToObject(testData.getCampaignName());
        // check lead details with response
        campaignDetailsPage.checkCampaignDetails(testData);
    }
}