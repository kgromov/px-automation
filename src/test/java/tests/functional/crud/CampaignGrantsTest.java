package tests.functional.crud;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.testng.annotations.BeforeClass;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by kgr on 6/29/2017.
 */
public abstract class CampaignGrantsTest extends GrantsTest {
    protected List<ObjectIdentityData> buyers;
    protected List<ObjectIdentityData> campaigns;
    protected String requestedURL;

    @BeforeClass
    protected void setObjects() {
        // set all available buyers by admin user
        this.buyers = adminDataProvider.getCreatedInstancesData("buyers");
        // select only automation created to prevent possible updates
        this.buyers = ObjectIdentityData.getObjectsByName(buyers, "Buyer Name ");
        // set all available campaigns by admin user
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        this.requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstances/report")
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "buyerInstanceName"),
                        Arrays.asList(period, period, "Campaign Name "))
                .sort("totalSpend", "asc")
                .build().getRequestedURL();
        JSONArray jsonArray = adminDataProvider.getDataAsJSONArray(requestedURL);
        // filter automation created only to prevent changes
        this.campaigns = ObjectIdentityData.getObjectsByJSONArray(jsonArray);
    }
}
