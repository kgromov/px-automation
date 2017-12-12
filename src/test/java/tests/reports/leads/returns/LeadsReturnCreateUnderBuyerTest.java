package tests.reports.leads.returns;

import configuration.dataproviders.LeadDataProvider;
import org.testng.annotations.Test;
import px.objects.leadReturn.SingleLeadReturnsTestData;
import px.reports.leadReturns.LeadReturnReportPage;
import px.reports.leadReturns.LeadReturnsReportTestData;
import px.reports.leads.LeadsReportPage;
import tests.LoginTest;

import static px.reports.leadReturns.LeadReturnStatusEnum.PENDING;

/**
 * Created by kgr on 9/4/2017.
 */
public class LeadsReturnCreateUnderBuyerTest extends LoginTest {

    @Test(dataProvider = "returnLeadWithPositiveDataUnderBuyer", dataProviderClass = LeadDataProvider.class)
    public void requestLeadRetunWithPositiveData(SingleLeadReturnsTestData testData, LeadReturnsReportTestData reportData) {
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
        // check that 'Return lead' item disappeared
        reportPage.checkReturnItem(testData.getLead(), false);
        // login as admin to check lead return
        reLoginAsAdmin();
        // check that new row in lead returns report page
        LeadReturnReportPage leadReturnReportPage = new LeadReturnReportPage(pxDriver);
        leadReturnReportPage.navigateToPage();
        leadReturnReportPage.checkSingleLeadReturn(testData, reportData);
//        leadReturnReportPage.checkSingleLeadReturn(testData);
    }

    @Test(dataProvider = "returnLeadWithNegativeDataUnderBuyer", dataProviderClass = LeadDataProvider.class)
    public void  requestLeadRetunWithNegativeData(SingleLeadReturnsTestData testData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRanges(testData);
        // set proper items per page
        reportPage.setItemPerPage(100);
        // invoke and fill in lead return form
        reportPage.invokeLeadReturnPopup(testData.getLead())
                .createLeadReturn(testData)
                .saveInstance(testData, PENDING);
    }
}
