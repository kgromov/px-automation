package px.reports.campaigns;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.FieldFormatObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static config.Config.isAdmin;
import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;
import static px.reports.dto.FieldFormatObject.*;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignDetailsTestData extends ReportTestData {
    protected ObjectIdentityData buyer;
    protected String campaignName;
    protected String campaignGUID;
    protected String buyerName;
    protected JSONObject campaignsObject;
    protected JSONArray campaignsDetails;
    // initial data
    Map<String, String> buyerCategoriesMap = dataProvider.getPossibleValueFromJSON("BuyerCategories");
    Map<String, String> buyerTypesMap = dataProvider.getPossibleValueFromJSON("BuyerTypes", "enumMask");
    Map<String, String> countriesMap = dataProvider.getPossibleValueFromJSON("Countries", "enumDescription");
    Map<String, String> currenciesMap = dataProvider.getPossibleValueFromJSON("Currencies", "enumDescription");
    // aggregated map
    protected Map<String, String> campaignDetailsMap;

    static {
        missedHeadersMetricsMap.put("directBid", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("floorPayout", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("monthlyCap", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("payoutQuality", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("fixedLRPerc", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("incLRinPayoutQualityPerc", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("dailyCap", NUMBER_FORMAT);
        missedHeadersMetricsMap.put("prevDupDays", NUMBER_FORMAT);
        missedHeadersMetricsMap.put("floorScore", NUMBER_FORMAT);
        missedHeadersMetricsMap.put("goalProfit", CURRENCY_FORMAT);
        // data mapping
        dataMap.put("-1", "No Cap");
        dataMapping.add(new ValuesMapper("dailyCap", dataMap));
    }

    public CampaignDetailsTestData() {
        super(false);
        setInstanceGroup(CAMPAIGNS_INSTANCE_NAME);
        setSorting("totalSpend", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        super.setHeaders();
        // choose any campaign from array
        int campaignIndex = DataHelper.getRandomInt(allRowsArray.length());
        log.info(String.format("Get '%d' campaign from total '%d'", campaignIndex, allRowsArray.length()));
        this.campaignsObject = allRowsArray.getJSONObject(campaignIndex);
        // navigation data
        this.campaignName = String.valueOf(campaignsObject.get("buyerInstanceName"));
        this.campaignGUID = String.valueOf(campaignsObject.get("buyerInstanceGuid"));
        if (isAdmin())
            this.buyerName = String.valueOf(campaignsObject.get("parentBuyerName"));
    }

    public CampaignDetailsTestData(boolean isInstances) {
        this();
        this.isInstances = true;
        // campaign details data
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud")
                .withParams("buyerInstanceGuid", campaignGUID)
                .build().getRequestedURL();
        this.campaignsDetails = new JSONArray(dataProvider.getDataAsString(requestedURL));
        // aggregate like key-value
        this.campaignDetailsMap = new HashMap<>();
        // to map buyerGUID in UI to it's name
//        List<ObjectIdentityData> buyers = dataProvider.getCreatedInstancesData("buyers");
        for (int i = 0; i < campaignsDetails.length(); i++) {
            JSONObject object = campaignsDetails.getJSONObject(i);
            if (object.has("field")) {
                try {
                    String key = String.valueOf(object.get("field"));
                    String value = String.valueOf(object.get("default"));
                    // enum index to description
                    value = key.equals("buyerCategory") ? DataHelper.getKeyByValue(buyerCategoriesMap, value) : value;
                    value = key.equals("buyerType") ? DataHelper.getKeyByValue(buyerTypesMap, value) : value;
                    value = key.equals("country") ? DataHelper.getKeyByValue(countriesMap, value) : value;
                    value = key.equals("currency") ? DataHelper.getKeyByValue(currenciesMap, value) : value;
                    // guid -> name
                    if (key.equals("parentBuyerGuid")) {
                        try {
//                            this.buyer = ObjectIdentityData.getObjectFromListByGUID(buyers, value);
                            this.buyer = new ObjectIdentityData(null, buyerName, value);
                            value = buyer.getName();
                        } catch (NullPointerException e) {
                            throw new TestDataException("Unable to get buyer name by guid " + value);
                        }
                    }
                    if (value == null)
                        log.info(String.format("DEBUG\tField '%s' value is null, default = '%s'", key, object.get("default")));
                    campaignDetailsMap.put(key, value);
                } catch (JSONException e) {
                    throw new TestDataException(String.format("No 'default' value for " +
                            "'%s' field, json = '%s'", object.get("field"), object));
                }
            }
        }
        campaignDetailsMap.remove("className");
        // set fields
        setHeaders();
        // for debug
        log.info(String.format("DEBUG\tjson length = '%s', map size = '%s'", campaignsDetails, campaignDetailsMap));
    }

    // campaign details
    public String getCampaignName() {
        return campaignName;
    }

    public String getCampaignGUID() {
        return campaignGUID;
    }

    public Map<String, String> getCampaignDetailsMap() {
        return campaignDetailsMap;
    }

    public boolean hasGoals() {
        return campaignDetailsMap.containsKey("buyerGoal") || campaignDetailsMap.containsKey("goalProfit");
    }

    @Override
    protected void setHeaders() {
        this.headersList = DataHelper.getListFromJSONArrayByKey(campaignsDetails, "field");
        // common
        this.fields = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            FieldFormatObject field = new FieldFormatObject(campaignsDetails.getJSONObject(i), missedHeadersMetricsMap, i);
            // set mapping
            if (ValuesMapper.hasMappedValues(dataMapping, field.getName()))
                field.setValuesMap(ValuesMapper.getMappedValues(dataMapping, field.getName()).getMap());
            fields.add(field);
        }
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "campaignName=" + campaignName +
                        ", campaignGUID=" + campaignGUID +
                        ", campaignsObject=" + campaignsObject +
                        ", campaignDetailsMap=" + campaignDetailsMap +
                        ", buyer=" + buyer : "";
        return super.toString() + instanceDetails;
    }
}