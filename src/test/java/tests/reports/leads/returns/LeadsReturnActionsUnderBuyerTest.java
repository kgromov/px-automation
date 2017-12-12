package tests.reports.leads.returns;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.objects.leadReturn.SingleLeadReturnsTestData;
import px.reports.leadReturns.LeadReturnReportPage;
import px.reports.leads.LeadsReportPage;
import tests.LoginTest;

import static px.reports.leadReturns.LeadReturnStatusEnum.DECLINED;
import static px.reports.leadReturns.LeadReturnStatusEnum.PENDING;

/**
 * Created by kgr on 9/4/2017.
 */
public class LeadsReturnActionsUnderBuyerTest extends LoginTest {

    @Test(dataProvider = "acceptLeadReturnDataUnderBuyer", dataProviderClass = LeadDataProvider.class)
    public void acceptLeadReturn(SingleLeadReturnsTestData testData) {
        // if no leads returned by certain buyer - request first
        if (testData.getStatus() == null) {
            LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
            reportPage.navigateToPage();
            reportPage.setCustomRanges(testData);
            // set proper items per page
            reportPage.setItemPerPage(100);
            // invoke and fill in lead return form
            reportPage.invokeLeadReturnPopup(testData.getLead())
                    .checkCampaign(testData)
                    .createLeadReturn(testData)
                    .saveInstance(testData, PENDING);
        }
        // should be already logged or relogged in block above (request return)
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        if (testData.getStatus() != null) reportPage.closeWelcomePopup();
        // login as admin to check lead return
        reLoginAsAdmin();
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData);
        // invoke and confirm popup
        reportPage.invokeAcceptPopup(testData);
        // login as buyer
        reLoginAsUser();
        // check that lead is absent in leads overview after accept in returns
        LeadsReportPage leadsReportPage = new LeadsReportPage(pxDriver);
        leadsReportPage.navigateToPage();
        leadsReportPage.checkAcceptedReturnItem(testData);
    }

    @Test(dataProvider = "declineLeadReturnDataUnderBuyer", dataProviderClass = LeadDataProvider.class)
    public void declineLeadReturn(SingleLeadReturnsTestData testData) {
        // if no leads returned by certain buyer - request first
        if (testData.getStatus() == null) {
            LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
            reportPage.navigateToPage();
            reportPage.setCustomRanges(testData);
            // set proper items per page
            reportPage.setItemPerPage(100);
            // invoke and fill in lead return form
            reportPage.invokeLeadReturnPopup(testData.getLead())
                    .checkCampaign(testData)
                    .createLeadReturn(testData)
                    .saveInstance(testData, PENDING);
        }
        // should be already logged or relogged in block above (request return)
        LeadReturnReportPage reportPage = new LeadReturnReportPage(pxDriver);
        if (testData.getStatus() != null) reportPage.closeWelcomePopup();
        // login as admin to check lead return
        reLoginAsAdmin();
        reportPage.navigateToPage();
        // set pending status to accept lead
        reportPage.setPeriodMonthFilter(testData);
        // invoke and fill in decline lead return form
        reportPage.invokeDeclinePopup(testData)
                .checkDecliningLead(testData)
                .declineLeadReturn(testData)
                .saveInstance(testData, DECLINED);
        // login as buyer
        reLoginAsUser();
        // check that lead is absent in leads overview after accept in returns
        LeadsReportPage leadsReportPage = new LeadsReportPage(pxDriver);
        leadsReportPage.navigateToPage();
        leadsReportPage.checkReturnItem(testData, true);
    }
}
