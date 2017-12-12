package tests.functional.crud.campaign;

import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import px.funtional.crud.CampaignRequestData;
import px.funtional.crud.GrantsVerification;
import px.funtional.crud.RequestData;
import tests.functional.crud.CampaignGrantsTest;

import java.util.Collections;
import java.util.List;

import static config.Config.isAdmin;
import static config.Config.user;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kgr on 6/29/2017.
 */
public class CampaignUnderBuyerGrantsTest extends CampaignGrantsTest {

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
    public void checkUpdateOwnCampaignTest() {
        // set buyers under buyer user
        this.buyers = userDataProvider.getCreatedInstancesData("buyers");
        // and check that there is only 1
        assertThat(String.format("There is 1 buyer under buyer\tuser: '%s'", user),
                buyers.size(), equalTo(1));
        // select available buyer
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // select campaign under buyer
        ObjectIdentityData campaign = new ObjectIdentityData(
                userDataProvider.getAnyJSONObjectFromArray(requestedURL));
        // create request data with campaign test data
        RequestData requestData = new CampaignRequestData(campaign, buyer);
        // check update post request
        GrantsVerification.checkUpdateRequest(requestData, true);
    }

    @Test
    public void checkUpdateNotOwnCampaignTest() {
        // select random buyer from all available
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // campaigns under buyer
        List<ObjectIdentityData> userCampaigns = ObjectIdentityData.getObjectsByJSONArray(
                userDataProvider.getDataAsJSONArray(requestedURL));
        List<String> userCampaignGUIDs = ObjectIdentityData.getAllGUIDs(userCampaigns);
        List<String> campaignGUIDs = ObjectIdentityData.getAllGUIDs(campaigns);
        // exclude buyer own campaigns
        campaignGUIDs.removeAll(userCampaignGUIDs);
        // change remain campaigns order
        Collections.shuffle(campaignGUIDs);
        // select any not own campaign
        String campaignGUID = campaignGUIDs.get(0);
        ObjectIdentityData campaign = ObjectIdentityData.getObjectFromListByGUID(campaigns, campaignGUID);
        RequestData requestData = new CampaignRequestData(campaign, buyer, false);
        GrantsVerification.checkUpdateRequest(requestData, true);
    }
}
