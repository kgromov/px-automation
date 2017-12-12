package px.objects.offers;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import px.objects.InstancesTestData;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.DataMode;

import java.util.*;

import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 10/18/2016.
 */
public class OfferTestData extends InstancesTestData {
    // general
    private String previewURL;
    private String offerURL;
    private String protocol;
    private String status;
    private String offerCategories;
    private String refID;
    private Date expirationDate;
    private String expirationFormattedDate;
    private int expirationMonth;
    private String note;
    private String currency;
    // payout
    private String payoutType;
    private String payoutMethod;
    private String defaultPayout;
    private String defaultTier1Payout;
    private String defaultTier2Payout;
    private String defaultTier3Payout;
    // revenue
    private String revenueType;
    private String revenueMethod;
    private String maxPayout;
    private String maxTier1Payout;
    private String maxTier2Payout;
    private String maxTier3Payout;
    // tracking
    private String trackingDomain;
    private String redirectOffer;
    private String conversionCap;
    private String sessionTracking;
    private String sessionHours;
    private String sessionImpressionHours;
    private String customSessionHours;
    private String customSessionImpressionHours;
    private String secondaryOffer;
    private List<String> checkBoxList;
    // dependent fields
    private String offerByURL;
    private String offerByName;
    // offer urls
    private String urlName; //api/offerurls?count=10&filter={"offerId":"305"}&page=1&sorting={"offerUrlName":"asc"}
    private String urlStatus; // table headers - http://rvmd-11865-px.stagingrevi.com/api/offerurls; could be mapped by data-field-name="offerUrlId"
    private String url;
    private String urlPreview;
    private int offerURLCount;
    // offer groups
    private String offerGroup; //api/lightModel/offergroups
    private boolean isGroupAssigned;
    // offer targeting
    private String deviceType;
    private String action;
    private String deviceBrand; //api/lightModel/offertargetrules
    // maps
    private Map<String, String> protocolsMap;
    private Map<String, String> statusMap;
    private Map<String, String> offerCategoriesMap;
    private Map<String, String> currencyMap;
    private Map<String, String> payoutTypeMap;
    private Map<String, String> revenueTypeMap;
    private Map<String, String> trackingDomainMap;
    private Map<String, String> redirectOfferMap;
    private Map<String, String> sessionHoursMap;
    private Map<String, String> secondaryOfferMap;
    private Map<String, String> offerGroupsMap;
    private Map<String, String> deviceBrandsMap;
    private List<String> deviceBrandsList;
    // for now is hardcoded
    private List<String> methodList = Arrays.asList("Default", "Tiered");
    private List<String> sessionTrackingList = Arrays.asList("Clicks", "Impressions");
    private List<String> secondaryOfferList = Arrays.asList("Network Offer", "Offer Url");
    // under buyer user
    public static final List<String> ADMIN_MENU_ITEMS = Arrays.asList("Advanced settings", "Create similar");

    public OfferTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("offers");
        // mapping from test data files
        this.protocolsMap = dataProvider.getPossibleValueFromJSON("Protocols");
        this.statusMap = dataProvider.getPossibleValueFromJSON("HasOffersStatuses");
//        this.offerCategoriesMap = dataProvider.getPossibleValueFromJSON("HasOffersStatuses");
        this.currencyMap = dataProvider.getPossibleValueFromJSON("HasOffersCurrencies");
        this.payoutTypeMap = dataProvider.getPossibleValueFromJSON("PayoutTypes");
        this.revenueTypeMap = dataProvider.getPossibleValueFromJSON("RevenueTypes");
//        this.trackingDomainMap = dataProvider.getPossibleValueFromJSON("RevenueTypes");
        this.sessionHoursMap = dataProvider.getPossibleValueFromJSON("SessionHoursSpans");
        this.protocol = getRandomValueFromList(new ArrayList<>(protocolsMap.keySet()));
        this.status = getRandomValueFromList(new ArrayList<>(statusMap.keySet()));
        this.currency = getRandomValueFromList(new ArrayList<>(currencyMap.keySet()));
        this.payoutType = getRandomValueFromList(new ArrayList<>(payoutTypeMap.keySet()));
        this.revenueType = getRandomValueFromList(new ArrayList<>(revenueTypeMap.keySet()));
        this.sessionHours = getRandomValueFromList(new ArrayList<>(sessionHoursMap.keySet()));
        this.sessionImpressionHours = getRandomValueFromList(new ArrayList<>(sessionHoursMap.keySet()));
        // created instances
        // TODO: could be figure out with SessionPool
        if(Config.isAdmin()) {
            List<String> offerCategoriesList = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("offerCategories"));
            // only automation created
            List<ObjectIdentityData> autoOffers = dataProvider.getCreatedInstancesData("offers");
            // filter by only automation created
            List<String> offersList = ObjectIdentityData.getAllNames(ObjectIdentityData.getObjectsByName(autoOffers, "Offer Name "));
//        List<String> offersList = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("offers"));
            this.offerCategories = getRandomValueFromList(offerCategoriesList);
            this.redirectOffer = getRandomValueFromList(offersList);
            this.offerByName = getRandomValueFromList(offersList);
        }
        // radio
        this.payoutMethod = getRandomValueFromList(methodList);
        this.revenueMethod = getRandomValueFromList(methodList);
        this.sessionTracking = getRandomValueFromList(sessionTrackingList);
        this.secondaryOffer = getRandomValueFromList(secondaryOfferList);
        // others
        if (isPositive()) setPositiveData();
        else setNegativeData();
        this.previewURL = getWebSiteUnique();
        this.expirationDate = getDateByMonthOffset(expirationMonth);
        this.expirationFormattedDate = getDateByFormatSimple(EXPIRATION_DATE_PATTERN, expirationDate);
        this.note = "Some to for note " + getRandomCharSequence(10);
        this.defaultPayout = getQuantity(1000); // + ",00 €";
        this.maxPayout = getQuantity(1000); // + ",00 €";
        this.defaultTier1Payout = getQuantity(1000); // + ",00 €";
        this.defaultTier2Payout = getQuantity(1000); // + ",00 €";
        this.defaultTier3Payout = getQuantity(1000); // + ",00 €";
        this.maxTier1Payout = getQuantity(1000); // + ",00 €";
        this.maxTier2Payout = getQuantity(1000); // + ",00 €";
        this.maxTier3Payout = getQuantity(1000); // + ",00 €";
        this.conversionCap = getQuantity(1000);
    }

    public OfferTestData(JSONObject jsonObject) {
        super(DataMode.getCreatedByResponse());
        setInstanceGroup("offers");
        // both in api and api/lightModel structures
        this.id = String.valueOf(jsonObject.get("offerId"));
        this.name = String.valueOf(jsonObject.get("offerName"));
        // api only
        if (jsonObject.has("previewUrl"))
            this.previewURL = String.valueOf(jsonObject.get("previewUrl"));
        if (jsonObject.has("categoryName"))
            this.offerCategories = String.valueOf(jsonObject.get("categoryName"));
//        this.jsonObject = dataProvider.getInstanceDetails(instanceGroup, "offerName", name);
    }

    @Override
    public OfferTestData setPositiveData() {
        super.setPositiveData();
        this.offerURL = getWebSiteUnique() + "/" + name.toLowerCase() + "?{transaction_id}";
        this.offerByURL = getWebSiteUnique() + "/" + name.replace("Name", "_url").toLowerCase() + "?{transaction_id}";
        this.refID = RandomStringUtils.random(getRandomInt(15), true, true);
        this.expirationMonth = getRandomInt(1, 12);
        this.customSessionHours = String.valueOf(getRandomInt(1, 12));
        this.customSessionImpressionHours = String.valueOf(getRandomInt(1, 12));
        return this;
    }

    @Override
    public OfferTestData setNegativeData() {
        super.setNegativeData();
        this.offerURL = RandomStringUtils.random(10, true, true) + "." + RandomStringUtils.random(10, true, true);
        this.offerByURL = RandomStringUtils.random(10, true, true) + "." + RandomStringUtils.random(10, true, true);
        this.refID = getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 32)); //RandomStringUtils.random(getRandomInt(16, 31), true, true);
        this.expirationMonth = -getRandomInt(1, 12);
        this.customSessionHours = RandomStringUtils.random(4, true, true);
        this.customSessionImpressionHours = RandomStringUtils.random(4, true, true);
        return this;
    }

    public void setOfferURLData() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offerurls")
                .filter("offerId", id)
                .sort("offerUrlName", "asc")
                .build().getRequestedURL();
        JSONArray jsonArray = dataProvider.getDataAsJSONArray(requestedURL);
        this.offerURLCount = jsonArray.length();
        this.urlName = "Offer URL " + RandomStringUtils.random(getRandomInt(15), true, true);
        this.urlStatus = DataHelper.getRandomValueFromList(Arrays.asList("active", "deleted"));
        this.urlPreview = "http://www." + RandomStringUtils.randomAlphabetic(8).toLowerCase() + "." + RandomStringUtils.randomAlphabetic(3).toLowerCase();
        this.url = "http://www." + RandomStringUtils.randomAlphabetic(8).toLowerCase() + "." + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "/" + name.toLowerCase() + "?{transaction_id}";
    }

    public void setOfferGroupsData() {
        List<ObjectIdentityData> groups = dataProvider.getCreatedInstancesData("offergroups");
        List<String> offerGroupsList = ObjectIdentityData.getAllNames(groups);
        this.offerGroup = getRandomValueFromList(offerGroupsList);
        String offerGroupID = ObjectIdentityData.getObjectFromListByName(groups, offerGroup).getId();
        // groups in current offer by id
        // api/offertargeting/groupsbyoffer?offerId=100
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offertargeting/groupsbyoffer")
                .withParams("offerId", id)
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
        if (jsonArray.length() > 0) {
            this.isGroupAssigned = DataHelper.getListFromJSONArrayByKey(jsonArray, "offerGroupId").contains(offerGroupID);
        }
    }

    public void setOfferTargetingData() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/offertargetrules")
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
        this.deviceBrandsList = DataHelper.getListFromJSONArrayByKey(jsonArray, "targetRuleName");
        this.deviceType = String.valueOf(DataHelper.getRandomInt(1, 2));
        this.action = DataHelper.getRandomValueFromList(Arrays.asList("Allow", "Deny"));
        this.deviceBrand = getRandomValueFromList(new ArrayList<>(deviceBrandsList));
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public String getOfferURL() {
        return offerURL;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getStatus() {
        return status;
    }

    public String getOfferCategories() {
        return offerCategories;
    }

    public String getRefID() {
        return refID;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getExpirationFormattedDate() {
        return expirationFormattedDate;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public String getNote() {
        return note;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPayoutType() {
        return payoutType;
    }

    public String getPayoutMethod() {
        return payoutMethod;
    }

    public String getDefaultPayout() {
        return defaultPayout;
    }

    public String getRevenueType() {
        return revenueType;
    }

    public String getRevenueMethod() {
        return revenueMethod;
    }

    public String getMaxPayout() {
        return maxPayout;
    }

    // tier payouts
    public String getDefaultTier1Payout() {
        return defaultTier1Payout;
    }

    public String getDefaultTier2Payout() {
        return defaultTier2Payout;
    }

    public String getDefaultTier3Payout() {
        return defaultTier3Payout;
    }

    public String getMaxTier1Payout() {
        return maxTier1Payout;
    }

    public String getMaxTier2Payout() {
        return maxTier2Payout;
    }

    public String getMaxTier3Payout() {
        return maxTier3Payout;
    }

    public String getTrackingDomain() {
        return trackingDomain;
    }

    public String getRedirectOffer() {
        return redirectOffer;
    }

    public String getConversionCap() {
        return conversionCap;
    }

    public String getSessionTracking() {
        return sessionTracking;
    }

    public String getSessionHours() {
        return sessionHours;
    }

    public String getSessionImpressionHours() {
        return sessionImpressionHours;
    }

    public String getCustomSessionHours() {
        return customSessionHours;
    }

    public String getCustomSessionImpressionHours() {
        return customSessionImpressionHours;
    }

    public String getSecondaryOffer() {
        return secondaryOffer;
    }

    public String getOfferByURL() {
        return offerByURL;
    }

    public String getOfferByName() {
        return offerByName;
    }

    // advanced settings
    public String getUrlName() {
        return urlName;
    }

    public String getUrlStatus() {
        return urlStatus;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public int getOfferURLCount() {
        return offerURLCount;
    }

    public boolean isAssignedToGroup() {
        return isGroupAssigned;
    }

    public String getOfferGroup() {
        return offerGroup;
    }

    public String getDeviceType() {
        return deviceType;
    }

    // currently cause it's unclear where to get data
    public int getDeviceTypeIndex() {
        return Integer.parseInt(deviceType);
    }

    public String getAction() {
        return action;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    // conditions
    public boolean isDefaultPayoutMethod() {
        return payoutMethod.equals("Default");
    }

    public boolean isPercentagePayout() {
        return payoutType.contains("PS");
    }

    public boolean isConversionPayout() {
        return !payoutType.contains("PS") || payoutType.contains("PA");
    }

    public boolean isPercentageRevenue() {
        return revenueType.contains("PS");
    }

    public boolean isConversionRevenue() {
        return !revenueType.contains("PS") || revenueType.contains("PA");
    }

    public boolean isDefaultRevenueMethod() {
        return revenueMethod.equals("Default");
    }

    public boolean isClicksSessionTracking() {
        return sessionTracking.equals("Clicks");
    }

    public boolean isSessionHourCustom(String sessionHour) {
        return sessionHour.equals("Custom");
    }

    public boolean isSecondaryOfferByName() {
        return secondaryOffer.equals("Network Offer");
    }

    public boolean isConversionTrackingByPixel() {
        return protocol.toLowerCase().contains("pixel");
    }

    public boolean isVisibleByStatus() {
        return status.equalsIgnoreCase("active") || status.equalsIgnoreCase("pending");
    }

    // offer request data
    public Map<String, String> getCurrencyMap() {
        return currencyMap;
    }

    public Map<String, String> getPayoutTypeMap() {
        return payoutTypeMap;
    }

    public Map<String, String> getRevenueTypeMap() {
        return revenueTypeMap;
    }

    public Map<String, String> getProtocolsMap() {
        return protocolsMap;
    }

    // runtime set
    public void setOfferCategories(String offerCategories) {
        this.offerCategories = offerCategories;
    }

    public void setRedirectOffer(String redirectOffer) {
        this.redirectOffer = redirectOffer;
    }

    public void setOfferByName(String offerByName) {
        this.offerByName = offerByName;
    }

    public List<String> getCheckBoxList() {
        return checkBoxList;
    }

    public void setCheckBoxList(List<String> checkBoxList) {
        this.checkBoxList = checkBoxList;
    }

    @Override
    public String toString() {
        return super.toString() + "\nOfferTestData{" +
                "previewURL='" + previewURL + '\'' +
                ", offerURL='" + offerURL + '\'' +
                ", protocol='" + protocol + '\'' +
                ", status='" + status + '\'' +
                ", offerCategories='" + offerCategories + '\'' +
                ", refID='" + refID + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", expirationMonth=" + expirationMonth +
                ", note='" + note + '\'' +
                ", currency='" + currency + '\'' +
                ", payoutType='" + payoutType + '\'' +
                ", payoutMethod='" + payoutMethod + '\'' +
                ", defaultPayout='" + defaultPayout + '\'' +
                ", revenueType='" + revenueType + '\'' +
                ", revenueMethod='" + revenueMethod + '\'' +
                ", maxPayout='" + maxPayout + '\'' +
                ", trackingDomain='" + trackingDomain + '\'' +
                ", redirectOffer='" + redirectOffer + '\'' +
                ", conversionCap='" + conversionCap + '\'' +
                ", sessionTracking='" + sessionTracking + '\'' +
                ", sessionHours='" + sessionHours + '\'' +
                ", secondaryOffer='" + secondaryOffer + '\'' +
                '}';
    }
}