package tests.reports.leads.returns;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.objects.leadReturn.SingleLeadReturnsTestData;
import px.reports.leadReturns.LeadReturnReportPage;
import px.reports.leads.LeadsReportPage;
import tests.LoginTest;

import java.util.List;

import static px.reports.leadReturns.LeadReturnStatusEnum.DECLINED;

/**
 * Created by kgr on 9/4/2017.
 */
public class LeadsReturnActionsTest extends LoginTest {

    @Test(dataProvider = "acceptLeadReturnData", dataProviderClass = LeadDataProvider.class)
    // as verification - change status from pending to accepted
    // if buyer user -> disappear from leads
    public void acceptLeadReturn(SingleLeadReturnsTestData testData) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData);
        // invoke and confirm popup
        reportPage.invokeAcceptPopup(testData);
        // check that new row in lead returns report page with 'Accepted' status
        reportPage.checkSingleLeadReturn(testData);
        // check that there are no actions
        reportPage.checkNoActions();
    }

    @Test(dataProvider = "declineLeadReturnWithPositiveData", dataProviderClass = LeadDataProvider.class)
    // as verification - change status from pending to decline
    public void declineLeadReturnWithPositiveData(SingleLeadReturnsTestData testData) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData);
        // invoke and fill in decline lead return form
        reportPage.invokeDeclinePopup(testData)
                .checkDecliningLead(testData)
                .declineLeadReturn(testData)
                .saveInstance(testData, DECLINED);
        // check that new row in lead returns report page with 'Accepted' status
        reportPage.checkSingleLeadReturn(testData);
        // check that there are no actions
        reportPage.checkNoActions();
        // check that lead on leads report does have 'Return lead' item in actions
        LeadsReportPage leadsReportPage = new LeadsReportPage(pxDriver);
        leadsReportPage.navigateToPage();
//        leadsReportPage.setCustomRanges(testData);
        leadsReportPage.checkReturnItem(testData, true);
    }

    @Test(dataProvider = "declineLeadReturnWithNegativeData", dataProviderClass = LeadDataProvider.class)
    // as verification - change status from pending to decline
    public void declineLeadReturnWithNegativeData(SingleLeadReturnsTestData testData) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData);
        // invoke and fill in decline lead return form
        reportPage.invokeDeclinePopup(testData)
                .checkDecliningLead(testData)
                .declineLeadReturn(testData)
                .saveInstance(testData, DECLINED);
    }

    @Test(dataProvider = "acceptMultipleLeadReturnData", dataProviderClass = LeadDataProvider.class)
    public void acceptMultipleLeadReturn(List<SingleLeadReturnsTestData> testData) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData.get(0));
        // invoke and confirm popup
        reportPage.invokeAcceptPopup(testData);
        // check that new row in lead returns report page with 'Accepted' status
        reportPage.checkSingleLeadReturn(testData);
        // check that there are no actions
        reportPage.checkNoActionButtons();
    }

    @Test(dataProvider = "declineMultipleLeadReturnWithPositiveData", dataProviderClass = LeadDataProvider.class)
    public void declineMultipleLeadReturnWithPositiveData(List<SingleLeadReturnsTestData> testData) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData.get(0));
        // invoke and fill in decline lead return form
        reportPage.invokeDeclinePopup(testData)
                .checkDecliningLead(testData)
                .declineLeadReturn(testData.get(0))
                .saveInstance(testData.get(0), DECLINED);
        // check that new row in lead returns report page with 'Accepted' status
        reportPage.checkSingleLeadReturn(testData);
        // check that there are no actions
        reportPage.checkNoActions();
        // check that lead on leads report does have 'Return lead' item in actions
        LeadsReportPage leadsReportPage = new LeadsReportPage(pxDriver);
        leadsReportPage.navigateToPage();
//        leadsReportPage.setCustomRanges(testData);
        testData.forEach(item -> leadsReportPage.checkReturnItem(item, true));
    }

    @Test(dataProvider = "declineMultipleLeadReturnWithNegativeData", dataProviderClass = LeadDataProvider.class)
    public void declineMultipleLeadReturnWithNegativeData(List<SingleLeadReturnsTestData> testData) {
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData.get(0));
        // invoke and fill in decline lead return form
        reportPage.invokeDeclinePopup(testData)
                .checkDecliningLead(testData)
                .declineLeadReturn(testData.get(0))
                .saveInstance(testData.get(0), DECLINED);
    }
}
