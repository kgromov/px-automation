package tests.reports;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.ReportTestData;
import px.reports.UserReports;
import px.reports.sourceQuality.SourceQualityScoreFiltersEnum;
import px.reports.sourceQuality.SourceQualityScoreReportPage;
import px.reports.sourceQuality.SourceQualityScoreTestData;
import tests.LoginTest;

import java.util.Collections;
import java.util.List;

import static configuration.helpers.DataHelper.getSetFromJSONArrayByKey;

/**
 * Created by kgr on 11/17/2016.
 */
public class SourceQualityScoreUnderPublisherTest extends LoginTest {
//    private String url = super.url = "http://pxdemo.px.com/";

    @Test
    public void checkFilterAccordance() {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.turnOffGraphics();
        reportPage.checkFiltersAccordance(new ReportTestData() {
            @Override
            public List<String> filters() {
                return Collections.singletonList("type");
            }
        });
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
        reportPage.checkGraphics(testData, testData.getAllRowsArray(), SourceQualityScoreFiltersEnum.filters());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "sourceQualityScoreReportCommonData", dataProviderClass = ReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(SourceQualityScoreTestData testData) {
        SourceQualityScoreReportPage reportPage = new SourceQualityScoreReportPage(pxDriver);
        reportPage.navigateToPage();
        // cause of overlapped tooltip
        reportPage.turnOffGraphics();
        // check subIDs accordance
        new UserReports() {
        }.checkSubIdsTo1Publisher(getSetFromJSONArrayByKey(testData.getAllRowsArray(), "subId"), "name");
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check report table
        reportPage.checkAllCells(testData);
    }
}