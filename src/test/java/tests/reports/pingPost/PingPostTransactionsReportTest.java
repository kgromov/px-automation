package tests.reports.pingPost;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.pingPostTransactions.PinPostTransactionsReportPage;
import px.reports.pingPostTransactions.PingPostTransactionsTestData;
import tests.LoginTest;

/**
 * Created by kgr on 5/10/2017.
 */
public class PingPostTransactionsReportTest extends LoginTest {
    private String url = super.url = "http://stage03-ui.stagingpx.com/"; // "http://rvmd-11606-ui.stagingpx.com/";

    @Test(dataProvider = "pingPostTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(PingPostTransactionsTestData testData) {
        PinPostTransactionsReportPage reportPage = new PinPostTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "pingPostTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(PingPostTransactionsTestData testData) {
        PinPostTransactionsReportPage reportPage = new PinPostTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "pingPostTransactionsReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(PingPostTransactionsTestData testData) {
        PinPostTransactionsReportPage reportPage = new PinPostTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters
        reportPage.checkPublishersFilter(testData);
        reportPage.checkPublisherInstanceFilter(testData);
        reportPage.resetPublishersFilter(testData);
        reportPage.checkResultCodesFilter(testData);
        reportPage.checkPostTypesFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
