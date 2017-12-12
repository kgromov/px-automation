package tests.reports.leads;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.export.LeadsExportTestData;
import px.reports.leads.LeadsReportPage;
import px.reports.leads.LeadsReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class LeadsReportTest extends LoginTest {

    @Test(dataProvider = "leadsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(LeadsReportTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        /* set proper data range from test data */
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "leadsReportExportData", dataProviderClass = ExportReportsDataProvider.class)
    public void checkExportReport(LeadsExportTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport(testData);
    }

    @Test(dataProvider = "leadsReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(LeadsReportTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
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
        reportPage.checkOffersFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}
