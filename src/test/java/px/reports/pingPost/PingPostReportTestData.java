package px.reports.pingPost;

import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;
import java.util.stream.Collectors;

import static pages.groups.MetaData.TIMEOUT_FORMAT;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.pingPost.PingPostReportFiltersEnum.DEFAULT_REPORT_TYPE;

/**
 * Created by kgr on 5/30/2017.
 */
public class PingPostReportTestData extends ReportTestData {
    // filters data
    private ObjectIdentityData publisher;
    private ObjectIdentityData subID;
    private String reportType = DEFAULT_REPORT_TYPE;
    // sub instances
    private List<ObjectIdentityData> subIDs;
    // table data
    private JSONArray itemsByReportType;
    private JSONArray itemsByPublisherID;
    private JSONArray itemsByPublisherInstanceGUID;
    private JSONArray itemsByAllFilters;
    private int itemsByReportTypeCount;
    // filters reset
    private List<FieldFormatObject> fieldsByDefaultReportType;
    private AbstractFiltersResetData resetData;
    // constants
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String PUBLISHER_FILTER = "PublisherId";
    public static final String PUBLISHER_INSTANCE_FILTER = "PublisherInstanceGuid";
    // static data
    public static final Map<String, String> REPORT_TYPE_MAP = new HashMap<>();
    private static final List<String> bubbleGraphics = Arrays.asList("Vertical", "Publisher");

    static {
        REPORT_TYPE_MAP.put("Hour", "hourly");
        REPORT_TYPE_MAP.put("Day", "daily");
        REPORT_TYPE_MAP.put("Week", "weekly");
        REPORT_TYPE_MAP.put("Vertical", "vertical");
        REPORT_TYPE_MAP.put("Publisher", "publisher");
        // metrics format
        missedHeadersMetricsMap.put("avgPingTimeout", TIMEOUT_FORMAT);
        missedHeadersMetricsMap.put("avgPostTimeout", TIMEOUT_FORMAT);
        // popup title mapping
        popupTitleMap.put("averagePingBidRep", "Ping Payout spread");
        popupTitleMap.put("averagePingBidCalc", "Ping Forecast spread");
        popupTitleMap.put("cplCalc", "Calculated CPL spread");
        popupTitleMap.put("cplRep", "Reported CPL spread");
        popupTitleMap.put("avgPingTimeout", "Ping Response Time spread");
        popupTitleMap.put("avgPostTimeout", "Post Response Time spread");
        popupTitleMap.put("pings", "Ping responses");
        popupTitleMap.put("posts", "Post responses");
    }

    public PingPostReportTestData() {
        this(false);
    }

    private PingPostReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("pingpost");
        setSorting("dateTime", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
    }

    public PingPostReportTestData(String reportType) {
        this(true);
        this.reportType = reportType;
        super.setHeaders();
        // choose any publisher from available list
        this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
        // publisher instances - filter items
        this.subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                Collections.singletonList("publisherGuid"), Collections.singletonList(publisher.getGuid()));
        this.subID = subIDs.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(subIDs);
        // items in table by buyer report type
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        this.itemsByReportTypeCount = dataProvider.getCurrentTotal();
        // rows in table by publisher id
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(publisher.getId(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByPublisherID = dataProvider.getDataAsJSONArray(requestedURL);
        // items by publisher instance guid
        if (hasPublisherInstances()) {
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                    .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getId(), subID.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
        }
        //  rows in table after all filters
        List<String> paramsKeys = new ArrayList<>(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"));
        if (hasPublisherInstances()) paramsKeys.add(PUBLISHER_INSTANCE_FILTER);
        List<String> paramsValues = new ArrayList<>(Arrays.asList(publisher.getId(), fromPeriod, toPeriod));
        if (hasPublisherInstances()) paramsValues.add(subID.getGuid());
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(paramsKeys, paramsValues)
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new PingPostReportTestData.FiltersResetData();
        resetData.setFilterValuesMap();
    }

    // filter data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getSubID() {
        return subID;
    }

    public String getReportType() {
        return reportType;
    }

    public List<ObjectIdentityData> getSubIDs() {
        return subIDs;
    }

    // table data
    public JSONArray getItemsByReportType() {
        return itemsByReportType;
    }

    public JSONArray getItemsByPublisherID() {
        return itemsByPublisherID;
    }

    public JSONArray getItemsByPublisherInstanceGUID() {
        return itemsByPublisherInstanceGUID;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getItemsByReportTypeCount() {
        return itemsByReportTypeCount;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public boolean hasPublisherInstances() {
        return subIDs != null && !subIDs.isEmpty();
    }

    // different reportType - different graphics set and default behaviour
    public boolean hasBubbleChart(String reportType) {
        return bubbleGraphics.contains(reportType);
    }

    // to set itemsByReport type
    public void setItemsByReportType(String reportType) {
        this.reportType = reportType;
        // items in table by buyer report type
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        this.itemsByReportTypeCount = dataProvider.getCurrentTotal();
        // set hasTotalRow for items by reportType
        this.hasTotalRow = itemsByReportType.length() > 0 && DataHelper.hasJSONValue(itemsByReportType.getJSONObject(0), "Total");
    }

    @Override
    public List<FieldFormatObject> getFields() {
        return isInstances && ((PingPostReportTestData.FiltersResetData) resetData).isChangeToDefaultFields() ? fieldsByDefaultReportType : fields;
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/pingpost/metadata")
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .build().getRequestedURL();
    }

    @Override
    protected void setHeaders() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/pingpost/metadata")
                .withParams("ReportType", REPORT_TYPE_MAP.get(DEFAULT_REPORT_TYPE))
                .build().getRequestedURL();
        JSONArray headers = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        List<String> headersList = DataHelper.getListFromJSONArrayByKey(headers, "field");
        // common
        this.fieldsByDefaultReportType = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            FieldFormatObject field = new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i);
            // set popup title according to mapping
            if (field.hasPopup()) field.getPopupData().setTitle(popupTitleMap.get(field.getName()));
            fieldsByDefaultReportType.add(field);
        }
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(DEFAULT_REPORT_TYPE))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    @Override
    public String toString() {
        String subIdDetails = isInstances && !subIDs.isEmpty() ? subID.toString() : "";
        String instanceDetails = isInstances ?
                "reportType=" + reportType +
                        ", publisher=" + publisher +
                        ", subID=" + subIdDetails +
                        ", subIDs=" + subIDs.stream().map(ObjectIdentityData::getId).collect(Collectors.joining(", "))
                : "";
        return super.toString() +
                "\nPingPostReportTestData{" +
                instanceDetails +
                '}';
    }

    class FiltersResetData extends AbstractFiltersResetData {
        private String defaultReportTypeURL;
        public FiltersResetData() {
            this.defaultReportTypeURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", REPORT_TYPE_MAP.get(DEFAULT_REPORT_TYPE))
                    .build().getRequestedURL().replace("?", "&");
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        boolean isChangeToDefaultFields() {
            return !filterValuesMap.containsKey(REPORT_TYPE_FILTER);
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getId());
            if (hasPublisherInstances())
                filterValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getGuid());
            filterValuesMap.put(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            if (hasPublisherInstances())
                filterLabelValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getId());
            filterLabelValuesMap.put(REPORT_TYPE_FILTER, reportType);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            if (resetIterationMap.size() > 1) {
                resetIterationMap.remove(REPORT_TYPE_FILTER);
            }
            if (resetIterationMap.size() > 1)
                return itemsByFiltersCombination;
            else if (resetIterationMap.size() == 0)
                return allRowsArray;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case PUBLISHER_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByPublisherID;
                case PUBLISHER_INSTANCE_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByPublisherInstanceGUID;
                case REPORT_TYPE_FILTER:
                    return itemsByReportType;
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
            // cause data in table supposed to be without reset filter
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            resetIterationMap.remove(filterReset);
            // if the last key in map 'BuyerCategory' return
            if (resetIterationMap.isEmpty() || (resetIterationMap.size() == 1 && resetIterationMap.containsKey(REPORT_TYPE_FILTER)))
                return;
            // remove 'ReportType' cause it's url not filter parameter
            resetIterationMap.remove(REPORT_TYPE_FILTER);
            // keys in filter json request
            List<String> keys = new ArrayList<>(resetIterationMap.keySet());
            keys.addAll(Arrays.asList(_fromPeriodKey, _toPeriodKey));
            // values in filter json request
            List<String> values = new ArrayList<>(resetIterationMap.values());
            values.addAll(Arrays.asList(fromPeriod, toPeriod));
            // get rows in table after filter combination
            String requestedURL = new RequestedURL.Builder()
                    .withAbsoluteURL(isChangeToDefaultFields() || filterReset.equals(REPORT_TYPE_FILTER) ? defaultReportTypeURL : _url)
                    .filter(keys, values)
                    .sort(_sortBy, _sortHow)
                    .build().getRequestedURL();
            this.itemsByFiltersCombination = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }
}