package px.reports.campaigns;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static configuration.helpers.DataHelper.getRandomInt;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignDetailsUnderBuyerTestData extends CampaignDetailsTestData implements TestData {
    private String dailyCap;
    // aggregated map
    protected Map<String, Boolean> editableFieldsMap;
    private boolean isPositive = true;
    // under buyer user
    public static final List<String> ADMIN_MENU_ITEMS = Arrays.asList("API configuration", "Clone campaign");
    public static final List<String> ADMIN_FIELDS = Arrays.asList(
            "qiqCeiling", "buyerCategory", "prevDupDays",
            "floorPayout", "floorScore", "qiqScore", "legName", "className"
    );

    public CampaignDetailsUnderBuyerTestData() {
        super();
        // campaign details data
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud")
                .withParams("buyerInstanceGuid", campaignGUID)
                .build().getRequestedURL();
        this.campaignsDetails = new JSONArray(dataProvider.getDataAsString(requestedURL));
        // aggregate like key-value
        setCampaignsDetails();
        // set fields
        setHeaders();
        // for debug
        log.info(String.format("DEBUG\tjson length = '%s', map size = '%s'", campaignsDetails, campaignDetailsMap));
    }

    public CampaignDetailsUnderBuyerTestData(String campaignGUID) {
        super();
        this.campaignGUID = campaignGUID;
        log.info(String.format("Get campaign by guid '%s' from total '%d'", campaignGUID, allRowsArray.length()));
        this.campaignsObject = DataHelper.getJSONFromJSONArrayByCondition(allRowsArray, "buyerInstanceGuid", campaignGUID);
        // navigation data
        this.campaignName = String.valueOf(campaignsObject.get("buyerInstanceName"));
        // campaign details data
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud")
                .withParams("buyerInstanceGuid", campaignGUID)
                .build().getRequestedURL();
        this.campaignsDetails = new JSONArray(dataProvider.getDataAsString(requestedURL));
        // aggregate like key-value
        setCampaignsDetails();
        // set fields
        setHeaders();
        // for debug
        log.info(String.format("DEBUG\tjson length = '%s', map size = '%s'", campaignsDetails, campaignDetailsMap));
    }

    // edit data
    public String getDailyCap() {
        return dailyCap;
    }

    public Map<String, Boolean> getEditableFieldsMap() {
        return editableFieldsMap;
    }

    protected void setCampaignsDetails() {
        // aggregate like key-value
        this.campaignDetailsMap = new HashMap<>();
        this.editableFieldsMap = new HashMap<>();
        // to map buyerGUID in UI to it's name
        List<ObjectIdentityData> buyers = dataProvider.getCreatedInstancesData("buyers");
        for (int i = 0; i < campaignsDetails.length(); i++) {
            JSONObject object = campaignsDetails.getJSONObject(i);
            if (object.has("field")) {
                try {
                    String key = String.valueOf(object.get("field"));
                    String value = object.has("default") ? String.valueOf(object.get("default")) : "";
                    boolean isEditable = object.has("editable") && Boolean.parseBoolean(String.valueOf(object.get("editable")));
                    // enum index to description
                    value = key.equals("buyerCategory") ? DataHelper.getKeyByValue(buyerCategoriesMap, value) : value;
                    value = key.equals("buyerType") ? DataHelper.getKeyByValue(buyerTypesMap, value) : value;
                    value = key.equals("country") ? DataHelper.getKeyByValue(countriesMap, value) : value;
                    value = key.equals("currency") ? DataHelper.getKeyByValue(currenciesMap, value) : value;
                    // guid -> name
                    this.buyer = ObjectIdentityData.getObjectFromListByGUID(buyers, value);
                    value = key.equals("parentBuyerGuid") ? buyer.getName() : value;
                    campaignDetailsMap.put(key, value);
                    editableFieldsMap.put(key, isEditable);
                } catch (NullPointerException | JSONException e) {
                    log.error(String.format("Unable to handle field: '%s'\tDetails=\n%s", object, e.getMessage()));
                }
            }
        }
        campaignDetailsMap.remove("className");
    }

    public boolean isPositive() {
        return isPositive;
    }

    // to set leg name
    @Override
    public CampaignDetailsUnderBuyerTestData setPositiveData() {
        this.dailyCap = String.valueOf(getRandomInt(-1, 100));
        return this;
    }

    @Override
    public CampaignDetailsUnderBuyerTestData setNegativeData() {
        this.isPositive = false;
        this.dailyCap = String.valueOf(getRandomInt(-100, -1));
        return this;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", dailyCap=" + dailyCap +
                ", editableFieldsMap=" + editableFieldsMap;
    }
}