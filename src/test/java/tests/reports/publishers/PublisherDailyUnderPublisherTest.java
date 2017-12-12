package tests.reports.publishers;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.PublisherReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.publisherDaily.PublisherDailyReportFiltersEnum;
import px.reports.publisherDaily.PublisherDailyReportPage;
import px.reports.publisherDaily.PublisherDailyReportTestData;
import px.reports.publisherDaily.PublisherDailyReportUnderPublisherTestData;
import tests.LoginTest;

import java.util.List;

import static px.reports.ReportPageLocators.BUYER_CATEGORIES_FILTER;

/**
 * Created by kgr on 11/17/2016.
 */
public class PublisherDailyUnderPublisherTest extends LoginTest {
//        private String url = super.url = "http://beta.px.com/";

    @Test
    public void checkFilterAccordance() {
        PublisherDailyReportUnderPublisherTestData testData = new PublisherDailyReportUnderPublisherTestData();
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.turnOffGraphics();
        reportPage.checkFiltersAccordance(testData);
    }

    @Test(dataProvider = "publisherDailyReportGroupingCommonData", dataProviderClass = ReportsDataProvider.class, testName = "checkCommonReport")
    public void checkCommonReport(PublisherDailyReportTestData testData, String reportType, List<String> groupingList) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
        // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, reportType);
        // set grouping
        reportPage.setGroupingFilter(testData);
        // check for each report type with groping
        reportPage.checkPagination(testData, testData.getItemsByGroupingCount());
        reportPage.checkGraphics(testData, testData.getItemsByGrouping(), PublisherDailyReportFiltersEnum.filters());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "publisherDailyReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkExportReport")
    public void checkExportReport(PublisherDailyReportTestData testData, String reportType) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
        // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, reportType);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherDailyReportGroupingExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkExportGroupingReport")
    public void checkExportGroupingReport(PublisherDailyReportTestData testData, String reportType, List<String> groupingList) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
        // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, reportType);
        // set grouping
        reportPage.setGroupingFilter(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherDailyReportInstancesData", dataProviderClass = PublisherReportsDataProvider.class, priority = 1, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(PublisherDailyReportUnderPublisherTestData testData) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
        // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);
        reportPage.turnOffGraphics();
        // set report type
        reportPage.setReportTypeFilter(testData);
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters
        reportPage.checkOffersFilter(testData);
        reportPage.checkBrowsersFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
        // check report type filter
        reportPage.checkReportTypeFilter(testData);
    }

    @Test(dataProvider = "publisherDailyReportGroupingInstancesData", dataProviderClass = PublisherReportsDataProvider.class, priority = 1, testName = "checkGroupingInstancesFiltersReport")
    public void checkGroupingInstancesFiltersReport(PublisherDailyReportUnderPublisherTestData testData) {
        PublisherDailyReportPage reportPage = new PublisherDailyReportPage(pxDriver);
        reportPage.navigateToPage();
        // include all non-grouping field columns
        reportPage.displayAllTableColumns(testData);
        // turn off all graphics
        reportPage.turnOffGraphics();
        // set report type
        reportPage.setReportTypeFilter(testData);
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set grouping
        reportPage.setGroupingFilter(testData);
        // common for each buyer category
        // check instance filters
        reportPage.checkOffersFilter(testData);
        reportPage.checkBrowsersFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
        // check grouping filter
        reportPage.checkGroupingFilter(testData);
        // check report type filter
        reportPage.checkReportTypeFilter(testData);
    }
}
