package tests.reports.leads.returns;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.reports.leadReturns.LeadReturnReportPage;
import px.reports.leadReturns.LeadReturnsReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 9/4/2017.
 */
public class LeadsReturnReportTest extends LoginTest {

    @Test(dataProvider = "leadsReturnReportData", dataProviderClass = LeadDataProvider.class, testName = "checkCommonReport")
    public void checkCommonReport(LeadReturnsReportTestData testData, String reportType) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set period month filter
        reportPage.setReportTypeFilter(testData);
        // check select all
        if (testData.getItemsByReportTypeCount() > 0)
            reportPage.checkSelectAllColumn();
        // pagination
        if (testData.getItemsByReportTypeCount() > 0)
            reportPage.checkPagination(testData, testData.getItemsByReportTypeCount());
    }

    @Test(dataProvider = "leadsReturnReportData", dataProviderClass = LeadDataProvider.class, testName = "leadsReturnReportData")
    public void checkInstancesFiltersReport(LeadReturnsReportTestData testData, String reportType) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set period month filter
        reportPage.setReportTypeFilter(testData);
        // check items by period month filter
        reportPage.checkPeriodMonthFilter(testData);
        // check items by report table filter
        reportPage.checkReportTypeFilter(testData);
    }
}