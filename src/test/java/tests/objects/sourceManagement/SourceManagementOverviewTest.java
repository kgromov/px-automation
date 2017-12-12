package tests.objects.sourceManagement;

import configuration.dataproviders.SourceManagementDataProvider;
import org.testng.annotations.Test;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.sourceManagement.SourceManagementOverviewData;
import px.objects.sourceManagement.SourceManagementTestData;
import px.objects.sourceManagement.pages.SourceManagementOverviewPage;
import px.objects.sourceManagement.pages.SourceManagementPage;
import tests.LoginTest;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementOverviewTest extends LoginTest {

    @Test(dataProvider = "sourceManagementOverviewData", dataProviderClass = SourceManagementDataProvider.class)
    public void checkSourceManagementOverview(SourceManagementOverviewData overviewData) {
        // navigate to target campaign with source
        new CampaignsPage(pxDriver)
                .navigateToObjects()
                .navigateToSourceManagement(overviewData.getCampaign());
        SourceManagementOverviewPage overviewPage = new SourceManagementOverviewPage(pxDriver);
        // check all cells
        overviewPage.checkAllCells(overviewData);
    }

    @Test(dataProvider = "deleteSourceData", dataProviderClass = SourceManagementDataProvider.class)
    public void deleteSourceInCampaignSourceManagement(SourceManagementTestData testData) {
        // navigate to target campaign with source
        SourceManagementPage sourceManagementPage = new CampaignsPage(pxDriver)
                .navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign());
        SourceManagementOverviewPage overviewPage = new SourceManagementOverviewPage(pxDriver);
        // if no sources - create before
        if (!testData.hasSources()) {
            sourceManagementPage.createInstance(testData)
                    .saveInstance(testData);
            overviewPage.checkSourcePresence(testData);
        }
        overviewPage.deleteSource(testData.getSourceID());
        overviewPage.checkSourceAbsence(testData);
    }
}
