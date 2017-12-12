package tests.reports.buyers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.buyerCampaign.BuyerCampaignReportFiltersEnum;
import px.reports.buyerCampaign.BuyerCampaignReportPage;
import px.reports.buyerCampaign.BuyerCampaignReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class BuyerCampaignReportTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test(dataProvider = "buyerCampaignReportCommonData", dataProviderClass = ReportsDataProvider.class, testName = "checkCommonReport")
    public void checkCommonReport(BuyerCampaignReportTestData testData, String reportType, String buyerCategory) {
        BuyerCampaignReportPage reportPage = new BuyerCampaignReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setReportTypeFilter(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
        // common for each report type and buyer category
        reportPage.checkPagination(testData, testData.getItemsByBuyerCategoryCount());
        reportPage.checkGraphics(testData, testData.getItemsByBuyerCategory(), BuyerCampaignReportFiltersEnum.values());
        if (testData.hasCampaignsByCategory())
            reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "buyerCampaignReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(BuyerCampaignReportTestData testData) {
         BuyerCampaignReportPage reportPage = new BuyerCampaignReportPage(pxDriver);
        reportPage.navigateToPage();
        // turn off graphics
        reportPage.turnOffGraphics();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setReportTypeFilter(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
    /*    // check by instances
        reportPage.checkBuyerCampaignFilter(testData);
        reportPage.checkTransactionTypesFilter(testData);
        reportPage.checkAllFilters(testData);*/
       // all rows
        reportPage.checkBuyerCategoryFilter(testData);
        reportPage.checkReportTypeFilter(testData);
    }
}
