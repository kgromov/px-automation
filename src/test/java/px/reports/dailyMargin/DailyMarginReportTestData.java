package px.reports.dailyMargin;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.PX_REPORT_DATE_PATTERN;
import static pages.groups.MetaData.DOUBLE_FORMAT;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.dailyMargin.DailyMarginReportFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.dto.FieldFormatObject.CURRENCY_FORMAT;
import static px.reports.dto.FieldFormatObject.PERCENTAGE_FORMAT;

/**
 * Created by kgr on 5/24/2017.
 */
public class DailyMarginReportTestData extends ReportTestData {
    // filters data
    private ObjectIdentityData publisher;
    private ObjectIdentityData subID;
    private List<String> verticalList;
    private List<String> buyerCategoriesList;
    private String reportType = DEFAULT_REPORT_TYPE;
    // initial data
    private Map<String, String> buyerCategoriesMap;
    private Map<String, String> verticalsMap;
    // table data
    private JSONArray itemsByReportType;
    private JSONArray itemsByPublisherGUID;
    private JSONArray itemsByPublisherInstanceGUID;
    private JSONArray itemsByVertical;
    private JSONArray itemsByBuyerCategories;
    private JSONArray itemsByAllFilters;
    private int itemsByReportTypeCount;
    // sub instances
    private List<ObjectIdentityData> subIDs;
    // filters reset
    private List<FieldFormatObject> fieldsByDefaultReportType;
    private AbstractFiltersResetData resetData;
    // constants
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String PUBLISHER_FILTER = "ParentPublisherGuid";
    public static final String PUBLISHER_INSTANCE_FILTER = "PublisherInstanceGuid";
    public static final String VERTICAL_FILTER = "Vertical";
    public static final String BUYER_CATEGORIES_FILTER = "BuyerCategory";
    // static data
    public static final Map<String, String> REPORT_TYPE_MAP = new HashMap<>();
    private static final List<String> bubbleGraphics = Arrays.asList("Publisher manager", "Offer ID", "Vertical", "Quality tier");

    static {
        REPORT_TYPE_MAP.put("Hour", "hourly");
        REPORT_TYPE_MAP.put("Day", "daily");
        REPORT_TYPE_MAP.put("Week", "weekly");
        REPORT_TYPE_MAP.put("Month", "monthly");
        REPORT_TYPE_MAP.put("Publisher manager", "manager");
        REPORT_TYPE_MAP.put("Offer ID", "offerId");
        REPORT_TYPE_MAP.put("Vertical", "vertical");
        REPORT_TYPE_MAP.put("Quality tier", "qualitytier");
        // missed fields formats
        missedHeadersMetricsMap.put("soldRatio", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("forecastReturn", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("forecastPubReturn", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("cpl", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("marginPerc", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("netMarginPerc", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("pubMargin", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("soldTimes", DOUBLE_FORMAT);
        missedHeadersMetricsMap.put("soldSharedPerc", PERCENTAGE_FORMAT);
        // data mapping
        dataMap.put("false", "No");
        dataMapping.add(new ValuesMapper("addUpsellToBalance", dataMap));
    }

    public DailyMarginReportTestData() {
        this(false);
    }

    private DailyMarginReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("publishermargin/report");
        setSorting("dateTime", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
    }

    public DailyMarginReportTestData(String reportType) {
        this(true);
        this.reportType = reportType;
        super.setHeaders();
        // initial data
        this.buyerCategoriesMap = dataProvider.getPossibleValueFromJSON("BuyerCategories");
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
        this.buyerCategoriesList = DataHelper.getRandomListFromList(new ArrayList<>(buyerCategoriesMap.keySet()),
                DataHelper.getRandomInt(1, buyerCategoriesMap.size()));
        this.verticalList = DataHelper.getRandomListFromList(new ArrayList<>(verticalsMap.keySet()),
                DataHelper.getRandomInt(1, verticalsMap.size() > 10 ? 10 : verticalsMap.size()));
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
        // set hasTotalRow for items by reportType
        this.hasTotalRow = itemsByReportType.length() > 0 && (DataHelper.hasJSONValue(itemsByReportType.getJSONObject(0), "Total") ||
                DataHelper.hasJSONValue(itemsByReportType.getJSONObject(0), "Forecast"));
        // rows in table by publisher guid
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(publisher.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByPublisherGUID = dataProvider.getDataAsJSONArray(requestedURL);
        // items by publisher instance guid
        if (hasPublisherInstances()) {
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                    .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getGuid(), subID.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
        }
        // rows in table by vertical
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(verticalList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by buyer categories
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(BUYER_CATEGORIES_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(buyerCategoriesList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerCategories = dataProvider.getDataAsJSONArray(requestedURL);
        //  rows in table after all filters
        List<String> paramsKeys = new ArrayList<>(Arrays.asList(PUBLISHER_FILTER,
                VERTICAL_FILTER, BUYER_CATEGORIES_FILTER, "FromPeriod", "ToPeriod"));
        if (hasPublisherInstances()) paramsKeys.add(PUBLISHER_INSTANCE_FILTER);
        List<String> paramsValues = new ArrayList<>(Arrays.asList(publisher.getGuid(),
                StringUtils.join(verticalList, "|"), StringUtils.join(buyerCategoriesList, "|"), fromPeriod, toPeriod));
        if (hasPublisherInstances()) paramsValues.add(subID.getGuid());
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .filter(paramsKeys, paramsValues)
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new DailyMarginReportTestData.FiltersResetData();
        resetData.setFilterValuesMap();
    }

    // filter data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getSubID() {
        return subID;
    }

    public List<String> getVerticalList() {
        return verticalList;
    }

    public List<String> getBuyerCategoriesList() {
        return buyerCategoriesList;
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

    public JSONArray getItemsByPublisherGUID() {
        return itemsByPublisherGUID;
    }

    public JSONArray getItemsByPublisherInstanceGUID() {
        return itemsByPublisherInstanceGUID;
    }

    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByBuyerCategories() {
        return itemsByBuyerCategories;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getItemsByReportTypeCount() {
        return itemsByReportTypeCount;
    }

    // for tooltip verification
    public List<String> getTableBuyerCategories() {
        return buyerCategoriesList.stream().map(category ->
                category.equals("Data") | category.equals("Other") ? category : category + "s"
        ).collect(Collectors.toList());
    }

    public List<String> getAllTableBuyerCategories() {
        return buyerCategoriesMap.keySet().stream().map(category ->
                category.equals("Data") | category.equals("Other") ? category : category + "s"
        ).collect(Collectors.toList());
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
        this.hasTotalRow = itemsByReportType.length() > 0 && (DataHelper.hasJSONValue(itemsByReportType.getJSONObject(0), "Total") ||
                DataHelper.hasJSONValue(itemsByReportType.getJSONObject(0), "Forecast"));
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public boolean hasPublisherInstances() {
        return subIDs != null && !subIDs.isEmpty();
    }

    public boolean hasForecast() {
        return hasForecast(fromPeriodDate, toPeriodDate);
    }

    public boolean hasForecast(String fromPeriod, String toPeriod) {
        // there is no forecast for hourly
        if (reportType.equals("Hour")) return false;
        // String to Date
        Date fromPeriodDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, fromPeriod);
        Date toPeriodDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, toPeriod);
        // cause in url toPeriod 1 day further
        toPeriodDate = DataHelper.getDateByDaysOffset(toPeriodDate, -1);
        return hasForecast(fromPeriodDate, toPeriodDate);
    }

    /**
     * forecast row exists if date range inside current month
     * perhaps today is excluded
     *
     * @param fromPeriodDate - selected start date in calendar date range
     * @param toPeriodDate   - selected end date in calendar date range
     * @return whether or not forecast row exists in report table
     */
    private boolean hasForecast(Date fromPeriodDate, Date toPeriodDate) {
        // there is no forecast for hourly
        if (reportType.equals("Hour")) return false;
        Date monthEndDate = new Date();
        Date monthStartDate = DataHelper.setDay(monthEndDate, 1);
        boolean isToday = DataHelper.getDayInDate(fromPeriodDate) == DataHelper.getDayInDate(toPeriodDate) && // 1 day duration
                DataHelper.getDayInDate(monthEndDate) == DataHelper.getDayInDate(toPeriodDate);
        return (DataHelper.getMonthOffset(monthStartDate, fromPeriodDate) == 0 ||   // if start date is included into current month
                DataHelper.getMonthOffset(monthEndDate, toPeriodDate) == 0) &&      // if end date is included into current month
                !isToday;                                                           // but not today

    }

    // different reportType - different graphics set and default behaviour
    public boolean hasBubbleChart(String reportType) {
        return bubbleGraphics.contains(reportType);
    }

    @Override
    public boolean hasDataToCalculate(JSONArray jsonArray) {
        try {
            int startRow = hasTotalRow ? 1 : 0;
            startRow = hasForecast() ? ++startRow : startRow;
            for (int i = startRow; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                boolean hasData = true;
                for (String param : graphicParams) {
                    hasData &= row.has(param) && !Pattern.compile("0+").matcher(DataHelper.remainDigits(String.valueOf(row.get(param)))).matches();
                    if (!hasData) break;
                }
                if (hasData) return true;
            }
            // in case of total with 1 length
        } catch (IndexOutOfBoundsException | JSONException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean hasStackedDataToCalculate(JSONArray jsonArray) {
        int dataRows = 0;
        int startRow = hasTotalRow ? 1 : 0;
        startRow = hasForecast() ? ++startRow : startRow;
        if (jsonArray.length() < startRow) return false;
        for (int i = startRow; i < jsonArray.length(); i++) {
            JSONObject row = jsonArray.getJSONObject(i);
            boolean hasData = true;
            for (String param : graphicParams) {
                hasData &= row.has(param) && !Pattern.compile("0+").matcher(DataHelper.remainDigits(String.valueOf(row.get(param)))).matches();
                if (!hasData) break;
            }
            // if all graphicParams exists and not 0
            if (hasData) ++dataRows;
            // Stacked Area chart should be shown if there are at least 3 rows with data excluding total row
            if (dataRows >= 3) return true;
        }
        return false;
    }

    @Override
    public List<FieldFormatObject> getFields() {
        return isInstances && ((FiltersResetData) resetData).isChangeToDefaultFields() ? fieldsByDefaultReportType : fields;
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", REPORT_TYPE_MAP.get(reportType))
                .build().getRequestedURL();
    }

    @Override
    protected void setHeaders() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
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
        this.hasTotalRow = allRowsArray.length() > 0 && (DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total") ||
                DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Forecast"));
    }

    @Override
    public int getRowsPerPage(int rows) {
        return rows + (hasTotalRow ? 1 : 0) + (hasForecast() ? 1 : 0);
    }

    @Override
    public String toString() {
        String subIdDetails = isInstances && !subIDs.isEmpty() ? subID.toString() : "";
        String instanceDetails = isInstances ?
                "reportType=" + reportType +
                        ", publisher=" + publisher +
                        ", verticals=" + verticalList +
                        ", buyerCategories=" + buyerCategoriesList +
                        ", subID=" + subIdDetails +
                        ", subIDs=" + subIDs.stream().map(ObjectIdentityData::getId).collect(Collectors.joining(", "))
                : "";
        return super.toString() +
                "\nDailyMarginReportTestData{" +
                instanceDetails +
                '}';
    }

    class FiltersResetData extends AbstractFiltersResetData {
        FiltersResetData() {
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", REPORT_TYPE_MAP.get(DEFAULT_REPORT_TYPE))
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        boolean isChangeToDefaultFields() {
            return !filterValuesMap.containsKey(REPORT_TYPE_FILTER);
        }

        boolean isAllBuyerCategories() {
            return !filterValuesMap.containsKey(BUYER_CATEGORIES_FILTER);
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getGuid());
            if (hasPublisherInstances())
                filterValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getGuid());
            filterValuesMap.put(VERTICAL_FILTER, StringUtils.join(verticalList, "|"));
            filterValuesMap.put(BUYER_CATEGORIES_FILTER, StringUtils.join(buyerCategoriesList, "|"));
            filterValuesMap.put(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            if (hasPublisherInstances())
                filterLabelValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getId());
            filterLabelValuesMap.put(VERTICAL_FILTER, StringUtils.join(verticalList, "|"));
            filterLabelValuesMap.put(BUYER_CATEGORIES_FILTER, buyerCategoriesList.toString());
            filterLabelValuesMap.put(REPORT_TYPE_FILTER, reportType);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            if (resetIterationMap.size() > 1) {
                resetIterationMap.remove(REPORT_TYPE_FILTER);
            }
            // reset fields condition
            if (!resetIterationMap.containsKey(PUBLISHER_FILTER))
                FieldFormatObject.resetCondition(getFields());
            if (resetIterationMap.size() > 1)
                return itemsByFiltersCombination;
            else if (resetIterationMap.size() == 0)
                return allRowsArray;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case PUBLISHER_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByPublisherGUID;
                case PUBLISHER_INSTANCE_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByPublisherInstanceGUID;
                case VERTICAL_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByVertical;
                case BUYER_CATEGORIES_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByBuyerCategories;
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
                    .withAbsoluteURL(isChangeToDefaultFields() || filterReset.equals(REPORT_TYPE_FILTER) ? headersURL : _url)
                    .filter(keys, values)
                    .sort(_sortBy, _sortHow)
                    .build().getRequestedURL();
            this.itemsByFiltersCombination = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }
}