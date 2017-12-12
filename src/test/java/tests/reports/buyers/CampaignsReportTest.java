package tests.reports.buyers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.campaigns.CampaignsReportFiltersEnum;
import px.reports.campaigns.CampaignsReportPage;
import px.reports.campaigns.CampaignsReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignsReportTest extends LoginTest {

    @Test(dataProvider = "campaignsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(CampaignsReportTestData testData) {
        CampaignsReportPage reportPage = new CampaignsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), CampaignsReportFiltersEnum.filters());
        reportPage.checkCalendarDateRangesTableRecordsOrder(testData);
    }

    @Test(dataProvider = "campaignsReportInstancesData", dataProviderClass = ReportsDataProvider.class, invocationCount = 5)
    public void checkTableReport(CampaignsReportTestData testData) {
        CampaignsReportPage reportPage = new CampaignsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check vertical filter
        reportPage.checkVerticalsFilter(testData);
        // check buyer filter
        reportPage.checkBuyersFilter(testData);
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}