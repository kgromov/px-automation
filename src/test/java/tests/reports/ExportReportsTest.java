package tests.reports;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.audience.AudienceReportPage;
import px.reports.audience.AudienceReportTestData;
import px.reports.buyerPerformance.BuyerPerformanceReportPage;
import px.reports.buyerPerformance.BuyerPerformanceTestData;
import px.reports.dailyMargin.DailyMarginReportPage;
import px.reports.dailyMargin.DailyMarginReportTestData;
import px.reports.export.LeadsExportTestData;
import px.reports.inbound.InboundTransactionTestData;
import px.reports.inbound.InboundTransactionsReportPage;
import px.reports.leads.LeadsReportPage;
import px.reports.outbound.OutboundTransactionTestData;
import px.reports.outbound.OutboundTransactionsReportPage;
import px.reports.pingPost.PingPostReportPage;
import px.reports.pingPost.PingPostReportTestData;
import px.reports.pingPostTransactions.PinPostTransactionsReportPage;
import px.reports.pingPostTransactions.PingPostTransactionsTestData;
import px.reports.publisherConversion.PublisherConversionPage;
import px.reports.publisherConversion.PublisherConversionTestData;
import px.reports.publisherDaily.PublisherDailyReportPage;
import px.reports.publisherDaily.PublisherDailyReportTestData;
import px.reports.publisherPerformance.PublisherPerformanceReportPage;
import px.reports.publisherPerformance.PublisherPerformanceTestData;
import px.reports.sourceQuality.SourceQualityScoreFiltersEnum;
import px.reports.sourceQuality.SourceQualityScoreReportPage;
import px.reports.sourceQuality.SourceQualityScoreTestData;
import tests.LoginTest;

import java.util.List;

import static px.reports.ReportPageLocators.BUYER_CATEGORIES_FILTER;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.audience.AudienceReportTestData.REPORT_TYPE_FILTER;
import static px.reports.audience.AudienceReportTestData.VERTICAL_FILTER;

/**
 * Created by kgr on 4/21/2017.
 */
public class ExportReportsTest extends LoginTest {

    @Test(dataProvider = "audienceReportNonVerticalExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "audienceReportNonVerticalExportData")
    public void checkDefaultVerticalExportReport(AudienceReportTestData testData, String reportType) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), reportType);
        // check export feature
        reportPage.checkExport();
    }

    @Test(dataProvider = "audienceReportVerticalExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "audienceReportVerticalExportData")
    public void checkVerticalExportReport(AudienceReportTestData testData, String reportType, String vertical) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set vertical
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), vertical);
        // set report type
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), reportType);
        // check export feature
        reportPage.checkExport();
    }

    @Test(dataProvider = "buyerPerformanceReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "buyerPerformanceReportExportData")
    public void checkExportBuyerPerformanceReport(BuyerPerformanceTestData testData, String buyerCategory) {
        BuyerPerformanceReportPage reportPage = new BuyerPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, buyerCategory);
        // display all columns in table
        reportPage.displayAllTableColumns();
        reportPage.checkExport();
    }

    @Test(dataProvider = "leadsReportExportData", dataProviderClass = ExportReportsDataProvider.class)
    public void checkExportLeadsReport(LeadsExportTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport(testData);
    }

    @Test(dataProvider = "publisherConversionReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportPublisherConversionReport(PublisherConversionTestData testData) {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherDailyReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "publisherDailyReportExportData")
    public void checkPublisherDailyExportReport(PublisherDailyReportTestData testData, String reportType) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
      /*  // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);*/
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, reportType);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherDailyReportGroupingExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "publisherDailyReportGroupingExportData")
    public void checkExportPublisherDailyGroupingReport(PublisherDailyReportTestData testData, String reportType, List<String> groupingList) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
      /*  // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);*/
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, reportType);
        // set grouping
        reportPage.setGroupingFilter(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherPerformanceReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkPublisherPerformanceExportReport(PublisherPerformanceTestData testData) {
        PublisherPerformanceReportPage reportPage = new PublisherPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "inboundTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportInboundTransactionsReport(InboundTransactionTestData testData) {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "outboundTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportOutboundTransactionsReport(OutboundTransactionTestData testData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "sourceQualityScoreReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportSourceQualityScoreReport(SourceQualityScoreTestData testData) {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), SourceQualityScoreFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    // not in development yet, is running on http://rvmd-11606-ui.stagingpx.com/
    @Test(dataProvider = "pingPostReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkExportReport")
    public void checkExportReport(PingPostReportTestData testData, String reportType) {
        PingPostReportPage reportPage = new PingPostReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setReportTypeFilter(testData);
        // check export feature
        reportPage.checkExport();
    }

    // not in development yet, is running on http://rvmd-12633-ui.stagingpx.com/
    @Test(dataProvider = "pingPostTransactionsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(PingPostTransactionsTestData testData) {
        PinPostTransactionsReportPage reportPage = new PinPostTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "dailyMarginReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkExportReport")
    public void checkExportReport(DailyMarginReportTestData testData, String reportType) {
        DailyMarginReportPage reportPage = new DailyMarginReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setReportTypeFilter(testData);
        // check export feature
        reportPage.checkExport();
    }
}
