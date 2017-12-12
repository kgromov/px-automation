package tests.reports;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.disposition.DispositionBreakdownFiltersEnum;
import px.reports.disposition.DispositionBreakdownReportPage;
import px.reports.disposition.DispositionBreakdownReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class DispositionBreakdownReportTest extends LoginTest {
    private String url = super.url = "http://stage05-ui.stagingpx.com/";

    @Test(dataProvider = "dispositionBreakdownReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(DispositionBreakdownReportTestData testData) {
        DispositionBreakdownReportPage reportPage = new DispositionBreakdownReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), DispositionBreakdownFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "dispositionBreakdownReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(DispositionBreakdownReportTestData testData) {
        DispositionBreakdownReportPage reportPage = new DispositionBreakdownReportPage(pxDriver);
        reportPage.navigateToPage();
        // cause of overlapped tooltip
        reportPage.turnOffGraphics();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check buyer instances filters
        reportPage.checkBuyerFilter(testData);
        reportPage.checkBuyerInstanceFilter(testData);
        reportPage.resetBuyersFilter(testData);
        // check publisher instances filters
        reportPage.checkPublisherFilter(testData);
        reportPage.checkPublisherInstanceFilter(testData);
        reportPage.resetPublishersFilter(testData);
        // check vertical filter
        reportPage.checkVerticalsFilter(testData);
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}
