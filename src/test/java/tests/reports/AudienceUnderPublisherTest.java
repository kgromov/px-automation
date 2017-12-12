package tests.reports;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.PublisherReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.audience.AudienceFiltersEnum;
import px.reports.audience.AudienceReportPage;
import px.reports.audience.AudienceReportTestData;
import px.reports.audience.AudienceReportUnderPublisherTestData;
import tests.LoginTest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.audience.AudienceReportTestData.REPORT_TYPE_FILTER;

/**
 * Created by kgr on 11/17/2016.
 */
public class AudienceUnderPublisherTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test
    public void checkFilterAccordance() {
        AudienceReportTestData testData = new AudienceReportUnderPublisherTestData(0);
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.turnOffGraphics();
        reportPage.checkFiltersAccordance(testData);
    }

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

    @Test(dataProvider = "audienceReportCommonData", dataProviderClass = PublisherReportsDataProvider.class, testName = "checkDefaultVerticalCommonReport")
    public void checkDefaultVerticalCommonReport(AudienceReportTestData testData, String reportType) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set report type
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), reportType);
        // check common for each report type
        reportPage.checkPagination(testData, testData.getItemsByReportTypeCount());
        reportPage.checkGraphics(testData, testData.getItemsByReportType(), AudienceFiltersEnum.filters());
        reportPage.checkCalendarDateRanges(testData, 30);
    }

    @Test(dataProvider = "audienceReportInstancesData", dataProviderClass = PublisherReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersByDefaultVerticalReport(AudienceReportTestData testData) {
        AudienceReportPage reportPage = new AudienceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // proper table records
        reportPage.checkReportTypeFilter(testData);
    }
}
