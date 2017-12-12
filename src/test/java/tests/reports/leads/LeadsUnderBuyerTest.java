package tests.reports.leads;

import configuration.dataproviders.BuyerReportsDataProvider;
import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.export.LeadsExportTestData;
import px.reports.leads.LeadsReportPage;
import px.reports.leads.LeadsReportTestData;
import px.reports.leads.LeadsReportUnderBuyerTestData;
import tests.LoginTest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.leads.LeadsReportTestData.BUYER_FILTER;
import static px.reports.leads.LeadsReportTestData.PUBLISHER_FILTER;

/**
 * Created by kgr on 11/17/2016.
 */
public class LeadsUnderBuyerTest extends LoginTest {

    @Test
    public void checkFilterAccordance() {
        LeadsReportUnderBuyerTestData testData = new LeadsReportUnderBuyerTestData(0);
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        // cause there are dependent filters
        // set publisher filter to display publisher instance filter
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER),
                testData.getPublisher().getId() + " - " + testData.getPublisher().getName());
        // set buyer filter to display buyer campaign filter
        reportPage.setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER),
                testData.getBuyer().getName());
        reportPage.checkFiltersAccordance(testData);
    }

    @Test(dataProvider = "leadsReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(LeadsReportTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
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

    @Test(dataProvider = "leadsReportInstancesData", dataProviderClass = BuyerReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(LeadsReportUnderBuyerTestData testData) {
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
        // check all filters
        reportPage.checkAllFilters(testData);
    }
}
