package px.reports.inbound;

import config.Config;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.InboundData;
import px.reports.dto.SearchData;

import java.util.*;

import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 12/21/2016.
 */
public class InboundTransactionTestData extends ReportTestData {
    {
        startMonthOffset = 1;
        durationDays = 4;
    }

    // filter data
    private ObjectIdentityData publisher;
    protected ObjectIdentityData offer;
    protected String resultCode;
    protected String vertical;
    // initial data
    protected Map<String, String> resultCodesMap;
    protected Map<String, String> verticalsMap;
    // table data
    private JSONArray itemsByPublisherID;
    protected JSONArray itemsByResultCode;
    protected JSONArray itemsByOfferID;
    protected JSONArray itemsByVertical;
    protected JSONArray itemsByAllFilters;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // inbound data
    protected InboundData inboundData;
    // constants
    public static final String PUBLISHER_FILTER = "PublisherIDFilter";
    public static final String RESULT_CODES_FILTER = "ReasonsFilter";
    public static final String VERTICAL_FILTER = "VerticalFilter";
    public static final String OFFER_FILTER = "OfferIdFilter";

    static {
        missedHeadersMetricsMap.put("date", "Date");
    }

    public InboundTransactionTestData() {
        this(false);
    }

    public InboundTransactionTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("inboundtransactions/report");
        setSorting("date", "desc");
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            setFiltersData();
            // rows in table by publisher id
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by result code
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(RESULT_CODES_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(resultCode.replaceAll(" ", "+"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByResultCode = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by vertical
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by offer id
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(offer.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByOfferID = dataProvider.getDataAsJSONArray(requestedURL);
            // all filters
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, RESULT_CODES_FILTER, VERTICAL_FILTER, OFFER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getId(), resultCode.replaceAll(" ", "+"), vertical, offer.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new InboundTransactionTestData.FiltersResetData();
            resetData.setFilterValuesMap();
            // set inbound data not create each time
            TRANSACTIONS_SET = new HashSet<>();
            this.inboundData = new InboundTransactionData();
        }
    }

    public InboundTransactionTestData setFiltersData() {
        this.resultCodesMap = dataProvider.getPossibleValueFromJSON("InboundPostResultCodes");
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
        this.resultCode = getRandomValueFromList(new ArrayList<>(resultCodesMap.keySet()));
        this.vertical = getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
//            this.vertical = getFilterValue(allRowsArray, "vertical"); // different register
        // get any of available offers
//            this.offer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("offers"));
        List<ObjectIdentityData> offers = dataProvider.getCreatedInstancesData("offers");
        this.offer = getFilterObject(offers, allRowsArray, "offerId", "id");
        if(Config.isAdmin()){
            // get any of available publishers
//            this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
            List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
            this.publisher = getFilterObject(publishers, allRowsArray, "publisherId", "id");
        }
        return this;
    }

    // filters data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getOffer() {
        return offer;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getVertical() {
        return vertical;
    }

    // table data
    public JSONArray getItemsByPublisherID() {
        return itemsByPublisherID;
    }

    public JSONArray getItemsByResultCode() {
        return itemsByResultCode;
    }

    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByOfferID() {
        return itemsByOfferID;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public InboundData getInboundData() {
        return inboundData;
    }

    public SearchData getSearchData(FieldFormatObject field) {
        return new InboundSearchData(this, field);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "publisher=" + publisher.toString() +
                        ", offer=" + offer.toString() +
                        ", resultCode=" + resultCode +
                        ", vertical=" + vertical : "";
        return super.toString() +
                "InboundTransactionTestData{" +
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
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getId());
            filterValuesMap.put(RESULT_CODES_FILTER, resultCode.replaceAll(" ", "+"));
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            filterValuesMap.put(OFFER_FILTER, offer.getId());
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            filterLabelValuesMap.put(RESULT_CODES_FILTER, resultCode);
            filterLabelValuesMap.put(VERTICAL_FILTER, vertical);
            filterLabelValuesMap.put(OFFER_FILTER, offer.getId() + " - " + offer.getName());
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
                    return itemsByPublisherID;
                case RESULT_CODES_FILTER:
                    return itemsByResultCode;
                case VERTICAL_FILTER:
                    return itemsByVertical;
                case OFFER_FILTER:
                    return itemsByOfferID;
            }
            return null;
        }
    }

    protected final class InboundTransactionData implements InboundData {

        @Override
        public String getData() {
            return null;
        }

        @Override
        public String getData(JSONObject jsonObject) {
            String leadGuid = String.valueOf(jsonObject.get("leadGuid"));
            String transactionId = String.valueOf(jsonObject.get(InboundTransactionsColumnsEnum.TRANSACTION_ID.getValue()));
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/inboundtransactions/inboundXml")
                    .withParams(Arrays.asList("leadGuid", "transactionId"),
                            Arrays.asList(leadGuid, transactionId))
                    .build().getRequestedURL();
            String response = dataProvider.getDataAsString(requestedURL);
            TRANSACTIONS_SET.add(transactionId);
            response = response.length() > 2 && !response.startsWith("<") ? response.substring(1, response.length() - 1) : response;
            return StringEscapeUtils.unescapeJava(response);
        }
    }

    private final class InboundSearchData extends SearchData {

        InboundSearchData(ReportTestData testData, FieldFormatObject field) {
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