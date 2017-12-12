package tests.objects.filterManagement;

import configuration.dataproviders.CampaignDataProvider;
import org.testng.annotations.Test;
import px.objects.campaigns.CampaignTestData;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.filters.FilterManagementPage;
import px.objects.filters.FilterManagementTestData;
import px.objects.filters.nodes.FilterNode;
import tests.LoginTest;

/**
 * Created by kgr on 10/24/2017.
 */
public class CreateCampaignsFilterTest extends LoginTest {

    @Test(dataProvider = "createFilterManagementContactData", dataProviderClass = CampaignDataProvider.class)
    public void createContactDataCampaignFilter(CampaignTestData testData, FilterNode filterTreeData) {
        CampaignsPage campaignsPage = new CampaignsPage(pxDriver);
        campaignsPage.navigateToObjects();
        // build filter tree
        FilterManagementPage filterPage = campaignsPage.navigateToFilterManagement(testData)
                .createContactDataFilter(filterTreeData)
                .saveFilter();
        // check that filter was created
        FilterManagementTestData.checkFilterFile(testData.getId());
        // check in UI by expected data
        filterPage.checkContactDataFilter(filterTreeData);
    }
}
