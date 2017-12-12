package tests.reports.leads.preview;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.objects.leads.buyer.LeadsPreviewUnderBuyerPage;
import px.objects.leads.buyer.LeadsUnderBuyerTestData;
import px.reports.leads.LeadsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsPreviewUnderBuyerTest extends LoginTest {

    @Test(dataProvider = "leadPreviewUnderBuyerData", dataProviderClass = LeadDataProvider.class, invocationCount = 5)
    public void checkLeadDetails(LeadsUnderBuyerTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose lead in leads report and preview to it
        LeadsPreviewUnderBuyerPage leadsPreviewPage = new LeadsPreviewUnderBuyerPage(pxDriver);
        leadsPreviewPage.navigateToPage(testData.getLead());
        // check lead details with response
        leadsPreviewPage.checkLeadDetails(testData);
    }
}