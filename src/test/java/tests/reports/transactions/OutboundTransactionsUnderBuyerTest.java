package tests.reports.transactions;

import configuration.dataproviders.BuyerReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.ReportTestData;
import px.reports.outbound.OutboundTransactionTestData;
import px.reports.outbound.OutboundTransactionUnderBuyerTestData;
import px.reports.outbound.OutboundTransactionsReportPage;
import tests.LoginTest;

import java.util.Arrays;
import java.util.List;

import static px.reports.outbound.OutboundTransactionTestData.*;

/**
 * Created by kgr on 11/17/2016.
 */
public class OutboundTransactionsUnderBuyerTest extends LoginTest {

    @Test
    public void checkFilterAccordance() {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.checkFiltersAccordance(new ReportTestData() {
            @Override
            public List<String> filters() {
                return Arrays.asList(BUYER_CAMPAIGN_FILTER, BUYER_RESULT_CODE_FILTER, POST_TYPE_FILTER, VERTICAL_FILTER);
            }
        });
    }

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

    @Test(dataProvider = "outboundTransactionsReportInstancesData", dataProviderClass = BuyerReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(OutboundTransactionUnderBuyerTestData testData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters
        reportPage.checkCampaignsFilter(testData);
        reportPage.checkResultCodesFilter(testData);
        reportPage.checkPostTypesFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
