package tests.reports;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.sourceQuality.SourceQualityScoreFiltersEnum;
import px.reports.sourceQuality.SourceQualityScoreReportPage;
import px.reports.sourceQuality.SourceQualityScoreTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class SourceQualityScoreReportTest extends LoginTest {
//    private String url = super.url = "http://pxdemo.px.com/";

    @Test(dataProvider = "sourceQualityScoreReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(SourceQualityScoreTestData testData) {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check export feature
        reportPage.checkExport();
    }

    @Test(dataProvider = "sourceQualityScoreReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(SourceQualityScoreTestData testData) {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), SourceQualityScoreFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "sourceQualityScoreReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(SourceQualityScoreTestData testData) {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
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
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}
