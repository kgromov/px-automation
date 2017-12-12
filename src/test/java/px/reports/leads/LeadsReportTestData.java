package px.reports.leads;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.leads.LeadObject;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.SearchData;

import java.util.*;
import java.util.stream.Collectors;

import static config.Config.isAdmin;
import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 2/28/2017.
 */
public class LeadsReportTestData extends ReportTestData {
    {
        startMonthOffset = 1;
        durationDays = 1;
    }

    static final int MAX_BUYER_IDS = 5;
    // filters data
    protected ObjectIdentityData publisher;
    protected ObjectIdentityData buyer;
    protected ObjectIdentityData buyerCampaign;
    protected ObjectIdentityData subID;
    private ObjectIdentityData offer;
    private String vertical;
    // initial data
    private Map<String, String> verticalsMap;
    // table data
    protected JSONArray itemsByPublisherGUID; // ParentPublisherGuid
    protected JSONArray itemsByBuyerGUID;     // parentbuyerguid
    protected JSONArray itemsByBuyerInstanceGUID;     // buyerinstanceguid
    protected JSONArray itemsByPublisherInstanceGUID; // PublisherInstanceGuid
    private JSONArray itemsByOfferID;
    private JSONArray itemsByVertical;
    protected JSONArray itemsByAllFilters;
    // sub instances
    protected List<ObjectIdentityData> buyerCampaigns; // publisherinstances?ParentPublisherGuid=
    protected List<ObjectIdentityData> subIDs;        // buyerinstancesbyparent?parent=
    // items counts
    protected int publisherItemsCount;
    protected int buyerItemsCount;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String BUYER_FILTER = "parentbuyerguid";
    public static final String BUYER_INSTANCE_FILTER = "buyerinstanceguid";
    public static final String PUBLISHER_FILTER = "ParentPublisherGuid";
    public static final String PUBLISHER_INSTANCE_FILTER = "PublisherInstanceGuid";
    public static final String OFFER_FILTER = "OfferId";
    public static final String VERTICAL_FILTER = "vertical";

    static {
        missedHeadersMetricsMap.put("payout", "Currency");
        missedHeadersMetricsMap.put("qiqScore", "Percentage");
        missedHeadersMetricsMap.put("numSlots", "Number");
        missedHeadersMetricsMap.put("fraudScore", "Number");
        missedHeadersMetricsMap.put("creationDate", "Date");
        // popup title mapping
        popupTitleMap.put("qiqScore", "Lead quality score");
    }

    protected LeadsReportTestData(int filters) {
        super(false);
    }

    public LeadsReportTestData() {
        this(false);
    }

    public LeadsReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("leaddetailsfastreport/data");
        setSorting("dateTime", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            setFiltersData();
            // rows in table by publisher GUID
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherGUID = dataProvider.getDataAsJSONArray(requestedURL);
            this.publisherItemsCount = getItemsCurrentTotalCount();
            // rows in table by buyer GUID
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(buyer.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
            this.buyerItemsCount = getItemsCurrentTotalCount();
            // items by buyer instance guid
            if (hasBuyerInstances()) {
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(BUYER_FILTER, BUYER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(buyer.getGuid(), buyerCampaign.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByBuyerInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
            }
            // items by publisher instance guid
            if (hasPublisherInstances()) {
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(publisher.getGuid(), subID.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByPublisherInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
            }
            // rows in table by offer id
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(offer.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByOfferID = dataProvider.getDataAsJSONArray(requestedURL);
            // items in table by vertical
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            //  rows in table after all filters
            List<String> paramsKeys = new ArrayList<>(Arrays.asList(PUBLISHER_FILTER, BUYER_FILTER,
                    OFFER_FILTER, VERTICAL_FILTER, "FromPeriod", "ToPeriod"));
            if (hasBuyerInstances()) paramsKeys.add(BUYER_INSTANCE_FILTER);
            if (hasPublisherInstances()) paramsKeys.add(PUBLISHER_INSTANCE_FILTER);
            List<String> paramsValues = new ArrayList<>(Arrays.asList(publisher.getGuid(), buyer.getGuid(),
                    offer.getId(), vertical, fromPeriod, toPeriod));
            if (hasBuyerInstances()) paramsValues.add(buyerCampaign.getGuid());
            if (hasPublisherInstances()) paramsValues.add(subID.getGuid());
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(paramsKeys, paramsValues)
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new LeadsReportTestData.FiltersResetData();
            resetData.setFilterValuesMap();
        }
    }

    // to make chainable and more flexible
    public LeadsReportTestData setFiltersData() {
        // get list of available publishers => get any publisher from 1st 100 rows
//            this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
        List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
        this.publisher = getFilterObject(publishers, allRowsArray, "publisherName", "name");
        // get list of available buyers => get any buyer from 1st 100 rows
        this.buyer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("buyers"));
        // buyer instances - filter items
        this.buyerCampaigns = dataProvider.getCreatedInstancesData("buyerinstancesbyparent",
                Collections.singletonList("parent"), Collections.singletonList(buyer.getGuid()));
        this.buyerCampaign = buyerCampaigns.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(buyerCampaigns);
        // publisher instances - filter items
        this.subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                Collections.singletonList("publisherGuid"), Collections.singletonList(publisher.getGuid()));
        this.subID = subIDs.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(subIDs);
        if (isAdmin()) {
            this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
            this.vertical = DataHelper.getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
            // get any of available offers => get any offer from 1st 100 rows
//            this.offer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("offers"));
            List<ObjectIdentityData> offers = dataProvider.getCreatedInstancesData("offers");
            this.offer = getFilterObject(offers, allRowsArray, "offerId", "id");
        }
        return this;
    }

    // filter data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getBuyer() {
        return buyer;
    }

    public ObjectIdentityData getBuyerCampaign() {
        return buyerCampaign;
    }

    public ObjectIdentityData getSubID() {
        return subID;
    }

    public ObjectIdentityData getOffer() {
        return offer;
    }

    public String getVertical() {
        return vertical;
    }

    public List<ObjectIdentityData> getBuyerCampaigns() {
        return buyerCampaigns;
    }

    public List<ObjectIdentityData> getSubIDs() {
        return subIDs;
    }

    // table data
    public JSONArray getItemsByPublisherGUID() {
        return itemsByPublisherGUID;
    }

    public JSONArray getItemsByBuyerGUID() {
        return itemsByBuyerGUID;
    }

    public JSONArray getItemsByBuyerInstanceGUID() {
        return itemsByBuyerInstanceGUID;
    }

    public JSONArray getItemsByPublisherInstanceGUID() {
        return itemsByPublisherInstanceGUID;
    }

    public JSONArray getItemsByOfferID() {
        return itemsByOfferID;
    }

    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getPublisherItemsCount() {
        return publisherItemsCount;
    }

    public int getBuyerItemsCount() {
        return buyerItemsCount;
    }

    public boolean hasBuyerInstances() {
        return buyerCampaigns != null && !buyerCampaigns.isEmpty();
    }

    public boolean hasPublisherInstances() {
        return subIDs != null && !subIDs.isEmpty();
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public SearchData getSearchData(FieldFormatObject field) {
        return new LeadsSearchData(this, field);
    }

    public static ResponseObject getResponseObject(JSONObject jsonObject) {
        return new ResponseObject(jsonObject);
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/leaddetailsfastreport/metadata")
                .build().getRequestedURL();
    }

    @Override
    protected void setFields() {
        super.setFields();
        fields.forEach(field -> field.setIndex(field.getIndex() + 1));
    }

    @Override
    public String toString() {
        String buyerCampaignDetails = isInstances && !buyerCampaigns.isEmpty() ? buyerCampaign.toString() : "";
        String subIdDetails = isInstances && !subIDs.isEmpty() ? subID.toString() : "";
        String instanceDetails = isInstances ?
                "buyer=" + buyer +
                        ", publisher=" + publisher +
                        ", offer=" + offer +
                        ", buyerCampaign=" + buyerCampaignDetails +
                        ", subID=" + subIdDetails +
                        ", buyerCampaigns=" + buyerCampaigns.stream().map(campaign -> campaign.getId() +
                        " - " + campaign.getName()).collect(Collectors.joining(", ")) +
                        ", subIDs=" + subIDs.stream().map(ObjectIdentityData::getId).collect(Collectors.joining(", ")) +
                        ", vertical=" + vertical : "";
        return super.toString() +
                "\nLeadsReportTestData{" +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {
        FiltersResetData() {
            this._instanceGroup = instanceGroup;
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(BUYER_FILTER, buyer.getGuid());
            if (hasBuyerInstances())
                filterValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getGuid());
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getGuid());
            if (hasPublisherInstances())
                filterValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getGuid());
            filterValuesMap.put(OFFER_FILTER, offer.getId());
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(BUYER_FILTER, buyer.getName());
            if (hasBuyerInstances())
                filterLabelValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getId() + " - " + buyerCampaign.getName());
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            if (hasPublisherInstances())
                filterLabelValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getId());
            filterLabelValuesMap.put(OFFER_FILTER, offer.getId() + " - " + offer.getName());
            filterLabelValuesMap.put(VERTICAL_FILTER, vertical);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            if (filterValuesMap.size() > 1)
                return itemsByFiltersCombination;
            else if (filterValuesMap.size() == 0)
                return allRowsArray;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case BUYER_FILTER:
                    return itemsByBuyerGUID;
                case BUYER_INSTANCE_FILTER:
                    return itemsByBuyerInstanceGUID;
                case PUBLISHER_FILTER:
                    return itemsByPublisherGUID;
                case PUBLISHER_INSTANCE_FILTER:
                    return itemsByPublisherInstanceGUID;
                case OFFER_FILTER:
                    return itemsByOfferID;
                case VERTICAL_FILTER:
                    return itemsByVertical;
            }
            return null;
        }

        @Override
        public void resetFilter() {
            List<String> combinationFiltersList = new ArrayList<>(filterValuesMap.keySet());
            this.filterReset = DataHelper.getRandomValueFromList(combinationFiltersList);
            log.info(String.format("Reset '%s' filter", filterReset));
            // remove dependent child filters
            if (filterReset.equals(BUYER_FILTER)) filterValuesMap.remove(BUYER_INSTANCE_FILTER);
            if (filterReset.equals(PUBLISHER_FILTER)) filterValuesMap.remove(PUBLISHER_INSTANCE_FILTER);
            // to prevent redundant request, data already exists
            if (combinationFiltersList.size() <= 2) return;
            // cause data in table supposed to be without reset filter
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            resetIterationMap.remove(filterReset);
            // keys in filter json request
            List<String> keys = new ArrayList<>(resetIterationMap.keySet());
            keys.addAll(Arrays.asList(_fromPeriodKey, _toPeriodKey));
            // values in filter json request
            List<String> values = new ArrayList<>(resetIterationMap.values());
            values.addAll(Arrays.asList(_fromPeriod, _toPeriod));
            // get rows in table after filter combination
            String requestedURL = new RequestedURL.Builder()
                    .withAbsoluteURL(_url)
                    .filter(keys, values)
                    .sort(_sortBy, _sortHow)
                    .build().getRequestedURL();
            this.itemsByFiltersCombination = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }

    private final class LeadsSearchData extends SearchData {

        LeadsSearchData(ReportTestData testData, FieldFormatObject field) {
            super(testData, field);
//            String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(searchBy, "FromPeriod", "ToPeriod"),
                            Arrays.asList(searchValue, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsSearchBy = dataProvider.getDataAsJSONArray(requestedURL);
            this.itemsSearchByCount = dataProvider.getCurrentTotal();
        }
    }

    public static final class ResponseObject extends LeadObject {
        private List<ObjectIdentityData> campaigns;
        private String creationDate;
        private String transactionId;
        private String vertical;
        private String buyerId;
        private String buyerInstanceName;
        private String payout;
        private String numSlots;
        private String publisherName;
        private String shortOrigin;
        private String subID;
        private String offerId;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String state;
        private String qiqScore;
        private String fraudScore;
        private String leadGuid;
        private String leadId;
        private boolean canReturn;
        private boolean canChangeDisposition;

        private ResponseObject(JSONObject jsonObject) {
            super(jsonObject);
            this.creationDate = String.valueOf(jsonObject.get("creationDate"));
            this.vertical = String.valueOf(jsonObject.get("vertical"));
            this.buyerId = String.valueOf(jsonObject.get("buyerId"));
            this.buyerInstanceName = jsonObject.has("buyerInstanceName")
                    ? String.valueOf(jsonObject.get("buyerInstanceName")) : null;
            this.payout = String.valueOf(jsonObject.get("payout"));
            this.shortOrigin = String.valueOf(jsonObject.get("shortOrigin"));
            this.firstName = String.valueOf(jsonObject.get("firstName"));
            this.lastName = String.valueOf(jsonObject.get("lastName"));
            this.email = String.valueOf(jsonObject.get("email"));
            this.phoneNumber = String.valueOf(jsonObject.get("phoneNumber"));
            this.state = String.valueOf(jsonObject.get("state"));
            this.qiqScore = String.valueOf(jsonObject.get("qiqScore"));
            this.leadGuid = String.valueOf(jsonObject.get("leadGuid"));
            this.leadId = String.valueOf(jsonObject.get("leadId"));
            this.canReturn = jsonObject.has("canReturn") && jsonObject.getBoolean("canReturn");
            this.canChangeDisposition = jsonObject.has("canChangeDisposition") && jsonObject.getBoolean("canChangeDisposition");
            // admin fields only
            if (isAdmin()) {
                this.transactionId = String.valueOf(jsonObject.get("transactionId"));
                this.publisherName = String.valueOf(jsonObject.get("publisherName"));
                this.subID = String.valueOf(jsonObject.get("subID"));
                this.offerId = String.valueOf(jsonObject.get("offerId"));
                this.numSlots = String.valueOf(jsonObject.get("numSlots"));
                this.fraudScore = String.valueOf(jsonObject.get("fraudScore"));
            }
            // set campaigns by buyerID if lead is sold; could be more than 1 lead
            if (isAdmin() && isSoldLead()) {
                /*this.campaigns = new LxpDataProvider().getCreatedInstancesData("buyerInstances");
                campaigns = Arrays.stream(buyerId.split(" - ")).map(campaignID ->
                        ObjectIdentityData.getObjectFromListByID(campaigns, campaignID)).collect(Collectors.toList());*/
                try {
                    JSONObject parameters = new JSONObject();
                    parameters.put("leadId", leadId);
                    parameters.put("toPeriod", getDateByFormatSimple(PX_REPORT_DATE_PATTERN, getDate()));
                    this.campaigns = new LxpDataProvider().getCreatedInstancesData("leadbuyers",
                            Collections.singletonList("parameters"), Collections.singletonList(parameters.toString()));
                } catch (TestDataException e) {
                    throw new TestDataException("Unable to get buyer campaigns by leadId = " + leadId, e);
                }
            }
        }

        public String getCreationDate() {
            Date temp = getDateByFormatSimple(LEADS_REPORT_PATTERN_IN, creationDate);
            return getDateByFormatSimple(EXPIRATION_DATE_PATTERN, temp).replace("AM", "am").replace("PM", "pm");
        }

        public String getCreationDate(String creationDate) {
            Date temp = getDateByFormatSimple(DataHelper.PX_INBOUND_REPORT_PATTERN, creationDate);
            return getDateByFormatSimple(EXPIRATION_DATE_PATTERN, temp);
        }

        public Date getDate() {
            return getDateByFormatSimple(LEADS_REPORT_PATTERN_IN, creationDate);
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getVertical() {
            return vertical;
        }

        public String getBuyerId() {
            return buyerId.equals("43") ? "Not sold" : buyerId;
        }

        public String getBuyerInstanceName() {
            return buyerInstanceName;
        }

        public String getPayout() {
            return DataHelper.getSplittedByComma(payout, 2);
        }

        public String getNumSlots() {
            return numSlots;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public String getShortOrigin() {
            return shortOrigin;
        }

        public String getSubID() {
            return subID;
        }

        public String getOfferId() {
            return offerId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getState() {
            return state;
        }

        public String getQiqScore() {
            return DataHelper.getRoundedFloat(qiqScore, 2);
        }

        public String getFraudScore() {
            return DataHelper.getRoundedFloat(fraudScore);
        }

        public String getLeadGuid() {
            return leadGuid;
        }

        public String getLeadId() {
            return leadId;
        }

        public boolean isCanReturn() {
            return canReturn;
        }

        public boolean isCanChangeDisposition() {
            return canChangeDisposition;
        }

        public boolean isSoldLead() {
            return !getBuyerId().equals("Not sold");
        }

        public boolean isSoldMore1Buyer() {
            return buyerId.split(" - ").length > 1;
        }

        public List<ObjectIdentityData> getCampaigns() {
            return campaigns;
        }

        public List<ObjectIdentityData> getCampaignsToSet() {
            return ObjectIdentityData.getRandomSubList(campaigns);
        }

        @Override
        public String toString() {
            return "ResponseObject{" +
                    "creationDate='" + creationDate + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    ", vertical='" + vertical + '\'' +
                    ", buyerId='" + buyerId + '\'' +
                    ", buyerInstanceName='" + buyerInstanceName + '\'' +
                    ", payout='" + payout + '\'' +
                    ", numSlots='" + numSlots + '\'' +
                    ", publisherName='" + publisherName + '\'' +
                    ", shortOrigin='" + shortOrigin + '\'' +
                    ", subID='" + subID + '\'' +
                    ", offerId='" + offerId + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", state='" + state + '\'' +
                    ", qiqScore='" + qiqScore + '\'' +
                    ", fraudScore='" + fraudScore + '\'' +
                    ", leadGuid='" + leadGuid + '\'' +
                    ", leadId='" + leadId + '\'' +
                    ", canReturn=" + canReturn +
                    ", canChangeDisposition=" + canChangeDisposition +
                    ", campaigns=" + campaigns +
                    '}';
        }
    }
}
