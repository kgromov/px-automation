package configuration.dataproviders;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.TestDataError;
import dto.TestDataException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.campaigns.CampaignTestData;
import px.objects.filters.nodes.DataFilterNode;
import px.objects.filters.FilterManagementTestData;
import px.objects.filters.nodes.FilterNode;
import px.objects.filters.nodes.FilterNodeData;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.DataHelper.getRandomValueFromList;
import static px.objects.campaigns.CampaignTestData.Categories.Lead;
import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;

/**
 * Created by konstantin on 21.10.2016.
 */
public class CampaignDataProvider extends SuperDataProvider {
    private final static Map<String, String> BUYER_CATEGORIES = dataProvider.getPossibleValueFromJSON("BuyerCategories");
    // usage: campaigns report
    public final static Predicate<JSONObject> PING_POST_LEADS_CAMPAIGN = campaign ->
            (campaign.getString("buyerInstanceDeliveryType").contains("PING")
                    || campaign.getString("buyerInstanceDeliveryType").contains("DIRECT"))
                    && (String.valueOf(campaign.get("buyerCategory"))).equals(BUYER_CATEGORIES.get(Lead.name()));
    // usage: lead campaigns with country/vertical
    public final static Predicate<JSONObject> FILTER_MANAGEMENT_CAMPAIGN = campaign ->
            campaign.getString("buyerInstanceName").contains("Campaign Name ")
//                    && campaign.getString("country").equals("US")
                    && (campaign.getString("buyerInstanceDeliveryType").contains("PING")
                    || campaign.getString("buyerInstanceDeliveryType").contains("DIRECT")
            );
    // lead campaigns
    public final static Predicate<JSONObject> LEAD_CAMPAIGN = campaign ->
            campaign.getString("buyerInstanceDeliveryType").contains("POST")
                    || campaign.getString("buyerInstanceDeliveryType").contains("DIRECT");

    @DataProvider
    public static Object[][] positiveCampaignData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            CampaignTestData testData = new CampaignTestData(dataMode);
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
    public static Object[][] negativeCampaignData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            CampaignTestData testData = new CampaignTestData(dataMode);
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
    public static Object[][] editCampaignData() {
        try {
            JSONObject jsonObject = getInstanceDetailsWithDate(CAMPAIGNS_INSTANCE_NAME, "buyerInstanceName", "Campaign Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            // cause campaign name is not updated
            CampaignTestData newData = new CampaignTestData(dataMode);
            newData.setPrevName(String.valueOf(jsonObject.get("buyerInstanceName")));
            CampaignTestData oldData = new CampaignTestData(jsonObject);
            return new Object[][]{
                    {oldData, newData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] editNegativeCampaignData() {
        try {
            JSONObject jsonObject = getInstanceDetailsWithDate(CAMPAIGNS_INSTANCE_NAME, "buyerInstanceName", "Campaign Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            CampaignTestData newData = new CampaignTestData(dataMode);
            newData.setPrevName(String.valueOf(jsonObject.get("buyerInstanceName")));
            CampaignTestData oldData = new CampaignTestData(jsonObject);
            return new Object[][]{
                    {oldData, newData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] deleteCampaignData() {
        try {
            JSONObject jsonObject = getInstanceDetails(CAMPAIGNS_INSTANCE_NAME, "buyerInstanceName", "Campaign Name ");
            CampaignTestData testData = new CampaignTestData(jsonObject);
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

    // =================================== Filter management ===================================
    private static final Set<String> UNMAPPED_FIELDS = new HashSet<>(Arrays.asList(
            "ContactData.CompanyName", "ContactData.ContactDataType", "ContactData.Country",
            "ContactData.HouseName", "ContactData.IMName", "ContactData.PayableToName",
            "ContactData.PositionTitle", "ContactData.ProvinceCode", "ContactData.UserPassword"));

    @DataProvider
    public static Object[][] createFilterManagementContactData() {
        try {
            JSONObject jsonObject = getLeadCampaignWithoutFilter(FILTER_MANAGEMENT_CAMPAIGN);
            CampaignTestData testData = new CampaignTestData(jsonObject);
            // set available filter fields for country/vertical
            FilterManagementTestData filtersData = new FilterManagementTestData(testData.getCountry(), testData.getVertical());
            List<FilterNodeData> data = filtersData.getFields();
            // remove unclear fields
            data.removeIf(nodeData -> UNMAPPED_FIELDS.contains(nodeData.getName()));
            // currently specified tree - root - operand; leaves - ContactData; height = 2
            FilterNode root = new FilterNode();
            List<FilterNode> leaves = data.stream().map(DataFilterNode::new).collect(Collectors.toList());
            root.setChildren(leaves);
            return new Object[][]{
                    {testData, root}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] previewFilterManagementContactData() {
        try {
            JSONObject jsonObject = getLeadCampaignWithoutFilter(FILTER_MANAGEMENT_CAMPAIGN);
            CampaignTestData testData = new CampaignTestData(jsonObject);
            // set available filter fields for country/vertical
            FilterManagementTestData filtersData = new FilterManagementTestData(testData.getCountry(), testData.getVertical());
            List<FilterNodeData> data = filtersData.getFields();
            // remove unclear fields
            data.removeIf(nodeData -> UNMAPPED_FIELDS.contains(nodeData.getName()));
            // currently specified tree - root - operand; leaves - ContactData; height = 2
            FilterNode root = new FilterNode();
            List<FilterNode> leaves = data.stream().map(DataFilterNode::new).collect(Collectors.toList());
            root.setChildren(leaves);
            return new Object[][]{
                    {testData, root}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] pingPostCampaignsData() {
        try {
            Map<String, String> verticalCountries = dataProvider.getPossibleValueFromJSON("VerticalAndCountry");
            List<String> types = Arrays.asList("PINGPOST", "DIRECTPOST");
            String[] verticalAndCountry = getRandomValueFromList(new ArrayList<>(verticalCountries.values())).split("-");
            String country = verticalAndCountry[0];
            String vertical = verticalAndCountry[1].toUpperCase();
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            CampaignTestData testData = new CampaignTestData(dataMode)
                    .withCategory(Lead.name())
                    .withDeliveryType(getRandomValueFromList(types))
                    .withCountry(country)
                    .withVertical(vertical);
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

    public static JSONObject getCampaignByType(Predicate<JSONObject> condition) {
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + CAMPAIGNS_INSTANCE_NAME)
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "buyerInstanceName"),
                        Arrays.asList(period, period, "Campaign Name "))
                .sort("totalSpend", "desc")
                .build().getRequestedURL();
        List<JSONObject> campaigns = JSONWrapper.toList(dataProvider.getDataAsJSONArray(requestedURL));
        Collections.shuffle(campaigns);
        return campaigns.stream().filter(condition).findFirst().orElse(null);
    }

    private static List<JSONObject> getAvailableAutoCampaignsForFilters() {
        Map<String, String> verticalCountries = dataProvider.getPossibleValueFromJSON("VerticalAndCountry");
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/buyerInstancesByVerticalAndCountry")
                .withParams(Collections.singletonList("verticalCountryList"),
                        Collections.singletonList(StringUtils.join(verticalCountries.values(), "|")))
                .build().getRequestedURL();
        JSONArray objects = new JSONArray(dataProvider.getDataAsString(requestedURL));
        List<JSONObject> campaigns = JSONWrapper.toList(objects);
        // extra filtration is required cause in response there are incorrect countries (not from VerticalAndCountry enum)
        List<String> availableCountryVerticals = verticalCountries.values().stream().map(String::toUpperCase).collect(Collectors.toList());
        // filter by country-vertical
        campaigns = campaigns.stream().filter(campaign ->
                availableCountryVerticals.contains(campaign.getString("country").toUpperCase()
                        + "-" + campaign.getString("vertical").toUpperCase())
        ).collect(Collectors.toList());
        Collections.shuffle(campaigns);
        return campaigns.stream().filter(campaign ->
                campaign.getString("buyerInstanceName").contains("Campaign Name ")).collect(Collectors.toList());
    }

    private static JSONObject getLeadCampaignWithoutFilter(Predicate<JSONObject> condition) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/filter")
                .withParams("buyerInstanceGuid", "%s")
                .build().getRequestedURL();
        List<JSONObject> autoCampaigns = getAvailableAutoCampaignsForFilters();
        autoCampaigns = autoCampaigns.stream().filter(condition).collect(Collectors.toList());
        List<JSONObject> campaignsWithFilter = new ArrayList<>();
        autoCampaigns.forEach(campaign -> {
            JSONObject filter = new JSONObject(dataProvider.getDataAsString(String.format(requestedURL, campaign.get("buyerInstanceGuid"))));
            if (!filter.has("filterItems") || filter.getJSONArray("filterItems").length() == 0)
                campaignsWithFilter.add(campaign);
        });
        if (campaignsWithFilter.isEmpty())
            throw new TestDataException("No available campaigns to create filter");
        int index = getRandomInt(campaignsWithFilter.size());
        return campaignsWithFilter.get(index);
    }

    private static JSONObject getLeadCampaignWithFilter(Predicate<JSONObject> condition) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/filter")
                .withParams("buyerInstanceGuid", "%s")
                .build().getRequestedURL();
        List<JSONObject> autoCampaigns = getAvailableAutoCampaignsForFilters();
        autoCampaigns = autoCampaigns.stream().filter(condition).collect(Collectors.toList());
        List<JSONObject> campaignsWithFilter = new ArrayList<>();
        autoCampaigns.forEach(campaign -> {
            JSONObject filter = new JSONObject(dataProvider.getDataAsString(String.format(requestedURL, campaign.get("buyerInstanceGuid"))));
            if (filter.has("filterItems") && filter.getJSONArray("filterItems").length() > 0)
                campaignsWithFilter.add(campaign);
        });
        if (campaignsWithFilter.isEmpty())
            throw new TestDataException("No available campaigns with filter");
        int index = getRandomInt(campaignsWithFilter.size());
        return campaignsWithFilter.get(index);
    }
}
