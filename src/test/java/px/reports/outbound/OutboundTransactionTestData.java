package px.reports.outbound;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.SearchData;
import px.reports.dto.TransactionData;

import java.util.*;
import java.util.stream.Collectors;

import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 12/21/2016.
 */
public class OutboundTransactionTestData extends ReportTestData {
    {
        this.startMonthOffset = 1;
        this.durationDays = 1;
    }

    // filters data
    private List<ObjectIdentityData> publishers;
    protected List<ObjectIdentityData> campaigns;
    protected List<String> resultCodeList;
    protected List<String> resultCodeLiteralsList;
    protected List<String> postTypeList;
    protected String vertical;
    private String transactionType;
    private String transactionTypeText;
    // initial data
    protected Map<String, String> resultCodesMap;
    protected Map<String, String> postTypesMap;
    protected Map<String, String> verticalsMap;
    // table data
    private JSONArray itemsByPublishersID; //"PublisherId":"1006|1010"
    protected JSONArray itemsByBuyerCampaignsID; //BuyerId":"55|97
    protected JSONArray itemsByResultCodes; //BuyerPostResultCodeId":"1|3
    protected JSONArray itemsByPostTypes;  // "PostType":"BuyerPaused|PublisherSubBlocked"
    protected JSONArray itemsByVertical;
    private JSONArray itemsByTransactionType;
    protected JSONArray itemsByAllFilters;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // inbound data
    protected TransactionData transactionData;
    // constants
    public static final String PUBLISHER_FILTER = "PublisherId";
    public static final String BUYER_CAMPAIGN_FILTER = "BuyerId";
    public static final String BUYER_RESULT_CODE_FILTER = "BuyerPostResultCodeId";
    public static final String POST_TYPE_FILTER = "PostType";
    public static final String VERTICAL_FILTER = "vertical";
    public static final String TRANSACTION_TYPE_FILTER = "status";
    // static data
    static final List<String> EXCLUSION_CODES_LIST = Arrays.asList("BuyerNoFilterMatch", "BuyerCapReached", "BuyerMonthCapReached", "FilteredDuplicate", "PublisherTierNotConnected");
    public static Map<String, String> EXCLUSION_CODES = new HashMap<>();

    static {
        missedHeadersMetricsMap.put("postDate", "Date");
        missedHeadersMetricsMap.put("payout", "Currency");
        // data mapping
        dataMap.put("Published", "Realtime");
        dataMap.put("FollowUp", "Rerun");
        dataMapping.add(new ValuesMapper("status", dataMap));
    }

    static {
        EXCLUSION_CODES.put("NotValidError", "Not Valid Error");
        EXCLUSION_CODES.put("BuyerCapReached", "Buyer Daily Cap Reached");
        EXCLUSION_CODES.put("BuyerMonthCapReached", "Buyer Month Revenue Cap Reached");
        EXCLUSION_CODES.put("EscoreTooLow", "Lead Quality Score Too Low");
        EXCLUSION_CODES.put("ExtendedEmailcheckFailed", "Email Verification Failed");
        EXCLUSION_CODES.put("FilteredDuplicate", "Duplicate Lead in last 24 hours");
        EXCLUSION_CODES.put("InvalidFirstNameorLastName", "Invalid Name");
        EXCLUSION_CODES.put("InvalidYearMakeModelVin", "Invalid VIN");
        EXCLUSION_CODES.put("LeadIsNotAccepted", "Lead  Not Accepted By Buyer");
        EXCLUSION_CODES.put("Payoutnotaccepted", "Buyer Payout Too Low");
        EXCLUSION_CODES.put("PreventedPostingOfDuplicate", "Duplicate Lead for Buyer Set Threshold");
        EXCLUSION_CODES.put("PublisherTierNotConnected", "Below Quality Floor");
        EXCLUSION_CODES.put("RequestTimedOut", "Buyer Timed Out");
        EXCLUSION_CODES.put("BuyerNoFilterMatch", "Blocked By Filter");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-AffiliateData.theLeadID", "Filtered out by Lead ID");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-BaeFunc.Age", "Filtered out by Age");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-BaeFunc.AgeMonth", "Filtered out by Age");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-BaeFunc.DayOfWeekNow", "Filtered out by Day");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-BaeFunc.Sum", "Filtered out by Calculations");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-BaeFunc.TimeNow", "Filtered out by Time");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-ContactData.Address", "Filtered out by Address");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-ContactData.ResidenceType", "Filtered out by Property Residence");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-ContactData.State", "Filtered out by State");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-ContactData.ZIPCode", "Filtered out by Zip Code");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-CurrentPolicy.InsuranceCompany", "Filtered out by Current Insurance Provider");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Driver.CreditRating", "Filtered out by Credit Rating");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Driver.Education", "Filtered out by Education");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Driver.LicenseStatus", "Filtered out by LicenseStatus");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Driver.RequiresSR22Filing", "Filtered out by Requires SR22 Filing");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-e.CurrentSecuritySystemCompany", "Filtered out by Current Security System Company");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Home.ElectricityBill", "Filtered out by Electricity Bill");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Home.OwnRented", "Filtered out by Property Ownership");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Home.PropertyOwned", "Filtered out by Property Ownership");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Home.PropertyType", "Filtered out by Property Type");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Home.RoofShade", "Filtered out by Roof Shade");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-MajorViolation.Description", "Filtered out by Description");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Mortgage.BankruptcyTime", "Filtered out by Bankruptcy Time");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Mortgage.FirstMortgageBalance", "Filtered out by First Mortgage Balance");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Mortgage.ForeclosureTime", "Filtered out by Foreclosure Time");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Mortgage.LoanAmount", "Filtered out by Loan Amount");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Mortgage.LoanType", "Filtered out by Loan Type");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Mortgage.PropertyType", "Filtered out by Property Type");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Person.CreditRating", "Filtered out by Credit Rating");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-Person.HouseHoldIncome", "Filtered out by Household Income");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-PublisherSub.Type", "Filtered out by Publisher sub Type");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-QuoteRequest.Vehicles", "Filtered out by Vehicles");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-RequestedPolicy.CoverageType", "Filtered out by Desired Coverage");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-SecuritySystem.ProInstall", "Filtered out by Pro Installation");
        EXCLUSION_CODES.put("BuyerNoFilterMatch-SecuritySystem.SecurityUsage", "Filtered out by Security Usage");
    }

    public OutboundTransactionTestData() {
        this(false);
    }

    public OutboundTransactionTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("outboundtransactions/report");
        setSorting("postDate", "desc");
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            setFiltersData();
            // rows in table by publisher IDs
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(publishers), "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublishersID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by campaign IDs
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(campaigns), "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerCampaignsID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by result codes
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_RESULT_CODE_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(resultCodeList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByResultCodes = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by post types
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(POST_TYPE_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(postTypeList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPostTypes = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by vertical
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(VERTICAL_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by transaction type
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(TRANSACTION_TYPE_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(transactionType, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByTransactionType = dataProvider.getDataAsJSONArray(requestedURL);
            // all filters
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, BUYER_CAMPAIGN_FILTER, BUYER_RESULT_CODE_FILTER,
                            POST_TYPE_FILTER, VERTICAL_FILTER, TRANSACTION_TYPE_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(publishers), "|"),
                                    StringUtils.join(ObjectIdentityData.getAllIDs(campaigns), "|"),
                                    StringUtils.join(resultCodeList, "|"), StringUtils.join(postTypeList, "|"),
                                    vertical, transactionType, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new OutboundTransactionTestData.FiltersResetData();
            resetData.setFilterValuesMap();
            // set inbound data not create each time
            this.transactionData = new OutboundData();
        }
    }


    public OutboundTransactionTestData setFiltersData() {
        this.resultCodesMap = dataProvider.getPossibleValueFromJSON("BuyerPostResultCodes", "enumSequenceIndex");
        this.postTypesMap = dataProvider.getPossibleValueFromJSON("PostTypes");
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
        // exclude some exceptional codes
        EXCLUSION_CODES_LIST.forEach(exclusion -> {
            resultCodesMap.remove(exclusion);
            postTypesMap.remove(exclusion);
        });
        this.resultCodeLiteralsList = DataHelper.getRandomListFromList(new ArrayList<>(resultCodesMap.keySet()), DataHelper.getRandomInt(1, 10));
        // in filter is set by name but in request sent as indexes
        this.resultCodeList = resultCodeLiteralsList.stream()
                .map(code -> resultCodesMap.get(code)).collect(Collectors.toList());
        this.postTypeList = DataHelper.getRandomListFromList(new ArrayList<>(postTypesMap.keySet()), DataHelper.getRandomInt(1, 10));
        this.vertical = DataHelper.getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
        // => get any campaign from 1st 100 rows
        this.campaigns = dataProvider.getCreatedInstancesData("buyerInstances");
        if(Config.isAdmin()) {
            this.transactionType = DataHelper.getRandomValueFromList(new ArrayList<>(dataMap.keySet()));
            this.transactionTypeText = dataMap.get(transactionType);
            /*// get list of available publishers
            this.publishers = dataProvider.getCreatedInstancesData("publishers");
            int count = DataHelper.getRandomInt(1, 10);
            Set<ObjectIdentityData> temp = new HashSet<>();
            for (int i = 0; i < count; i++) {
                temp.add(publishers.get(DataHelper.getRandomInt(publishers.size())));
            }
            publishers = new ArrayList<>(temp);
            Comparator<ObjectIdentityData> comparator = (ObjectIdentityData p1, ObjectIdentityData p2)
                    -> Integer.parseInt(p1.getId()) - Integer.parseInt(p2.getId());
            Collections.sort(publishers, comparator);
            // get list of available buyer campaigns
            this.campaigns = dataProvider.getCreatedInstancesData("buyerInstances");
            count = DataHelper.getRandomInt(1, 10);
            temp = new HashSet<>();
            for (int i = 0; i < count; i++) {
                temp.add(campaigns.get(DataHelper.getRandomInt(campaigns.size())));
            }
            campaigns = new ArrayList<>(temp);*/
            // => get any publisher from 1st 100 rows
            this.publishers = dataProvider.getCreatedInstancesData("publishers");
            publishers = getFilterObjects(publishers, allRowsArray, "publisherId", "id");
            campaigns = getFilterObjects(campaigns, allRowsArray, "buyerId", "id");
        }
        return this;
    }

    // filter data
    public List<ObjectIdentityData> getPublishers() {
        return publishers;
    }

    public List<ObjectIdentityData> getCampaigns() {
        return campaigns;
    }

    public List<String> getResultCodeLiteralsList() {
        return resultCodeLiteralsList;
    }

    public List<String> getPostTypeList() {
        return postTypeList;
    }

    public String getVertical() {
        return vertical;
    }

    public String getTransactionType() {
        return transactionTypeText;
    }

    // table data
    public JSONArray getItemsByPublishersID() {
        return itemsByPublishersID;
    }

    public JSONArray getItemsByBuyerCampaignsID() {
        return itemsByBuyerCampaignsID;
    }

    public JSONArray getItemsByResultCodes() {
        return itemsByResultCodes;
    }

    public JSONArray getItemsByPostTypes() {
        return itemsByPostTypes;
    }

    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByTransactionType() {
        return itemsByTransactionType;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public SearchData getSearchData(FieldFormatObject field) {
        return new OutboundSearchData(this, field);
    }

    public TransactionData getTransactionData() {
        return transactionData;
    }

    public boolean hasTransactionData(String postType) {
        return postType.equals("PING") || postType.equals("POST") || postType.equals("DIRECTPOST");
    }

    public static boolean isExclusionCode(String code) {
        return EXCLUSION_CODES.containsKey(code);
    }

    public static String getExclusionCodeValueByKey(String key) {
        return EXCLUSION_CODES.get(key);
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList("PostDate", "PostDate2"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "publishers=" + publishers.stream().map(ObjectIdentityData::toString).collect(Collectors.joining(", ")) +
                        ", campaigns=" + campaigns.stream().map(ObjectIdentityData::toString).collect(Collectors.joining(", ")) +
                        ", resultCodeLiteralsList=" + resultCodeLiteralsList +
                        ", resultCodeList=" + resultCodeList +
                        ", postTypeList=" + postTypeList +
                        ", vertical=" + vertical +
                        ", transactionType=" + transactionType : "";
        return super.toString() +
                "OutboundTransactionTestData{" +
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
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
            this._fromPeriodKey = "PostDate";
            this._toPeriodKey = "PostDate2";
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(PUBLISHER_FILTER, StringUtils.join(ObjectIdentityData.getAllIDs(publishers), "|"));
            filterValuesMap.put(BUYER_CAMPAIGN_FILTER, StringUtils.join(ObjectIdentityData.getAllIDs(campaigns), "|"));
            filterValuesMap.put(BUYER_RESULT_CODE_FILTER, StringUtils.join(resultCodeList, "|"));
            filterValuesMap.put(POST_TYPE_FILTER, StringUtils.join(postTypeList, "|"));
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            filterValuesMap.put(TRANSACTION_TYPE_FILTER, transactionType);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publishers.stream().map(p -> p.getId() +
                    "-" + p.getName()).collect(Collectors.joining(", ")));
            filterLabelValuesMap.put(BUYER_CAMPAIGN_FILTER, campaigns.stream().map(p -> p.getId() +
                    "-" + p.getName()).collect(Collectors.joining(", ")));
            filterLabelValuesMap.put(BUYER_RESULT_CODE_FILTER, resultCodeLiteralsList.toString());
            filterLabelValuesMap.put(POST_TYPE_FILTER, postTypeList.toString());
            filterLabelValuesMap.put(VERTICAL_FILTER, vertical);
            filterLabelValuesMap.put(TRANSACTION_TYPE_FILTER, transactionTypeText);
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
                case PUBLISHER_FILTER:
                    return itemsByPublishersID;
                case BUYER_CAMPAIGN_FILTER:
                    return itemsByBuyerCampaignsID;
                case BUYER_RESULT_CODE_FILTER:
                    return itemsByResultCodes;
                case POST_TYPE_FILTER:
                    return itemsByPostTypes;
                case VERTICAL_FILTER:
                    return itemsByVertical;
                case TRANSACTION_TYPE_FILTER:
                    return itemsByTransactionType;
            }
            return null;
        }
    }

    class OutboundData implements TransactionData {
        private JSONObject transactionObject;

        @Override
        public String getRequestData() {
            try {
                return StringEscapeUtils.unescapeJava(String.valueOf(transactionObject.get("requestData")));
            } catch (JSONException e) {
                throw new JSONException(e.getMessage() + "\t" + transactionObject.toString(), e);
            }
        }

        @Override
        public String getResponseData() {
            try {
                return StringEscapeUtils.unescapeJava(String.valueOf(transactionObject.get("responseData")));
            } catch (JSONException e) {
                throw new JSONException(e.getMessage() + "\t" + transactionObject.toString(), e);
            }
        }

        @Override
        public void setData(JSONObject jsonObject) {
            String leadResponseId = String.valueOf(jsonObject.get("leadResponseId"));
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/outboundtransactions/transactionDetails")
                    .withParams("leadResponseId", leadResponseId)
                    .build().getRequestedURL();
            String response = dataProvider.getDataAsString(requestedURL);
            this.transactionObject = new JSONObject(response);
            TRANSACTIONS_SET.add(leadResponseId);
        }
    }

    private final class OutboundSearchData extends SearchData {

        OutboundSearchData(ReportTestData testData, FieldFormatObject field) {
            super(testData, field);
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(searchBy, "PostDate", "PostDate2"),
                            Arrays.asList(searchValue, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsSearchBy = dataProvider.getDataAsJSONArray(requestedURL);
            this.itemsSearchByCount = dataProvider.getCurrentTotal();
        }
    }
}