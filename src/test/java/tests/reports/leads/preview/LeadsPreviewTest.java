package tests.reports.leads.preview;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.objects.leads.LeadsPreviewPage;
import px.objects.leads.LeadsTestData;
import px.reports.leads.LeadsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsPreviewTest extends LoginTest {
//    private String url = super.url = "http://stage05-ui.stagingpx.com/"; // "http://rvmd-12094-ui.stagingpx.com/";

    @Test(dataProvider = "leadPreviewData", dataProviderClass = LeadDataProvider.class, invocationCount = 5)
    public void checkLeadDetails(LeadsTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose lead in leads report and preview to it
        LeadsPreviewPage leadsPreviewPage = reportPage.navigateToPage(testData.getLead());
        // check lead details with response
        leadsPreviewPage.checkLeadDetails(testData);
        // check lead transactions
        leadsPreviewPage.checkLeadTransactions(testData);
    }
}