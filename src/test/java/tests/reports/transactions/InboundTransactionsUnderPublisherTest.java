package tests.reports.transactions;

import configuration.dataproviders.PublisherReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.ReportTestData;
import px.reports.inbound.InboundTransactionTestData;
import px.reports.inbound.InboundTransactionsReportPage;
import tests.LoginTest;

import java.util.Arrays;
import java.util.List;

import static px.reports.inbound.InboundTransactionTestData.*;

/**
 * Created by kgr on 11/17/2016.
 */
public class InboundTransactionsUnderPublisherTest extends LoginTest {

    @Test
    public void checkFilterAccordance() {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.checkFiltersAccordance(new ReportTestData() {
            @Override
            public List<String> filters() {
                return Arrays.asList(RESULT_CODES_FILTER, VERTICAL_FILTER, OFFER_FILTER);
            }
        });
    }

    @Test(dataProvider = "inboundTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(InboundTransactionTestData testData) {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "inboundTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(InboundTransactionTestData testData) {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "inboundTransactionsReportInstancesData", dataProviderClass = PublisherReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(InboundTransactionTestData testData) {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters       ;
        reportPage.checkReasonCodesFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        reportPage.checkOffersFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
