package tests.reports;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.audience.AudienceFiltersEnum;
import px.reports.audience.AudienceReportPage;
import px.reports.audience.AudienceReportTestData;
import px.reports.audience.AudienceVerticalReportTestData;
import tests.LoginTest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.audience.AudienceReportTestData.REPORT_TYPE_FILTER;
import static px.reports.audience.AudienceReportTestData.VERTICAL_FILTER;

/**
 * Created by kgr on 11/17/2016.
 */
public class AudienceReportTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test(dataProvider = "audienceReportNonVerticalExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkDefaultVerticalExportReport")
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

    @Test(dataProvider = "audienceReportVerticalExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkVerticalExportReport")
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

    @Test(dataProvider = "audienceReportNonVerticalCommonData", dataProviderClass = ReportsDataProvider.class, testName = "checkDefaultVerticalCommonReport")
    public void checkDefaultVerticalCommonReport(AudienceReportTestData testData, String reportType) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), reportType);
        // check common for each report type
        reportPage.checkPagination(testData, testData.getItemsByReportTypeCount());
        reportPage.checkGraphics(testData, testData.getItemsByDefaultReportType(), AudienceFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData, 30);
    }

    @Test(dataProvider = "audienceReportNonVerticalInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(AudienceReportTestData testData) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // proper table records
        reportPage.setReportTypeFilter(testData);
        // check vertical filter
        reportPage.checkVerticalFilter(testData);
        // proper table records
        reportPage.setReportTypeFilter(testData);
        // check buyer instances filters
        reportPage.checkBuyerFilter(testData);
        reportPage.checkBuyerInstanceFilter(testData);
        reportPage.resetBuyersFilter(testData);
        // check publisher instances filters
        reportPage.checkPublisherFilter(testData);
        reportPage.checkPublisherInstanceFilter(testData);
        reportPage.resetPublishersFilter(testData);
        // check all filters
        reportPage.checkAllFilters(testData);
        // check report type filter
        reportPage.checkReportTypeFilter(testData);
    }

    @Test(dataProvider = "audienceReportVerticalInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersByVerticalReport(AudienceVerticalReportTestData testData) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set vertical for proper report types
        reportPage.setVerticalFilter(testData);
        // proper table records
        reportPage.setReportTypeFilter(testData);
        // check buyer instances filters
        reportPage.checkBuyerFilter(testData);
        reportPage.checkBuyerInstanceFilter(testData);
        reportPage.resetBuyersFilter(testData);
        // check publisher instances filters
        reportPage.checkPublisherFilter(testData);
        reportPage.checkPublisherInstanceFilter(testData);
        reportPage.resetPublishersFilter(testData);
        // check all filters
        reportPage.checkAllFilters(testData);
        // check vertical type filter
        reportPage.checkVerticalFilter(testData);
    }
}
