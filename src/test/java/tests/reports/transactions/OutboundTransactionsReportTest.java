package tests.reports.transactions;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.outbound.OutboundTransactionTestData;
import px.reports.outbound.OutboundTransactionsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class OutboundTransactionsReportTest extends LoginTest {

    @Test(dataProvider = "outboundTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(OutboundTransactionTestData testData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "outboundTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(OutboundTransactionTestData testData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "outboundTransactionsReportInstancesData", dataProviderClass = ReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(OutboundTransactionTestData testData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters
        reportPage.checkPublishersFilter(testData);
        reportPage.checkCampaignsFilter(testData);
        reportPage.checkResultCodesFilter(testData);
        reportPage.checkPostTypesFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        reportPage.checkTransactionTypesFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
