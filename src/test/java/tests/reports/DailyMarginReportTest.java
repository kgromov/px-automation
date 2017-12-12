package tests.reports;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.dailyMargin.DailyMarginReportFiltersEnum;
import px.reports.dailyMargin.DailyMarginReportPage;
import px.reports.dailyMargin.DailyMarginReportTestData;
import tests.LoginTest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.dailyMargin.DailyMarginReportTestData.REPORT_TYPE_FILTER;

/**
 * Created by kgr on 5/25/2017.
 */
public class DailyMarginReportTest extends LoginTest {

    @Test(dataProvider = "dailyMarginReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkExportReport")
    public void checkExportReport(DailyMarginReportTestData testData, String reportType) {
        DailyMarginReportPage reportPage = new DailyMarginReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), reportType);
        // check export feature
        reportPage.checkExport();
    }

    @Test(dataProvider = "dailyMarginReportCommonData", dataProviderClass = ReportsDataProvider.class, testName = "checkCommonReport")
    public void checkCommonReport(DailyMarginReportTestData testData, String reportType) {
        DailyMarginReportPage reportPage = new DailyMarginReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setReportTypeFilter(testData);
        reportPage.checkPagination(testData, testData.getItemsByReportTypeCount());
        reportPage.checkGraphics(testData, testData.getItemsByReportType(), DailyMarginReportFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "dailyMarginReportCommonData", dataProviderClass = ReportsDataProvider.class, testName = "checkForecast")
    public void checkForecast(DailyMarginReportTestData testData, String reportType) {
        DailyMarginReportPage reportPage = new DailyMarginReportPage(pxDriver);
        reportPage.navigateToPage();
        // set report type
        reportPage.setReportTypeFilter(testData);
        // check forecast row presence/absence according to date range and breakdown filter
        reportPage.checkForecast(testData);
    }

    @Test(dataProvider = "dailyMarginReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(DailyMarginReportTestData testData) {
        DailyMarginReportPage reportPage = new DailyMarginReportPage(pxDriver);
        reportPage.navigateToPage();
        // turn off the graphics cause of overlapped tooltip(s)
        reportPage.turnOffGraphics();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check report type filter
        reportPage.checkReportTypeFilter(testData);
        // proper table records
        reportPage.setReportTypeFilter(testData);
        // check publisher instances filters
        reportPage.checkPublisherFilter(testData);
        reportPage.checkPublisherInstanceFilter(testData);
        reportPage.resetPublishersFilter(testData);
        // check other filters
        reportPage.checkVerticalsFilter(testData);
        reportPage.checkBuyerCategoriesFilter(testData);
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}