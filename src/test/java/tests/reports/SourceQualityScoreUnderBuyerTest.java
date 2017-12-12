package tests.reports;

import configuration.dataproviders.BuyerReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.sourceQuality.SourceQualityScoreFiltersEnum;
import px.reports.sourceQuality.SourceQualityScoreReportPage;
import px.reports.sourceQuality.SourceQualityScoreTestData;
import px.reports.sourceQuality.SourceQualityScoreUnderBuyerTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class SourceQualityScoreUnderBuyerTest extends LoginTest {
//    private String url = super.url = "http://pxdemo.px.com/";

    @Test
    public void checkFilterAccordance() {
        SourceQualityScoreUnderBuyerTestData testData = new SourceQualityScoreUnderBuyerTestData(0);
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.turnOffGraphics();
        reportPage.checkFiltersAccordance(testData);
    }

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
        reportPage.checkGraphicsUnderBuyer(testData, testData.getAllRowsArray(), SourceQualityScoreFiltersEnum.filters());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "sourceQualityScoreReportInstancesData", dataProviderClass = BuyerReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(SourceQualityScoreUnderBuyerTestData testData) {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        // cause of overlapped tooltip
        reportPage.turnOffGraphics();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check buyer instances filters
        reportPage.checkBuyerCampaigns(testData);
        reportPage.checkBuyerInstanceFilter(testData);
    }
}