package px.objects.campaigns;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.InstancesTestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static configuration.helpers.DataHelper.getKeyByValue;
import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 10/18/2016.
 */
public class CampaignTestData extends InstancesTestData {
    public enum Categories {
        Lead, Call, Click, Data, Other;
    }

    // general
    private String buyerName;
    private String country;
    private String currency; //all
    private String buyerCategory;
    private String buyerType;
    private String vertical;
    private String deliveryType;
    private String daysForDuplicate; // get know
    private String legName;
    private String hashLegName;
    // commercial
    private String buyerTier; // get know
    private String directBID;
    private String payoutQuality;
    private String florPayout;
    private String maxLR;
    private String incLRInPayoutQuality;
    // volume and quality
    private String monthlyPayoutCap;
    private String dailyLeadsCap; // get know
    private String floorScore;   // get know
    private String sourceQualityFloor;
    private String publisherScoreCelling;
    // goals
    private String buyerGoal;
    private String goalProfit;
    // maps for possible next verification
    private Map<String, String> countriesMap;
    private Map<String, String> currenciesMap;
    private Map<String, String> buyerCategoryMap;
    private Map<String, String> buyerTypeMap;
    private Map<String, String> buyerTierMap;
    private Map<String, String> verticalsMap;
    private Map<String, String> deliveryTypeMap;
    private Map<String, String> sourceQualityFloorMap;

    public CampaignTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("campaigns");
        // mapping from test data files
        this.countriesMap = dataProvider.getPossibleValueFromJSON("Countries", "enumDescription");
        this.currenciesMap = dataProvider.getPossibleValueFromJSON("Currencies", "enumDescription");
        this.buyerCategoryMap = dataProvider.getPossibleValueFromJSON("BuyerCategories");
        this.buyerTypeMap = dataProvider.getPossibleValueFromJSON("BuyerTypes");
        this.buyerTierMap = dataProvider.getPossibleValueFromJSON("Tiers", "enumDescription");
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals");
        this.deliveryTypeMap = dataProvider.getPossibleValueFromJSON("BuyerDeliveryTypes");
        this.sourceQualityFloorMap = dataProvider.getPossibleValueFromJSON("PublisherTiers");
        this.country = getRandomValueFromList(new ArrayList<>(countriesMap.keySet()));
        this.currency = getRandomValueFromList(new ArrayList<>(currenciesMap.keySet()));
        this.buyerCategory = getRandomValueFromList(new ArrayList<>(buyerCategoryMap.keySet()));
        this.buyerType = getRandomValueFromList(new ArrayList<>(buyerTypeMap.keySet()));
        this.buyerTier = getRandomValueFromList(new ArrayList<>(buyerTierMap.keySet()));
        this.vertical = getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
        this.deliveryType = getRandomValueFromList(new ArrayList<>(deliveryTypeMap.keySet()));
        this.sourceQualityFloor = getRandomValueFromList(new ArrayList<>(sourceQualityFloorMap.keySet()));
        this.publisherScoreCelling = getRandomValueFromList(new ArrayList<>(sourceQualityFloorMap.keySet()));
        // created instances
        // TODO: could be figure out with SessionPool
        if (Config.isAdmin()) {
            List<String> buyerNamesList = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("buyers"));
            this.buyerName = getRandomValueFromList(new ArrayList<>(buyerNamesList));
        }
        // others
        if (isPositive()) setPositiveData();
        else setNegativeData();
        this.legName = "Legacy Name " + getRandomCharSequence();
        this.hashLegName = getRandomCharSequence(32); // maxLength = 255
        this.daysForDuplicate = getQuantity(365);
        this.directBID = getQuantity(1000);// + ",00 €";
        this.florPayout = getQuantity(isPositive() ? Integer.parseInt(directBID) : 1000); // add later on condition for negative
        this.monthlyPayoutCap = getQuantity(1000); // + ",00 €";
        this.payoutQuality = getPercentage();// + " %";
        this.maxLR = getPercentage(); // + " %";
        this.incLRInPayoutQuality = getPercentage(); // + " %";
        this.dailyLeadsCap = getQuantity(100);
        this.floorScore = getQuantity(10);
        // goals
        this.goalProfit = getQuantity(1000);
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/dispositionGoals")
                .build().getRequestedURL();
        try {
            JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
            List<String> dispositionStatuses = DataHelper.getListFromJSONArrayByKey(jsonArray, "ourStatus");
            this.buyerGoal = getRandomValueFromList(dispositionStatuses);
        } catch (JSONException e) {
//           throw  new TestDataException("Unable to get campaign goals data", e);
            log.error("Unable to get campaign goals data");
        }
    }

    public CampaignTestData(JSONObject jsonObject) {
        super(DataMode.getCreatedByResponse());
        setInstanceGroup("campaigns");
        this.id = String.valueOf(jsonObject.get("buyerInstanceID"));
        this.guid = String.valueOf(jsonObject.get("buyerInstanceGuid"));
        this.name = String.valueOf(jsonObject.get("buyerInstanceName"));
        this.deliveryType = String.valueOf(jsonObject.get("buyerInstanceDeliveryType"));
        this.buyerCategory = String.valueOf(jsonObject.get("buyerCategory"));
        this.vertical = String.valueOf(jsonObject.get("vertical"));
        this.country = jsonObject.has("country") ? String.valueOf(jsonObject.get("country")) : null;
        // get key by value
        this.buyerCategoryMap = dataProvider.getPossibleValueFromJSON("BuyerCategories");
        this.buyerCategory = getKeyByValue(buyerCategoryMap, buyerCategory);
    }

    // fields set could be increased if required
    public CampaignTestData(String campaignGUID) {
        super(DataMode.getCreatedByResponse());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud")
                .withParams("buyerInstanceGuid", campaignGUID)
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
        JSONObject countryObject = JSONWrapper.toList(jsonArray).stream().filter(field -> field.has("field")
                && String.valueOf(field.get("field")).equals("country")).findFirst().orElse(null);
        this.country = countryObject != null ? String.valueOf(countryObject.get("default")) : null;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getCountry() {
        return country;
    }

    public String getCurrency() {
        return currency;
    }

    public String getBuyerCategory() {
        return buyerCategory;
    }

    public String getBuyerType() {
        return buyerType;
    }

    public String getVertical() {
        return vertical;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public String getDaysForDuplicate() {
        return daysForDuplicate;
    }

    public String getLegName() {
        return legName;
    }

    public String getHashLegName() {
        return hashLegName;
    }

    public String getBuyerTier() {
        return buyerTier;
    }

    public String getDirectBID() {
        return directBID;
    }

    public String getPayoutQuality() {
        return payoutQuality;
    }

    public String getFlorPayout() {
        return florPayout;
    }

    public String getMaxLR() {
        return maxLR;
    }

    public String getIncLRInPayoutQuality() {
        return incLRInPayoutQuality;
    }

    public String getMonthlyPayoutCap() {
        return monthlyPayoutCap;
    }

    public String getDailyLeadsCap() {
        return dailyLeadsCap;
    }

    public String getFloorScore() {
        return floorScore;
    }

    public String getSourceQualityFloor() {
        return sourceQualityFloor;
    }

    public String getPublisherScoreCelling() {
        return publisherScoreCelling;
    }

    public String getCategoryKeyByValue(String buyerCategoryValue) {
        return getKeyByValue(buyerCategoryMap, buyerCategoryValue);
    }

    public boolean isShared() {
        return buyerType != null && buyerType.equals("Shared");
    }

    // goals
    public String getBuyerGoal() {
        return buyerGoal;
    }

    public String getGoalProfit() {
        return goalProfit;
    }

    public boolean hasGoals() {
        return buyerGoal != null;
    }

    // conditions
    public boolean isRedirectAfterCreation() {
        return buyerCategory.equals("Lead");
    }

    // for request data
    public Map<String, String> getBuyerCategoryMap() {
        return buyerCategoryMap;
    }

    public Map<String, String> getBuyerTypeMap() {
        return buyerTypeMap;
    }

    public Map<String, String> getBuyerTierMap() {
        return buyerTierMap;
    }

    public Map<String, String> getCountriesMap() {
        return countriesMap;
    }

    public Map<String, String> getCurrenciesMap() {
        return currenciesMap;
    }

    // campaign builder
    public CampaignTestData withVertical(List<String> verticals) {
        this.vertical = getRandomValueFromList(verticals);
        return this;
    }

    public CampaignTestData withCountry(List<String> countries) {
        this.country = getRandomValueFromList(countries);
        this.country = DataHelper.getKeyByValueIgnoreCase(countriesMap, country);
        return this;
    }

    public CampaignTestData withDeliveryType(String type) {
        this.deliveryType = type;
        return this;
    }

    public CampaignTestData withCategory(String category) {
        this.buyerCategory = category;
        return this;
    }

    public CampaignTestData withVertical(String vertical) {
        this.vertical = vertical;
        return this;
    }

    public CampaignTestData withCountry(String country) {
        this.country = DataHelper.getKeyByValueIgnoreCase(countriesMap, country);
        return this;
    }

    // filter management
    public void setCountry() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstance/crud")
                .withParams("buyerInstanceGuid", guid)
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
        JSONObject countryObject = JSONWrapper.toList(jsonArray).stream().filter(field -> field.has("field")
                && String.valueOf(field.get("field")).equals("country")).findFirst().orElse(null);
        this.country = countryObject != null ? String.valueOf(countryObject.get("default")) : null;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCampaignTestData{" +
                "buyerName='" + buyerName + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                ", buyerCategory='" + buyerCategory + '\'' +
                ", buyerType='" + buyerType + '\'' +
                ", vertical='" + vertical + '\'' +
                ", deliveryType='" + deliveryType + '\'' +
                ", daysForDuplicate='" + daysForDuplicate + '\'' +
                ", legName='" + legName + '\'' +
                ", buyerTier='" + buyerTier + '\'' +
                ", directBID='" + directBID + '\'' +
                ", payoutQuality='" + payoutQuality + '\'' +
                ", florPayout='" + florPayout + '\'' +
                ", maxLR='" + maxLR + '\'' +
                ", incLRInPayoutQuality='" + incLRInPayoutQuality + '\'' +
                ", monthlyPayoutCap='" + monthlyPayoutCap + '\'' +
                ", dailyLeadsCap='" + dailyLeadsCap + '\'' +
                ", floorScore='" + floorScore + '\'' +
                ", sourceQualityFloor='" + sourceQualityFloor + '\'' +
                ", publisherScoreCelling='" + publisherScoreCelling + '\'' +
                ", buyerGoal='" + buyerGoal + '\'' +
                ", goalProfit='" + goalProfit + '\'' +
                '}';
    }
}