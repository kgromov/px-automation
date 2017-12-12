package tests.reports.pingPost;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.pingPost.PingPostReportFiltersEnum;
import px.reports.pingPost.PingPostReportPage;
import px.reports.pingPost.PingPostReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 5/30/2017.
 */
public class PingPostReportTest extends LoginTest {
    private String url = super.url = "http://stage03-ui.stagingpx.com/"; // "http://rvmd-11606-ui.stagingpx.com/";

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

    @Test(dataProvider = "pingPostReportCommonData", dataProviderClass = ReportsDataProvider.class, testName = "checkCommonReport")
    public void checkCommonReport(PingPostReportTestData testData, String reportType) {
        PingPostReportPage reportPage = new PingPostReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setReportTypeFilter(testData);
        reportPage.checkPagination(testData, testData.getItemsByReportTypeCount());
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), PingPostReportFiltersEnum.values());
        // turn off graphics
        reportPage.turnOffGraphics();
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "pingPostReportInstancesData", dataProviderClass = ReportsDataProvider.class, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(PingPostReportTestData testData) {
        PingPostReportPage reportPage = new PingPostReportPage(pxDriver);
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
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}
