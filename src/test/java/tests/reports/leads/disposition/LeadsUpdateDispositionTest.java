package tests.reports.leads.disposition;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.objects.disposition.DispositionPage;
import px.objects.disposition.DispositionTestData;
import px.objects.leads.LeadsPreviewPage;
import px.reports.leads.LeadsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/18/2017.
 */
public class LeadsUpdateDispositionTest extends LoginTest {
//    private String url = super.url = "http://stage05-ui.stagingpx.com/"; // "http://rvmd-12094-ui.stagingpx.com/";

    @Test(dataProvider = "positiveDispositionHistoryData", dataProviderClass = LeadDataProvider.class, invocationCount = 3)
    public void updateDispositionWithPositiveData(DispositionTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose lead in leads report and preview to it
        DispositionPage dispositionPage = new DispositionPage(pxDriver)
                // invoke 'Update disposition' popup
                .invokeUpdateDispositionPopup(testData.getLead())
                // check email - campaign table
                .checkSelectedLeadsTable(testData.getLead())
                // update disposition with new data (new row in history)
                .updateDisposition(testData);
        // update disposition history json array response
        testData.setDispositionHistory();
        // check history
        LeadsPreviewPage leadsPreviewPage = reportPage.navigateToPage(testData.getLead());
        dispositionPage.checkDispositionHistoryTable(testData);
        leadsPreviewPage.checkDispositionHistory(testData);
    }

    @Test(dataProvider = "negativeDispositionHistoryData", dataProviderClass = LeadDataProvider.class, invocationCount = 3)
    public void updateDispositionWithNegativeData(DispositionTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose lead in leads report and preview to it
        DispositionPage dispositionPage = new DispositionPage(pxDriver)
                // invoke 'Update disposition' popup
                .invokeUpdateDispositionPopup(testData.getLead())
                // check email - campaign table
                .checkSelectedLeadsTable(testData.getLead())
                // update disposition with new data (new row in history)
                .updateDisposition(testData);
        // check expected errors
        dispositionPage.checkErrorMessage(testData);
    }

    @Test(dataProvider = "updateDispositionForMultipleLeadsWithPositiveData", dataProviderClass = LeadDataProvider.class, invocationCount = 3)
    public void updateDispositionForMultipleLeadsWithPositiveData(DispositionTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // choose lead in leads report and preview to it
        DispositionPage dispositionPage = new DispositionPage(pxDriver)
                // invoke 'Update disposition' popup
                .invokeUpdateDispositionPopup(testData.getLeads())
                // check email - campaign table
                .checkSelectedLeadsTable(testData.leadsAsArray())
                // update disposition with new data (new row in history)
                .updateDisposition(testData);
        // check history of all selected leads
        testData.getLeads().forEach(lead->{
            // navigate to preview
            reportPage.navigateToPage(lead);
            // check disposition history with new row
            dispositionPage.checkDispositionHistoryTable(testData);
        });
    }
}
