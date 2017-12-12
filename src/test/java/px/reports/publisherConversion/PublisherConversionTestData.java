package px.reports.publisherConversion;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;

import java.util.*;

/**
 * Created by kgr on 4/4/2017.
 */
public class PublisherConversionTestData extends ReportTestData {
    private int startMonthOffset = super.startMonthOffset = 2;
    private int durationDays = super.durationDays = 3;
    // filters data
    private ObjectIdentityData publisher;
    protected ObjectIdentityData offer;
    protected String adjustment;
    protected String browser;
    protected String conversionStatus;
    // initial data
    protected Map<String, String> browsersMap;
    protected Map<String, String> conversionStatusMap;
    // table data
    private JSONArray itemsByPublisherID; //AffiliateIdFilter {publisherID}
    protected JSONArray itemsByAdjustment; // AdjustmentFilter {enum index/number, No-0. Yes-1}
    protected JSONArray itemsByOfferID; // OfferIdFilter {offerID}
    protected JSONArray itemsByBrowser; // BrowserFilter {enum description} hasoffersbrowsers
    protected JSONArray itemsByConversionStatus; // ConvStatusFilter {enum description} convstatuses
    protected JSONArray itemsByAllFilters;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String PUBLISHER_FILTER = "AffiliateIdFilter";
    public static final String ADJUSTMENT_FILTER = "AdjustmentFilter";
    public static final String OFFER_FILTER = "OfferIdFilter";
    public static final String BROWSER_FILTER = "BrowserFilter";
    public static final String CONVERSION_STATUS_FILTER = "ConvStatusFilter";

    static {
        // types
        missedHeadersMetricsMap.put("payout", "Currency");
        missedHeadersMetricsMap.put("saleAmount", "Currency");
        // values
        dataMap.put("null", "");
        dataMapping.add(new ValuesMapper("note", dataMap));
        dataMap = new HashMap<>();
        dataMap.put("", "Default");
        dataMapping.add(new ValuesMapper("offerUrl", dataMap));
    }

    public PublisherConversionTestData() {
        this(false);
    }

    public PublisherConversionTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("publisherconversions/report");
        setSorting("creationDate", "desc");
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            this.browsersMap = dataProvider.getPossibleValueFromJSON("HasOffersBrowsers");
            this.conversionStatusMap = dataProvider.getPossibleValueFromJSON("ConvStatuses");
            this.browser = DataHelper.getRandomValueFromList(new ArrayList<>(browsersMap.keySet()));
            // get any conversion status from 1st 100 rows
//            this.conversionStatus = DataHelper.getRandomValueFromList(new ArrayList<>(conversionStatusMap.keySet()));
            conversionStatus = getFilterValue(allRowsArray, "convStatus");
            // get any adjustment from 1st 100 rows
//            this.adjustment = String.valueOf(DataHelper.getRandomInt(0, 1));
            adjustment = getFilterValue(allRowsArray, "adjustment");
            // get any of available publishers => get any publisher from 1st 100 rows
//            this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
            List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
            this.publisher = getFilterObject(publishers, allRowsArray, "affiliateID", "id");
            // get any of available offers => get any offer from 1st 100 rows
//            this.offer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("offers"));
            List<ObjectIdentityData> offers = dataProvider.getCreatedInstancesData("offers");
            this.offer = getFilterObject(offers, allRowsArray, "offerID", "id");
            // rows in table by publisher id
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by offer id
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(offer.getId(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByOfferID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by adjustment
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(ADJUSTMENT_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(adjustment, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAdjustment = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by browser
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BROWSER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(browser, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBrowser = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by conversion status
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(CONVERSION_STATUS_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(conversionStatus, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByConversionStatus = dataProvider.getDataAsJSONArray(requestedURL);
            // all filters
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, ADJUSTMENT_FILTER, OFFER_FILTER, BROWSER_FILTER, CONVERSION_STATUS_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getId(), adjustment, offer.getId(), browser, conversionStatus, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new PublisherConversionTestData.FiltersResetData();
            resetData.setFilterValuesMap();
        }
    }

    // filter data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getOffer() {
        return offer;
    }

    public String getAdjustment() {
        return DataHelper.getYesNo(adjustment);
    }

    public String getBrowser() {
        return browser;
    }

    public String getConversionStatus() {
        return conversionStatus;
    }

    // table data
    public JSONArray getItemsByPublisherID() {
        return itemsByPublisherID;
    }

    public JSONArray getItemsByAdjustment() {
        return itemsByAdjustment;
    }

    public JSONArray getItemsByOfferID() {
        return itemsByOfferID;
    }

    public JSONArray getItemsByBrowser() {
        return itemsByBrowser;
    }

    public JSONArray getItemsByConversionStatus() {
        return itemsByConversionStatus;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "publisher=" + publisher.toString() +
                        ", offer=" + offer.toString() +
                        ", adjustment=" + DataHelper.getYesNo(adjustment) +
                        ", browser=" + browser +
                        ", conversionStatus=" + conversionStatus : "";
        return super.toString() +
                "PublisherConversionTestData{" + instanceDetails + '}';
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
            filterValuesMap.put(ADJUSTMENT_FILTER, DataHelper.getYesNo(adjustment));
            filterValuesMap.put(OFFER_FILTER, offer.getId());
            filterValuesMap.put(BROWSER_FILTER, browser);
            filterValuesMap.put(CONVERSION_STATUS_FILTER, conversionStatus);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            filterLabelValuesMap.put(ADJUSTMENT_FILTER, adjustment);
            filterLabelValuesMap.put(OFFER_FILTER, offer.getId() + " - " + offer.getName());
            filterLabelValuesMap.put(BROWSER_FILTER, browser);
            filterLabelValuesMap.put(CONVERSION_STATUS_FILTER, conversionStatus);
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
                case ADJUSTMENT_FILTER:
                    return itemsByAdjustment;
                case OFFER_FILTER:
                    return itemsByOfferID;
                case BROWSER_FILTER:
                    return itemsByBrowser;
                case CONVERSION_STATUS_FILTER:
                    return itemsByConversionStatus;
            }
            return null;
        }
    }
}
