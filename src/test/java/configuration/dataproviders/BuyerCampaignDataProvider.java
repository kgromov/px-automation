package configuration.dataproviders;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.TestDataError;
import org.json.JSONArray;
import org.testng.annotations.DataProvider;
import px.reports.campaigns.CampaignDetailsUnderBuyerTestData;
import utils.FailListener;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by kgr on 6/26/2017.
 */
public class BuyerCampaignDataProvider extends SuperDataProvider {
    // change logic to all buyer campaigns all 5 if there are more
    @DataProvider
    public static Object[][] campaignUnderBuyerDetailsReportData() {
        try {
            checkBuyerUnderBuyerUser();
            List<String> campaignGUIDs = getBuyerCampaignsGUID();
            Object[][] objects = new Object[campaignGUIDs.size()][1];
            for (int i = 0; i < campaignGUIDs.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    CampaignDetailsUnderBuyerTestData testData = new CampaignDetailsUnderBuyerTestData(campaignGUIDs.get(i));
                    objects[i][0] = testData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                }
            }
            FailListener.OWN_INVOCATION_COUNT = 0;
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] editCampaignUnderBuyerData() {
        try {
            checkBuyerUnderBuyerUser();
            CampaignDetailsUnderBuyerTestData testData = new CampaignDetailsUnderBuyerTestData();
            testData.setPositiveData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] editCampaignUnderBuyerNegativeData() {
        try {
            checkBuyerUnderBuyerUser();
            CampaignDetailsUnderBuyerTestData testData = new CampaignDetailsUnderBuyerTestData();
            testData.setNegativeData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    private static List<String> getBuyerCampaignsGUID() {
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstances/report")
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(period, period))
                .sort("totalSpend", "desc")
                .build().getRequestedURL();
        JSONArray jsonArray = dataProvider.getDataAsJSONArray(requestedURL);
        List<String> campaignGUIDs = DataHelper.getListFromJSONArrayByKey(jsonArray, "buyerInstanceGuid");
        return campaignGUIDs.size() > 5 ? campaignGUIDs.subList(0, 6) : campaignGUIDs;
    }
}
