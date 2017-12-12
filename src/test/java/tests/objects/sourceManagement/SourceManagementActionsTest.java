package tests.objects.sourceManagement;

import configuration.dataproviders.SourceManagementDataProvider;
import dto.LeadResponse;
import org.testng.annotations.Test;
import px.funtional.lead.LeadDataUtils;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.sourceManagement.SourceManagementTestData;
import px.objects.sourceManagement.pages.SourceManagementOverviewPage;
import px.objects.sourceManagement.pages.SourceManagementPage;
import px.objects.users.ContactTestData;
import px.reports.outbound.OutboundTransactionsReportPage;
import tests.LoginTest;

import static px.objects.sourceManagement.SourceManagementTestData.getSourceFileLastModified;
import static px.objects.sourceManagement.SourceManagementTestData.waitSourceFileUpdated;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementActionsTest extends LoginTest {

    @Test(dataProvider = "createSourceWithPositiveData", dataProviderClass = SourceManagementDataProvider.class)
    public void createSourceWithPositiveData(SourceManagementTestData testData) {
        // navigate to target campaign with source
        SourceManagementPage sourceManagementPage = new CampaignsPage(pxDriver)
                .navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign());
        // create new source
        sourceManagementPage.createInstance(testData)
                .saveInstance(testData);
        new SourceManagementOverviewPage(pxDriver).checkSourcePresence(testData);
    }

    @Test(dataProvider = "createSourceWithNegativeData", dataProviderClass = SourceManagementDataProvider.class)
    public void createSourceWithNegativeData(SourceManagementTestData testData) {
        // navigate to target campaign with source
        SourceManagementPage sourceManagementPage = new CampaignsPage(pxDriver)
                .navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign());
        // create new source
        sourceManagementPage.createInstance(testData)
                .saveInstance(testData);
    }

    /* Flow test with source:
   * 1) Input data (cds campaign, buyer campaign, lead) is taken from outbound report;
   * 2) LeadGUID should be present in inbound report either;
   * 3) Save leadData (currently xml only) by leadGUID;
   * 4) Any changes with source (block, prioritize) are applied for buyer campaign only;
   * 5) Before those changes applied there should some timeout up to 6 minutes (till file \\px001-c01\g$\demo_02\virtualstore\allpublishertobuyerexceptions.csv is modified)
   */
    @Test(dataProvider = "leadInsertSourceManagementData", dataProviderClass = SourceManagementDataProvider.class)
    public void sourceFlowWithLeadInsert(SourceManagementTestData testData) {
        ContactTestData contactData = new ContactTestData();
        CampaignsPage campaignsPage = new CampaignsPage(pxDriver);
        // make wrapper - see usage in LeadDataUtils
        LeadResponse response = LeadDataUtils.insertLeadXmlWithEmail(contactData, testData.getLeadData());
        // check success transaction in outbound report
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.checkTransaction(testData, contactData.getEmail(), true);
        // block source
        long lastModified = getSourceFileLastModified(testData.getAllSubIDsSource());
        // navigate to target campaign with source
        campaignsPage.navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign())
                // block source
                .setSource(testData.getAllSubIDsSource())
                .blockSource()
                .saveInstance(testData);
        // there is some timeout (up to 6 minutes while source changes will be applied)
        waitSourceFileUpdated(testData.getAllSubIDsSource(), lastModified);
        // insert lead
        response = LeadDataUtils.insertLeadXmlWithEmail(contactData, testData.getLeadData());
        // check it's not success in outbound
        reportPage.navigateToPage();
        reportPage.checkTransaction(testData, contactData.getEmail(), false);
        // save source last modified
        lastModified = getSourceFileLastModified(testData.getAllSubIDsSource());
        // prioritize source
        campaignsPage.navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign())
                // cherry pick + prioritize
                .setSource(testData.getMostMatchesSource())
                .cherryPickSource(true)
                .prioritizeSource()
                .saveInstance(testData);
        // there is some timeout (up to 6 minutes while source changes will be applied)
        waitSourceFileUpdated(testData.getAllSubIDsSource(), lastModified);
        // insert lead
        response = LeadDataUtils.insertLeadXmlWithEmail(contactData, testData.getLeadData());
        // check it's success in outbound
        reportPage.navigateToPage();
        reportPage.checkTransaction(testData, contactData.getEmail(), true);
        // save source last modified
        lastModified = getSourceFileLastModified(testData.getAllSubIDsSource());
        // navigate to campaign source management
        campaignsPage.navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign());
        // delete creates sources
        SourceManagementOverviewPage overviewPage = new SourceManagementOverviewPage(pxDriver);
        overviewPage.deleteSource(testData.getAllSubIDsSource());
        overviewPage.deleteSource(testData.getMostMatchesSource());
        // there is some timeout (up to 6 minutes while source changes will be applied)
        waitSourceFileUpdated(testData.getAllSubIDsSource(), lastModified);
        // insert lead
        response = LeadDataUtils.insertLeadXmlWithEmail(contactData, testData.getLeadData());
        reportPage.navigateToPage();
        reportPage.checkTransaction(testData, contactData.getEmail(), true);
    }
}
