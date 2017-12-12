package tests.reports.publishers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.publisherPerformance.PublisherPerformanceReportFiltersEnum;
import px.reports.publisherPerformance.PublisherPerformanceReportPage;
import px.reports.publisherPerformance.PublisherPerformanceTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class PublisherPerformanceReportTest extends LoginTest {

    @Test(dataProvider = "publisherPerformanceReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(PublisherPerformanceTestData testData) {
        PublisherPerformanceReportPage reportPage = new PublisherPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), PublisherPerformanceReportFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "publisherPerformanceReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(PublisherPerformanceTestData testData) {
        PublisherPerformanceReportPage reportPage = new PublisherPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherPerformanceReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(PublisherPerformanceTestData testData) {
        PublisherPerformanceReportPage reportPage = new PublisherPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // turn off graphics
        reportPage.turnOffGraphics();
        // check instance filters
        reportPage.checkPublisherManagersFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        reportPage.checkBuyerCategoriesFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
