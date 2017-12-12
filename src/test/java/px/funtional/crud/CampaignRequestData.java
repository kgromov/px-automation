package px.funtional.crud;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.campaigns.CampaignTestData;

import java.util.*;

import static config.Config.isAdmin;
import static config.Config.isBuyer;

/**
 * Created by kgr on 6/29/2017.
 */
public final class CampaignRequestData implements RequestData {
    private CampaignTestData testData;
    private ObjectIdentityData identityData;
    private ObjectIdentityData parentData;
    private boolean isUserOwner;

    public CampaignRequestData(ObjectIdentityData parentData, CampaignTestData testData) {
        this.parentData = parentData;
        this.testData = testData;
        System.out.println(String.format("Parent = %s, test data name = %s", parentData, testData.getName()));
    }

    public CampaignRequestData(ObjectIdentityData parentData) {
        this.parentData = parentData;
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new CampaignTestData(dataMode);
    }

    public CampaignRequestData(ObjectIdentityData identityData, ObjectIdentityData parentData) {
        this(identityData, parentData, true);
    }

    public CampaignRequestData(ObjectIdentityData identityData, ObjectIdentityData parentData, boolean isUserOwner) {
        this.identityData = identityData;
        this.parentData = parentData;
        this.isUserOwner = isUserOwner;
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new CampaignTestData(dataMode);
    }

    @Override
    public String createURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud").build()
                .getRequestedURL();
    }

    @Override
    public String updateURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/update").build()
                .getRequestedURL();
    }

    @Override
    public String getURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud")
                .withParams("buyerInstanceGuid", identityData.getGuid())
                .build().getRequestedURL();
    }

    @Override
    public String getIdKey() {
        return "buyerInstanceGuid";
    }

    @Override
    public Set<String> allowedFieldsToUpdate() {
        Set<String> keys = asJSON().keySet();
        keys.remove("parentBuyerGuid");
        return isAdmin() ? keys : isUserOwner && isBuyer() ? Collections.singleton("dailyCap") : new HashSet<>();
    }

    @Override
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        object.put("country", testData.getCountriesMap()
                .get(testData.getCountry()));                          // enumDescription
        object.put("currency", testData.getCurrenciesMap()
                .get(testData.getCurrency()));                         // enumDescription
        object.put("buyerInstanceName", testData.getName());
        object.put("buyerCategory", testData.getBuyerCategoryMap()
                .get(testData.getBuyerCategory()));                    // enumSequenceNumber
        object.put("buyerType", testData.getBuyerTypeMap()
                .get(testData.getBuyerType()));                        // enumSequenceIndex
        object.put("vertical", testData.getVertical());
        object.put("buyerInstanceDeliveryType", testData.getDeliveryType());
        object.put("prevDupDays", testData.getDaysForDuplicate());     // admin
        object.put("legName", testData.getLegName());                  // admin
        object.put("tier", testData.getBuyerTierMap()
                .get(testData.getBuyerTier()));                        // enumSequenceIndex or Description
        object.put("directBid", testData.getDirectBID());
        object.put("payoutQuality", testData.getPayoutQuality());
        object.put("floorPayout", testData.getFlorPayout());           // admin
        object.put("fixedLRPerc", testData.getMaxLR());
        object.put("incLRinPayoutQualityPerc", testData.getIncLRInPayoutQuality());
        object.put("monthlyCap", testData.getMonthlyPayoutCap());
        object.put("dailyCap", testData.getDailyLeadsCap());
        object.put("floorScore", testData.getFloorScore());            // admin
        object.put("qiqScore", testData.getSourceQualityFloor());      // admin
        object.put("qiqCeiling", testData.getPublisherScoreCelling()); // admin
        // parent
        object.put("parentBuyerGuid", parentData.getGuid());
        // update
        if (identityData != null) {
            object.put("buyerInstanceID", identityData.getId());
            object.put("buyerInstanceGuid", identityData.getGuid());
        }
        return object;
    }

    @Override
    public Map<String, String> asMap(String requestedURL) {
        JSONArray updateDetails = new JSONArray(dataProvider.getDataAsString(requestedURL));
        Map<String, String> objectDetails = new HashMap<>(updateDetails.length());
        for (int i = 0; i < updateDetails.length(); i++) {
            JSONObject object = updateDetails.getJSONObject(i);
            if (object.has("field")) {
                try {
                    String key = String.valueOf(object.get("field"));
                    String value = object.has("default") ? String.valueOf(object.get("default")) : "";
                    objectDetails.put(key, value);
                } catch (NullPointerException | JSONException e) {
                    System.out.println(String.format("Unable to handle field: '%s'\tDetails=\n%s", object, e.getMessage()));
                }
            }
        }
        return objectDetails;
    }
}