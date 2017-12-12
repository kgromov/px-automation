package px.reports.pingPostTransactions;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.InboundData;
import px.reports.dto.SearchData;

import java.util.*;
import java.util.stream.Collectors;

import static px.reports.dto.FieldFormatObject.*;
import static px.reports.outbound.OutboundTransactionTestData.EXCLUSION_CODES;

/**
 * Created by kgr on 5/10/2017.
 */
public class PingPostTransactionsTestData extends ReportTestData {
    {
        durationDays = 1;
        startMonthOffset = 2;
    }

    // filters data
    private ObjectIdentityData publisher;
    private ObjectIdentityData subID;
    private List<String> resultCodeList;
    private List<String> resultCodeLiteralsList;
    private List<String> postTypeList;
    private String vertical;
    // initial data
    private Map<String, String> resultCodesMap;
    private Map<String, String> postTypesMap;
    private Map<String, String> verticalsMap;
    // sub instances
    private List<ObjectIdentityData> subIDs;        // buyerinstancesbyparent?parent=
    // table data
    private JSONArray itemsByPublishersID;
    private JSONArray itemsByPublisherInstanceGUID;
    private JSONArray itemsByResultCodes; // "BuyerPostResultCodeId":"1|3
    private JSONArray itemsByPostTypes;   // "PostType":"PING|POST"
    private JSONArray itemsByVertical;
    private JSONArray itemsByAllFilters;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // inbound data
    private InboundData inboundData;
    // constants
    public static final String PUBLISHER_FILTER = "PublisherId";
    public static final String PUBLISHER_INSTANCE_FILTER = "SubId";
    public static final String PUBLISHER_RESULT_CODE_FILTER = "PublisherPostResultCodeId";
    public static final String POST_TYPE_FILTER = "PostType";
    public static final String VERTICAL_FILTER = "vertical";
    // static data
    private static final List<String> exclusionCodesList = Arrays.asList("BuyerNoFilterMatch", "BuyerCapReached", "BuyerMonthCapReached", "FilteredDuplicate", "PublisherTierNotConnected");

    static {
        missedHeadersMetricsMap.put("postDate", DATE_FORMAT);
        missedHeadersMetricsMap.put("payoutReported", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("payoutCalculated", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("transactionDuration", TIMEOUT_FORMAT);
        // data mapping
        dataMapping.add(new ValuesMapper("publisherPostResultCode", EXCLUSION_CODES));
    }

    public PingPostTransactionsTestData() {
        this(false);
    }

    public PingPostTransactionsTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("pingposttransactions/report");
        setSorting("postDate", "desc");
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
        setHeaders();
        // just for debug - whether unique postDate or not
        Set<String> dateSet = new HashSet<>(DataHelper.getListFromJSONArrayByKey(allRowsArray, "postDate"));
        if (dateSet.size() < allRowsArray.length())
            log.info(String.format("DEBUG\tNot unique sorting data 'postDate'. All rows = '%d', unique postDate rows = '%d'", allRowsArray.length(), dateSet.size()));
        if (isInstances) {
            setFiltersData();
            // rows in table by publisher GUID
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublishersID = dataProvider.getDataAsJSONArray(requestedURL);
            // items by publisher instance guid
            if (hasPublisherInstances()) {
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(publisher.getId(), subID.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByPublisherInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
            }
            // rows in table by result codes
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_RESULT_CODE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(StringUtils.join(resultCodeList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByResultCodes = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by post types
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(POST_TYPE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(StringUtils.join(postTypeList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPostTypes = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by vertical
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            //  rows in table after all filters
            List<String> paramsKeys = new ArrayList<>(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_RESULT_CODE_FILTER,
                    POST_TYPE_FILTER, VERTICAL_FILTER, "FromPeriod", "ToPeriod"));
            if (hasPublisherInstances()) paramsKeys.add(PUBLISHER_INSTANCE_FILTER);
            List<String> paramsValues = new ArrayList<>(Arrays.asList(publisher.getId(), StringUtils.join(resultCodeList, "|"),
                    StringUtils.join(postTypeList, "|"), vertical, fromPeriod, toPeriod));
            if (hasPublisherInstances()) paramsValues.add(subID.getGuid());
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(paramsKeys, paramsValues)
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new PingPostTransactionsTestData.FiltersResetData();
            resetData.setFilterValuesMap();
            // set inbound data not create each time
            TRANSACTIONS_SET = new HashSet<>();
            this.inboundData = new PingPostInboundData();
        }
    }

    public PingPostTransactionsTestData setFiltersData() {
        this.resultCodesMap = dataProvider.getPossibleValueFromJSON("BuyerPostResultCodes", "enumSequenceIndex");
        this.postTypesMap = dataProvider.getPossibleValueFromJSON("TransactionPingPostTypes");
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
        // exclude some exceptional codes
        exclusionCodesList.forEach(exclusion -> {
            resultCodesMap.remove(exclusion);
            postTypesMap.remove(exclusion);
        });
        this.resultCodeLiteralsList = DataHelper.getRandomListFromList(new ArrayList<>(resultCodesMap.keySet()), DataHelper.getRandomInt(1, 10));
        // in filter is set by name but in request sent as indexes
        this.resultCodeList = resultCodeLiteralsList.stream()
                .map(code -> resultCodesMap.get(code)).collect(Collectors.toList());
        this.postTypeList = DataHelper.getRandomListFromList(new ArrayList<>(postTypesMap.keySet()), postTypesMap.size());
        this.vertical = DataHelper.getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
//            this.vertical = getFilterValue(allRowsArray, "vertical"); // different register
        // get list of available publishers and select any of them
//            this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
        List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
        this.publisher = getFilterObject(publishers, allRowsArray, "publisherId", "id");
        // publisher instances - filter items
        this.subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                Collections.singletonList("publisherId"), Collections.singletonList(publisher.getId()));
        this.subID = subIDs.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(subIDs);
        return this;
    }

    // filter data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getSubID() {
        return subID;
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

    public List<ObjectIdentityData> getSubIDs() {
        return subIDs;
    }

    // table data
    public JSONArray getItemsByPublishersID() {
        return itemsByPublishersID;
    }

    public JSONArray getItemsByPublisherInstanceGUID() {
        return itemsByPublisherInstanceGUID;
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

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public boolean hasPublisherInstances() {
        return subIDs != null && !subIDs.isEmpty();
    }

    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public InboundData getInboundData() {
        return inboundData;
    }

    public SearchData getSearchData(FieldFormatObject field) {
        return new PingPostTransactionSearchData(this, field);
    }

    @Override
    public String toString() {
        String subIdDetails = isInstances && !subIDs.isEmpty() ? subID.toString() : "";
        String instanceDetails = isInstances ?
                "publisher=" + publisher +
                        ", subID=" + subIdDetails +
                        ", subIDs=" + subIDs.stream().map(ObjectIdentityData::getId).collect(Collectors.joining(", ")) +
                        ", resultCodeLiteralsList=" + resultCodeLiteralsList +
                        ", resultCodeList=" + resultCodeList +
                        ", postTypeList=" + postTypeList +
                        ", vertical=" + vertical : "";
        return super.toString() +
                "\nPingPostTransactionsTestData{" +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {

        public FiltersResetData() {
            this._instanceGroup = instanceGroup;
            this._url = headersURL.replace("?", "&");
            this._sortBy = sortBy;
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getId());
            if (hasPublisherInstances())
                filterValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getGuid());
            filterValuesMap.put(PUBLISHER_RESULT_CODE_FILTER, StringUtils.join(resultCodeList, "|"));
            filterValuesMap.put(POST_TYPE_FILTER, StringUtils.join(postTypeList, "|"));
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            if (hasPublisherInstances())
                filterLabelValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getId());
            filterLabelValuesMap.put(PUBLISHER_RESULT_CODE_FILTER, resultCodeLiteralsList.toString());
            filterLabelValuesMap.put(POST_TYPE_FILTER, postTypeList.toString());
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
                case PUBLISHER_FILTER:
                    return itemsByPublishersID;
                case PUBLISHER_INSTANCE_FILTER:
                    return itemsByPublisherInstanceGUID;
                case PUBLISHER_RESULT_CODE_FILTER:
                    return itemsByResultCodes;
                case POST_TYPE_FILTER:
                    return itemsByPostTypes;
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

    private class PingPostInboundData implements InboundData {

        @Override
        public String getData() {
            return null;
        }

        @Override
        public String getData(JSONObject jsonObject) {
            String leadGuid = String.valueOf(jsonObject.get("leadGuid"));
            String postType = String.valueOf(jsonObject.get(PingPostTransactionsColumnsEnum.POST_TYPE.getValue()));
            String transactionId = String.valueOf(jsonObject.get(PingPostTransactionsColumnsEnum.TRANSACTION_ID.getValue()));
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/pingposttransactions/inboundXml")
                    .withParams(Arrays.asList("leadGuid", "postType", "transactionId"),
                            Arrays.asList(leadGuid, postType, transactionId))
                    .build().getRequestedURL();
            String response = dataProvider.getDataAsString(requestedURL);
            TRANSACTIONS_SET.add(transactionId);
            response = response.length() > 2 && !response.startsWith("<") ? response.substring(1, response.length() - 1) : response;
            return StringEscapeUtils.unescapeJava(response);
        }
    }

    private final class PingPostTransactionSearchData extends SearchData {

        PingPostTransactionSearchData(ReportTestData testData, FieldFormatObject field) {
            super(testData, field);
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
}