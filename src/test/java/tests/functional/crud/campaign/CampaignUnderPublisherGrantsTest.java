package tests.functional.crud.campaign;

import configuration.dataproviders.CampaignDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import px.funtional.crud.CampaignRequestData;
import px.funtional.crud.GrantsVerification;
import px.funtional.crud.RequestData;
import px.objects.campaigns.CampaignTestData;
import tests.functional.crud.CampaignGrantsTest;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/29/2017.
 */
public class CampaignUnderPublisherGrantsTest extends CampaignGrantsTest {

    @Test
    public void checkCreateCampaignTest() {
        // select any available buyer
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // create request data with campaign test data
        RequestData requestData = new CampaignRequestData(buyer);
        // check create post request
        GrantsVerification.checkCreateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdateCampaignTest() {
        // select random buyer from all available
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // select any not own campaign
        ObjectIdentityData campaign = ObjectIdentityData.getAnyObjectFromList(campaigns);
        RequestData requestData = new CampaignRequestData(campaign, buyer, false);
        GrantsVerification.checkUpdateRequest(requestData, isAdmin());
    }

    @Test(dataProvider = "pingPostCampaignsData", dataProviderClass = CampaignDataProvider.class, invocationCount = 20, enabled = false)
    public void createPingPostCampaigns(CampaignTestData testData) {
        // select any available buyer
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // create request data with campaign test data
        RequestData requestData = new CampaignRequestData(buyer, testData);
        // check create post request
        GrantsVerification.checkCreateRequest(requestData, isAdmin());
    }
}
