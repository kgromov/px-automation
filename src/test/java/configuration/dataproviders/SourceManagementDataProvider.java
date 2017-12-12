package configuration.dataproviders;

import dto.ObjectIdentityData;
import dto.TestDataError;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.sourceManagement.SourceManagementOverviewData;
import px.objects.sourceManagement.SourceManagementTestData;
import px.reports.outbound.OutboundLeadFilterData;

import static configuration.dataproviders.CampaignDataProvider.PING_POST_LEADS_CAMPAIGN;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementDataProvider extends SuperDataProvider {

    @DataProvider
    public static Object[][] leadInsertSourceManagementData() {
        try {
            // DEBUG, cause of broken lookups
           /* JSONObject outbound = new JSONObject("{\n" +
                    "\t\t\"postDate\": \"2017-11-14T23:53:56\",\n" +
                    "\t\t\"transactionId\": \"F62FCFAF-D0E3-4E0F-8199-CF1762FCBFB5\",\n" +
                    "\t\t\"publisherId\": 175,\n" +
                    "\t\t\"sourceId\": \"93_175_0a9\",\n" +
                    "\t\t\"subId\": \"da809ba2-7e0a-4b47-a793-fca218cf70a9\",\n" +
                    "\t\t\"payout\": 3.67,\n" +
                    "\t\t\"email\": \"a885eb30d1972bca@stagingpx.com\",\n" +
                    "\t\t\"buyerName\": \"Ernst HomeSecurity PP\",\n" +
                    "\t\t\"postType\": \"POST\",\n" +
                    "\t\t\"buyerPostResultCode\": \"Success\",\n" +
                    "\t\t\"status\": \"Published\",\n" +
                    "\t\t\"leadGuid\": \"EA60C7EA-FE1B-4F02-A0FF-185A6FC36217\",\n" +
                    "\t\t\"leadId\": 3650604,\n" +
                    "\t\t\"campaignId\": 93,\n" +
                    "\t\t\"campaignName\": \"homesecurity-zipcode\",\n" +
                    "\t\t\"buyerId\": 51,\n" +
                    "\t\t\"responseId\": \"fb84a5e0-df84-4492-a81b-34f043a11c10\",\n" +
                    "\t\t\"leadResponseId\": \"3CDD5940-10E6-46D2-ACF3-3AC3A1A3F447\",\n" +
                    "\t\t\"buyerPostResultCodeId\": 1,\n" +
                    "\t\t\"payoutAccepted\": \"1\",\n" +
                    "\t\t\"netPayout\": 3.67,\n" +
                    "\t\t\"source\": \"5c13cb9f-b080-497e-84ec-68a1bfb4b476\",\n" +
                    "\t\t\"leadCreationDate\": \"2017-11-14T23:53:56\",\n" +
                    "\t\t\"detailsAvailable\": true\n" +
                    "\t}");
            JSONObject inbound = new JSONObject();
            inbound.put("publisherId", "175");
            inbound.put("vertical", "homesecurity");
            String requestedURL2 = new RequestedURL.Builder()
                    .withRelativeURL("api/leadpreview/inbound")
                    .withParams("leadGuid", String.valueOf(outbound.get("leadGuid")))
                    .build().getRequestedURL();
            JSONArray jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL2)).getJSONArray();
            JSONObject xmlBody = JSONWrapper.toList(jsonArray).stream().filter(field ->
                    field.has("field") && field.getString("field").equals("XmlBody"))
                    .findFirst().orElse(null);
            String xmlData = xmlBody != null ? xmlBody.getString("default") : null;
            SourceManagementTestData testData = new SourceManagementTestData(
                    outbound, inbound, xmlData)
                    .setPositiveData();*/
            OutboundLeadFilterData filterData = new OutboundLeadFilterData();
            SourceManagementTestData testData = new SourceManagementTestData(
                    filterData.getOutboundTransaction(),
                    filterData.getInboundTransaction(),
                    filterData.getXmlData())
                    .setPositiveData();
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
    public static Object[][] sourceManagementOverviewData() {
        // buyerId is required
        try {
            JSONObject object = CampaignDataProvider.getCampaignByType(PING_POST_LEADS_CAMPAIGN);
            ObjectIdentityData campaign = new ObjectIdentityData(
                    String.valueOf(object.get("buyerInstanceID")),
                    String.valueOf(object.get("buyerInstanceName")),
                    String.valueOf(object.get("buyerInstanceGuid"))
            );
            SourceManagementOverviewData overviewData = new SourceManagementOverviewData(campaign);
            return new Object[][]{
                    {overviewData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] createSourceWithPositiveData() {
        try {
            SourceManagementTestData testData = new SourceManagementTestData()
                    .setExistedSources().setPositiveData();
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
    public static Object[][] createSourceWithNegativeData() {
        try {
            SourceManagementTestData testData = new SourceManagementTestData()
                    .setNegativeData();
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
    public static Object[][] deleteSourceData() {
        try {
            SourceManagementTestData testData = new SourceManagementTestData()
                    .setExistedSources();
            if (!testData.hasSources()) testData.setPositiveData();
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
}
