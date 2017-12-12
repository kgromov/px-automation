package px.objects.publishers;

import config.Config;
import configuration.helpers.DataHelper;
import px.objects.InstancesTestData;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.users.ContactTestData;
import px.objects.subIds.SubIdTestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 10/18/2016.
 */
public class PublisherTestData extends InstancesTestData {
    private String tier;
    private String type;
    private String accessMode;
    private String margin;
    private String minPrice;
    private String fixedPricing;
    private String escorePercentage;
    private String startBalance;
    private String maxNegativeBalance;
    private String freeTests;
    private String upsellBalance;
    private String leadingBalance;
    private String managerID;
    // advanced settings
    // general info
    private String spending;
    private String qualityScore;
    private String leadPercentage;
    private String leadPercentageType;
    private String fraudPercentage;
    private String status;
    // ping post fields
    private String pingPostType;
    private String bidType;
    private String bidFloor;
    private String outBidPerc;
    private String outBidPercPerc;
    private String pingTimeout;
    //api/publisherinfo/contactitem?contactInfoId=d7f817bb-f71e-413c-a1e9-10486ad57a1d&parentObjectId=d7f817bb-f71e-413c-a1e9-10486ad57a1d
    // key='type'
    // contact data
    private ContactTestData contactTestData;
    private String additionalContactInfo; // ? try to find through requests
    private String companyName;
    private String maritalStatus;
    private String webSite;
    private String billingCycle;
    private String paymentTerms;
    // address info
    private String extraInfo;
    private String country;
    // maps
    private Map<String, String> tierMap;
    private Map<String, String> typeMap;
    private Map<String, String> accessModeMap;
    private Map<String, String> leadingBalanceMap;
    private Map<String, String> statusMap;
    private Map<String, String> paymentTermsMap;
    private Map<String, String> billingPeriodsMap;
    private Map<String, String> maritalStatusMap;
    private Map<String, String> countriesMap;
    private Map<String, String> leadReturnMap;
    // ping post fields
    private Map<String, String> bidTypeMap;
    private Map<String, String> pingPostTypeMap;
    private Map<String, String> pingPostTimeoutMap;

    public PublisherTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("publishers");
        // mapping from test data files
        this.tierMap = dataProvider.getPossibleValueFromJSON("PublisherTiers");
        this.typeMap = dataProvider.getPossibleValueFromJSON("PublisherTypes");
        this.accessModeMap = dataProvider.getPossibleValueFromJSON("PublisherAccessModes");
        this.leadingBalanceMap = dataProvider.getPossibleValueFromJSON("LeadingBalances");
        this.bidTypeMap = dataProvider.getPossibleValueFromJSON("PingPostBidType");
        this.pingPostTypeMap = dataProvider.getPossibleValueFromJSON("PingPostType");
        this.pingPostTimeoutMap = dataProvider.getPossibleValueFromJSON("PingTimeoutLimit");
        this.tier = DataHelper.getRandomValueFromList(new ArrayList<>(tierMap.keySet()));
        this.type = DataHelper.getRandomValueFromList(new ArrayList<>(typeMap.keySet()));
        this.accessMode = DataHelper.getRandomValueFromList(new ArrayList<>(accessModeMap.keySet()));
        this.leadingBalance = DataHelper.getRandomValueFromList(new ArrayList<>(leadingBalanceMap.keySet()));
        this.bidType = DataHelper.getRandomValueFromList(new ArrayList<>(bidTypeMap.keySet()));
        this.pingPostType = DataHelper.getRandomValueFromList(new ArrayList<>(pingPostTypeMap.keySet()));
        this.pingTimeout = DataHelper.getRandomValueFromList(new ArrayList<>(pingPostTimeoutMap.keySet()));
        // yes/no
        this.fixedPricing = DataHelper.getRandomYesNo();
        this.upsellBalance = DataHelper.getRandomYesNo();
        // created instances
        // TODO: could be figure out with SessionPool
        if (Config.isAdmin() && !(this instanceof SubIdTestData)) {
            List<String> publisherManagersList = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("publisherManagers"));
            if (publisherManagersList.isEmpty())
                throw new TestDataException("There is no any publisherManager - " + publisherManagersList);
            this.managerID = DataHelper.getRandomValueFromList(publisherManagersList);
        }
        // contact info
        this.contactTestData = new ContactTestData(isPositive());
        // others
        setPositiveData();
        if (!isPositive()) setNegativeData();
        this.freeTests = getQuantity(10);
        this.minPrice = getQuantity(1000); // + ",00 €";
        this.startBalance = getQuantity(1000); // + ",00 €";
        this.maxNegativeBalance = getQuantity(1000); // + ",00 €";
        this.margin = getPercentage(); // + " %";
        this.escorePercentage = getPercentage(); // + " %";
        this.outBidPerc = getPercentage();
        this.bidFloor = getPercentage();
        this.outBidPercPerc = getPercentage();
        if (isEditMode()) setEditData();
    }

    private void setEditData() {
        this.statusMap = dataProvider.getPossibleValueFromJSON("Status");
        this.paymentTermsMap = dataProvider.getPossibleValueFromJSON("PaymentTerms");
        this.billingPeriodsMap = dataProvider.getPossibleValueFromJSON("BillingPeriod");
        this.maritalStatusMap = dataProvider.getPossibleValueFromJSON("MaritalStatus");
        this.countriesMap = dataProvider.getPossibleValueFromJSON("Countries");
        this.leadReturnMap = dataProvider.getPossibleValueFromJSON("LeadReturnPercentagePreDefined");
        // general info
        this.spending = getQuantity(1000);
        this.qualityScore = getQuantity(1000);
        this.leadPercentage = getPercentage();
        this.leadPercentageType = getRandomValueFromList(new ArrayList<>(leadReturnMap.keySet()));
        this.fraudPercentage =  getPercentage();
        this.status = getRandomValueFromList(new ArrayList<>(statusMap.keySet()));
        // contact data
        this.companyName = StringUtils.capitalize(instanceGroup) + " Company " + getRandomCharSequence();
        // depends on additional contact info - possible 20 items
        this.webSite = getWebSiteUnique();
        this.paymentTerms = getRandomValueFromList(new ArrayList<>(paymentTermsMap.keySet()));
        this.billingCycle = getRandomValueFromList(new ArrayList<>(billingPeriodsMap.keySet()));
        this.maritalStatus = getRandomValueFromList(new ArrayList<>(maritalStatusMap.keySet()));
        this.country = getRandomValueFromList(new ArrayList<>(countriesMap.keySet()));
        // address info
        this.extraInfo = "Some extra info " + name;
        // cause state is input field
        this.contactTestData.setState(getQuantity(16));
    }

    public PublisherTestData(JSONObject jsonObject) {
        super(DataMode.getCreatedByResponse());
        setInstanceGroup("publishers");
        // both in api and api/lightModel structures
        this.id = String.valueOf(jsonObject.get("publisherId"));
        this.guid = String.valueOf(jsonObject.get("publisherGuid"));
        this.name = String.valueOf(jsonObject.get("publisherName"));
        // api only
        this.tier = jsonObject.has("publisherTier") ? String.valueOf(jsonObject.get("publisherTier")) : null;
        this.type = jsonObject.has("type") ? String.valueOf(jsonObject.get("type")) : null;
        this.accessMode = jsonObject.has("accessMode") ? String.valueOf(jsonObject.get("accessMode")) : null;
        this.margin = jsonObject.has("margin") ? String.valueOf(jsonObject.get("margin")) : null;
        this.upsellBalance = jsonObject.has("addUpsellToBalance") ? String.valueOf(jsonObject.get("addUpsellToBalance")) : null;
        this.escorePercentage = jsonObject.has("escoreCheckPercentage") ? String.valueOf(jsonObject.get("escoreCheckPercentage")) : null;
        this.fixedPricing = jsonObject.has("fixedPricing") ? String.valueOf(jsonObject.get("fixedPricing")) : null;
        // reformat
        if (upsellBalance != null) this.upsellBalance = DataHelper.getYesNo(upsellBalance);
        if (fixedPricing != null) this.fixedPricing = DataHelper.getYesNo(fixedPricing);
    }

    public String getTier() {
        return tier;
    }

    public String getType() {
        return type;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public String getMargin() {
        return margin;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getFixedPricing() {
        return fixedPricing;
    }

    public String getEscorePercentage() {
        return escorePercentage;
    }

    public String getStartBalance() {
        return startBalance;
    }

    public String getMaxNegativeBalance() {
        return maxNegativeBalance;
    }

    public String getFreeTests() {
        return freeTests;
    }

    public String getUpsellBalance() {
        return upsellBalance;
    }

    public String getLeadingBalance() {
        return leadingBalance;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    public boolean hasManager() {
        return managerID != null;
    }

    // ping post
    public String getPingPostType() {
        return pingPostType;
    }

    public String getBidType() {
        return bidType;
    }

    public String getOutBidPerc() {
        return outBidPerc;
    }

    public String getPingTimeout() {
        return pingTimeout;
    }

    public String getBidFloor() {
        return bidFloor;
    }

    public String getOutBidPercPerc() {
        return outBidPercPerc;
    }

    // request data
    public Map<String, String> getBidTypeMap() {
        return bidTypeMap;
    }

    public Map<String, String> getPingPostTypeMap() {
        return pingPostTypeMap;
    }

    public Map<String, String> getPingPostTimeoutMap() {
        return pingPostTimeoutMap;
    }

    // advanced settings
    public String getSpending() {
        return spending;
    }

    public String getQualityScore() {
        return qualityScore;
    }

    public String getLeadPercentage() {
        return leadPercentage;
    }

    public String getLeadPercentageType() {
        return leadPercentageType;
    }

    public String getFraudPercentage() {
        return fraudPercentage;
    }

    public String getStatus() {
        return status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getWebSite() {
        return webSite;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public String getAdditionalContactInfo() {
        return additionalContactInfo;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public String getCountry() {
        return country;
    }

    public ContactTestData getContactTestData() {
        return contactTestData;
    }

    public void setContactTestData() {
        this.contactTestData = new ContactTestData(isPositive());
    }

    public void setContactTestData(boolean isPositive) {
        this.contactTestData = new ContactTestData(isPositive);
    }

    @Override
    public String toString() {
        String contactDetails = contactTestData != null ? contactTestData.toString() : "";
        String editDetails = isEditMode() && !isCreatedByResponse() ?
                "\nspending='" + spending + '\'' +
                        ", qualityScore='" + qualityScore + '\'' +
                        ", leadPercentage='" + leadPercentage + '\'' +
                        ", leadPercentageType='" + leadPercentageType + '\'' +
                        ", fraudPercentage='" + fraudPercentage + '\'' +
                        ", status='" + status + '\'' +
                        ", companyName='" + companyName + '\'' +
                        ", webSite='" + webSite + '\'' +
                        ", paymentTerms='" + paymentTerms + '\'' +
                        ", billingCycle='" + billingCycle + '\'' +
                        ", maritalStatus='" + maritalStatus + '\'' +
                        ", country='" + country + '\'' +
                        ", extraInfo='" + extraInfo + '\'' : "";
        return super.toString() + "\nPublisherTestData{" +
                "tier='" + tier + '\'' +
                ", type='" + type + '\'' +
                ", accessMode='" + accessMode + '\'' +
                ", margin='" + margin + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", fixedPricing='" + fixedPricing + '\'' +
                ", escorePercentage='" + escorePercentage + '\'' +
                ", startBalance='" + startBalance + '\'' +
                ", maxNegativeBalance='" + maxNegativeBalance + '\'' +
                ", freeTests='" + freeTests + '\'' +
                ", upsellBalance='" + upsellBalance + '\'' +
                ", leadingBalance='" + leadingBalance + '\'' +
                ", managerID='" + managerID + '\'' +
                editDetails +
                contactDetails +
                '}';
    }
}
