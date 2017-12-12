package tests.objects.campaigns;

import configuration.dataproviders.CampaignDataProvider;
import org.testng.annotations.Test;
import px.objects.campaigns.CampaignColumnsEnum;
import px.objects.campaigns.CampaignTestData;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.campaigns.pages.CreateCampaignsPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/24/2016.
 */
public class CreateCampaignsTest extends LoginTest {

    @Test(dataProvider = "positiveCampaignData", dataProviderClass = CampaignDataProvider.class)
    public void createCampaignWithPositiveData(CampaignTestData testData) {
        CampaignsPage campaignsPage = new CampaignsPage(pxDriver);
        campaignsPage.navigateToObjects();
        campaignsPage.fillInPage();
        new CreateCampaignsPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
        campaignsPage.redirect(testData)
                // check created in campaigns table -> search by name
                .checkCreatedInstanceByName(testData, CampaignColumnsEnum.NAME.getValue());
    }

    @Test(dataProvider = "negativeCampaignData", dataProviderClass = CampaignDataProvider.class)
    public void createCampaignWithNegativeData(CampaignTestData testData) {
        CampaignsPage campaignsPage = new CampaignsPage(pxDriver);
        campaignsPage.navigateToObjects();
        campaignsPage.fillInPage();
        new CreateCampaignsPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
    }
}
