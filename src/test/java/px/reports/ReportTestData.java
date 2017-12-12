package px.reports;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pages.groups.PaginationStrategy;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.ReportDataMapping;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static config.Config.daysRange;
import static config.Config.startDate;
import static configuration.helpers.DataHelper.getDateByDaysOffset;
import static configuration.helpers.DataHelper.getRandomInt;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 11/17/2016.
 */
public abstract class ReportTestData implements PaginationStrategy {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected LxpDataProvider dataProvider;
    protected String instanceGroup;
    protected boolean isInstances;
    protected JSONArray allRowsArray;
    protected int totalCount;
    protected boolean isBigData;
    protected boolean hasTotalRow;
    protected boolean hasGraphics;
    // sorting
    protected String sortBy;
    protected String sortHow;
    // Date ranges
    protected int startMonthOffset = 3;
    protected int durationDays = 30;
    protected String fromPeriod;
    protected String toPeriod;
    protected Date fromPeriodDate;
    protected Date toPeriodDate;
    // headers
    protected String headersURL;
    protected JSONArray headers;
    protected List<String> headersList;
    protected List<FieldFormatObject> fields;
    // metricType unfortunately is used only for graphic parameters
    protected static Map<String, String> missedHeadersMetricsMap = new HashMap<>();
    // generic data mapping
    protected static List<ValuesMapper> dataMapping = new ArrayList<>();
    protected static Map<String, String> dataMap = new HashMap<>();
    // tooltip mapping
    protected ReportDataMapping mapping = new ReportDataMapping();
    // popup title to field name mapping
    protected static Map<String, String> popupTitleMap = new HashMap<>();
    // set of graphic parameters
    protected Set<String> graphicParams;
    // static data
    public static final Map<String, String> REPORT_TYPE_MAP = new HashMap<>();
    public static Set<String> TRANSACTIONS_SET = new HashSet<>();
    public static final Set<String> FORMULA_FIELDS = new HashSet<>(Arrays.asList("marginPerc", "netMarginPerc", "rpls", "rpl"));

    static {
        REPORT_TYPE_MAP.put("Hour", "hourly");
        REPORT_TYPE_MAP.put("Day", "daily");
        REPORT_TYPE_MAP.put("Week", "weekly");
    }

    protected ReportTestData() {
        this(false);
    }

    public ReportTestData(boolean isInstances) {
        this.isInstances = isInstances;
        this.dataProvider = new LxpDataProvider();
    }

    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    public void setDateRanges(String url) {
        this.fromPeriod = DataHelper.getParameterJSONFromURL(url, "filter", "FromPeriod");
        this.toPeriod = DataHelper.getParameterJSONFromURL(url, "filter", "ToPeriod");
    }

    // set headers
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .build().getRequestedURL();
    }

    protected void setHeaders() {
        setHeadersURL();
        this.headers = new JSONArray(dataProvider.getDataAsString(headersURL, HttpMethodsEnum.OPTIONS));
        this.headersList = DataHelper.getListFromJSONArrayByKey(headers, "field");
        // common
        setFields();
    }

    protected void setFields() {
        this.fields = getFields(headersURL);
    }

    protected List<FieldFormatObject> getFields(String metaDataURL) {
        this.headers = new JSONArray(dataProvider.getDataAsString(metaDataURL, HttpMethodsEnum.OPTIONS));
        this.headersList = DataHelper.getListFromJSONArrayByKey(headers, "field");
        List<FieldFormatObject> fields = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            FieldFormatObject field = new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i);
            // set popup title according to mapping
            if (field.hasPopup()) field.getPopupData().setTitle(popupTitleMap.get(field.getName()));
            // set mapping
            if (ValuesMapper.hasMappedValues(dataMapping, field.getName()))
                field.setValuesMap(ValuesMapper.getMappedValues(dataMapping, field.getName()).getMap());
            // tooltip mapping
            field.withMapping(mapping = new ReportDataMapping(instanceGroup));
            fields.add(field);
        }
        return fields;
    }

    /**
     * Method set date range for reports
     * StartDate could be:
     * 1) Set by parameter from Jenkins via System property.
     * Has the highest priority
     * 2) Set by default month offset
     * Each test Date might have own month offset. In that case if start Date before date by month offset
     * Date will be decreased according to month this specific offset
     * 3) For base environment Date could be hardcoded, e.g. fot new staging environment - 2017-02-03
     * EndDate could be any Date in range [0; 30] according to StartDate but before or equals now Date
     */

    protected void setDateRanges() {
        // start date is limited and different on different environments
        Date startConfigDate = startDate != null ? startDate
                : Config.isNewStagingEnvironment()
                ? DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, "2017-02-03")
                : DataHelper.setDay(DataHelper.getDateByMonthOffset(-startMonthOffset), 1);
        // set maximum days range
        durationDays = daysRange > 0 ? daysRange : durationDays;
        int startMonthOffset = DataHelper.getMonthOffset(startConfigDate);
        // if startMonthOffset = month of start date - prevent day before
        int startConfigDay = DataHelper.getDayInDate(startConfigDate);
        // validation if start config sate is too old
        if (startMonthOffset > this.startMonthOffset) {
            startMonthOffset = this.startMonthOffset;
            startConfigDay = 1;
        }
        // logic to calculate data ranges
        int month = getRandomInt(-startMonthOffset, 0);
        int leastAvailableDay = startMonthOffset == Math.abs(month) ? startConfigDay : 1;
        int day = getRandomInt(leastAvailableDay, DataHelper.getLastActualDayOfMonth(month));
        // if current month - day offset could not be positive - date in future
        day = month == 0 ? getRandomInt(-DataHelper.getLastActualDayOfMonth(month) + leastAvailableDay, 0) : day;
        fromPeriodDate = DataHelper.getDateByOffset(month, day);
        // to period
        month = getRandomInt(-startMonthOffset, 0);
        day = getRandomInt(leastAvailableDay, DataHelper.getLastActualDayOfMonth(month));
        // if current month day offset could not be positive - date in future
        day = month == 0 ? getRandomInt(-DataHelper.getLastActualDayOfMonth(month) + leastAvailableDay, 0) : day;
        toPeriodDate = DataHelper.getDateByOffset(month, day);
        // which date is later
        if (toPeriodDate.getTime() < fromPeriodDate.getTime()) {
            Date temp = fromPeriodDate;
            fromPeriodDate = toPeriodDate;
            toPeriodDate = temp;
        }
        log.info(String.format("Initial date range [%s-%s]", fromPeriodDate, toPeriodDate));
        // if there less than 1 day
        long daysDiff = TimeUnit.DAYS.convert(toPeriodDate.getTime() - fromPeriodDate.getTime(), TimeUnit.MILLISECONDS);
        // increase if less than 1 day difference
        if (daysDiff < 1) {
            toPeriodDate = getDateByDaysOffset(toPeriodDate, 1);
            ++daysDiff;
            log.info(String.format("After increasing by 1 day 'toPeriodDate'\tRange [%s-%s]", fromPeriodDate, toPeriodDate));
        }
        // decrease if more than 30 days
        if (daysDiff > durationDays) {
//            int daysOffset = (int) (daysDiff - daysDiff % 30 + 1);
            int daysOffset = 0;
            while (daysDiff - daysOffset > durationDays) {
                daysOffset += durationDays;
            }
            log.info(String.format("After decreasing by '%d' days 'toPeriodDate', daysDiff='%d'\nRange: [%s-%s]", daysOffset, daysDiff, fromPeriodDate, toPeriodDate));
            toPeriodDate = getDateByDaysOffset(toPeriodDate, -daysOffset);
        }
/*        // DEBUG WITH SPECIFIC VALUES
        fromPeriodDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, "2017-02-24");
        toPeriodDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, "2017-02-25");*/
        // finally parse to format
        this.fromPeriod = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, fromPeriodDate);
        this.toPeriod = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, toPeriodDate);
        // decrease date cause of lxp logic with future day
        toPeriodDate = getDateByDaysOffset(toPeriodDate, -1);
    }

    protected void setDateRanges2() {
        Date commonStartDate = getDateByDaysOffset(90);
        // start date is limited and different on different environments
        Date startConfigDate = startDate != null ? (startDate.getTime() < commonStartDate.getTime() ? startDate : commonStartDate) : commonStartDate;
        // if less than 90 days by jenkins parameter
        int maxDaysOffset = (int) TimeUnit.DAYS.convert(new Date().getTime() - startConfigDate.getTime(), TimeUnit.MILLISECONDS);
        // set maximum days range
        durationDays = daysRange > 0 ? daysRange : durationDays;
        // random day offset
        int daysOffset = getRandomInt(maxDaysOffset);
        fromPeriodDate = getDateByDaysOffset(daysOffset);
        // if current month day offset could not be positive - date in future
        daysOffset = getRandomInt(maxDaysOffset);
        toPeriodDate = getDateByDaysOffset(daysOffset);
        // which date is later
        if (toPeriodDate.getTime() < fromPeriodDate.getTime()) {
            Date temp = fromPeriodDate;
            fromPeriodDate = toPeriodDate;
            toPeriodDate = temp;
        }
        log.info(String.format("Initial date range [%s-%s]", fromPeriodDate, toPeriodDate));
        // if there less than 1 day
        long daysDiff = TimeUnit.DAYS.convert(toPeriodDate.getTime() - fromPeriodDate.getTime(), TimeUnit.MILLISECONDS);
        // increase if less than 1 day difference
        if (daysDiff < 1) {
            toPeriodDate = getDateByDaysOffset(toPeriodDate, 1);
            ++daysDiff;
            log.info(String.format("After increasing by 1 day 'toPeriodDate'\tRange [%s-%s]", fromPeriodDate, toPeriodDate));
        }
        // decrease if more than 30 days
        if (daysDiff > durationDays) {
//            int daysOffset = (int) (daysDiff - daysDiff % 30 + 1);
            daysOffset = 0;
            while (daysDiff - daysOffset > durationDays) {
                daysOffset += durationDays;
            }
            log.info(String.format("After decreasing by '%d' days 'toPeriodDate', daysDiff='%d'\nRange: [%s-%s]", daysOffset, daysDiff, fromPeriodDate, toPeriodDate));
            toPeriodDate = getDateByDaysOffset(toPeriodDate, -daysOffset);
        }
/*        // DEBUG WITH SPECIFIC VALUES
        fromPeriodDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, "2017-02-24");
        toPeriodDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, "2017-02-25");*/
        // finally parse to format
        this.fromPeriod = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, fromPeriodDate);
        this.toPeriod = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, toPeriodDate);
        // decrease date cause of lxp logic with future day
        toPeriodDate = getDateByDaysOffset(toPeriodDate, -1);
    }

    protected void validateDateRangeBigData() {
        // in big data reports end date should in the past
        long daysDiff = TimeUnit.DAYS.convert(new Date().getTime() - toPeriodDate.getTime(), TimeUnit.MILLISECONDS);
        if (daysDiff < 1) {
            // if date have been equals
            if (fromPeriodDate.getTime() == toPeriodDate.getTime()) {
                log.info("Decrease start date of calendar date range for big data");
                fromPeriodDate = getDateByDaysOffset(fromPeriodDate, -1);
            }
            log.info("Decrease end date of calendar date range for big data");
            toPeriodDate = getDateByDaysOffset(toPeriodDate, -1);
            this.fromPeriod = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, fromPeriodDate);
            this.toPeriod = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, toPeriodDate);
            toPeriodDate = getDateByDaysOffset(toPeriodDate, -1);
            log.info(String.format("After decreasing by 1 day 'toPeriodDate'\nRange: [%s-%s]", fromPeriodDate, toPeriodDate));
        }
    }

    public String getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(String instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    public JSONArray getAllRowsArray() {
        return allRowsArray;
    }

    public int getItemsTotalCount() {
//        return isBigData ? totalCount : allRowsArray.length();
        return totalCount;
    }

    public int getItemsCurrentTotalCount() {
        return dataProvider.getCurrentTotal();
    }

    public String getFromPeriod() {
        return fromPeriod;
    }

    public String getToPeriod() {
        return toPeriod;
    }

    public Date getFromPeriodDate() {
        return fromPeriodDate;
    }

    public Date getToPeriodDate() {
        return toPeriodDate;
    }

    public JSONArray getHeaders() {
        return headers;
    }

    public List<FieldFormatObject> getFields() {
        return fields;
    }

    protected void setSorting(String sortBy, String sortHow) {
        this.sortBy = sortBy;
        this.sortHow = sortHow;
    }

    public boolean isInstances() {
        return isInstances;
    }

    public boolean isBigData() {
        return isBigData;
    }

    public boolean hasTotalRow() {
        return hasTotalRow;
    }

    public boolean hasGraphics() {
        return hasGraphics;
    }

    public List<String> filters() {
        throw new UnsupportedOperationException("Implementation required in class " + this.getClass().getName());
    }

    // ------------------------ Graphics ------------------------
    public boolean hasDataToCalculate(JSONArray jsonArray) {
        try {
            for (int i = hasTotalRow ? 1 : 0; i < jsonArray.length(); i++) {
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

    // should be at least 2 records excluding total
    public boolean hasStackedDataToCalculate(JSONArray jsonArray) {
        int dataRows = 0;
        int startRow = hasTotalRow ? 1 : 0;
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

    public String getRangeURL() {
        StringBuilder builder = new StringBuilder("?query=");
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectNested = new JSONObject();
        jsonObjectNested.put("FromPeriod", fromPeriod);
        jsonObjectNested.put("ToPeriod", toPeriod);
        jsonObject.put("filter", jsonObjectNested);
        jsonObjectNested = new JSONObject();
        jsonObjectNested.put("qiqScore", "desc");
        jsonObject.put("sorting", jsonObjectNested);
        builder.append(jsonObject.toString());
        return builder.toString();
    }

    public Set<String> graphicParams() {
        return graphicParams;
    }

    public void setBubbleChartGraphicParams(String url) {
        this.graphicParams = new HashSet<>(DataHelper.getValuesJSONFromURL(url, "chart", "b1", "b2", "b3"));
    }

    public void setColumnChartGraphicParams(String url) {
        this.graphicParams = new HashSet<>(DataHelper.getValuesJSONFromURL(url, "chart", "c2")); //"c1",
    }

    public void setStackedAreaChartGraphicParams(String url) {
        this.graphicParams = new HashSet<>(DataHelper.getValuesJSONFromURL(url, "chart", "sa1", "sa2"));
    }

    // -------- Getting data from response without filters --------

    /**
     * @param rowsArray - report table rows in form of JSON array
     * @param dataKey   - key in JSON which represents seeking object, e.g. 'vertical'
     * @return filter value as string that present in report's table
     */

    protected String getFilterValue(JSONArray rowsArray, String dataKey) {
        Set<String> usedItems = new HashSet<>(DataHelper.getListFromJSONArrayByKey(rowsArray, dataKey));
        usedItems.remove("Total");
        usedItems.remove("");
        log.info(String.format("DEBUG:\tData values by key '%s' in report table = %s", dataKey, usedItems));
        return DataHelper.getRandomValueFromList(new ArrayList<>(usedItems));
    }

    /**
     * @param rowsArray - report table rows in form of JSON array
     * @param dataKey   - key in JSON which represents seeking object, e.g. 'vertical'
     * @return filter values as list of string random size [1..10] with values that present in report's table
     */
    protected List<String> getFilterValues(JSONArray rowsArray, String dataKey) {
        Set<String> usedItems = new HashSet<>(DataHelper.getListFromJSONArrayByKey(rowsArray, dataKey));
        usedItems.remove("Total");
        usedItems.remove("");
        log.info(String.format("DEBUG:\tData values by key '%s' in report table = %s", dataKey, usedItems));
        int count = getRandomInt(1, usedItems.size() > 10 ? 10 : usedItems.size());
        return DataHelper.getRandomListFromList(new ArrayList<>(usedItems), count);
    }

    /**
     * Method is used to get object that would be used as filter in report
     * Cause there are a lot of cases when empty table is checked after filter is set
     * So method return any object that mentioned in JSON array response (report's table rows)
     * <p>Possible exceptions:</p>
     * # IndexOutOfBoundsException if there are no items by key or rowsArray is empty
     * #
     *
     * @param objects     - list of objects identifiers {name, id, guid}
     * @param rowsArray   - report table rows in form of JSON array
     * @param dataKey     - key in JSON which represents seeking object, e.g. 'offerId'
     * @param objectIdKey - object identity key - ant of {name, id, guid} to get object from objects list
     * @return ObjectIdentity that present in objects list and report's table
     */
    protected ObjectIdentityData getFilterObject(List<ObjectIdentityData> objects, JSONArray rowsArray, String dataKey, String objectIdKey) {
        Set<String> usedItems = new HashSet<>(DataHelper.getListFromJSONArrayByKey(rowsArray, dataKey));
        usedItems.remove("Total");
        usedItems.remove("");
        log.info(String.format("DEBUG:\tData values by key '%s' in report table = %s", dataKey, usedItems));
        List<String> missedNames = usedItems.stream().filter(name -> !ObjectIdentityData.getAllNames(objects)
                .contains(name)).collect(Collectors.toList());
        log.info(String.format("DEBUG:\tMissed objects (in all objects) by objectIdKey '%s' " +
                "are in report table = %s", objectIdKey, missedNames));
        try {
            String objectID = DataHelper.getRandomValueFromList(new ArrayList<>(usedItems));
            return getObjectByIdentifier(objects, objectIdKey, objectID);
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return ObjectIdentityData.getAnyObjectFromList(objects);
        }
    }

    /**
     * Method is used to get object that would be used as filter in report
     * Cause there are a lot of cases when empty table is checked after filter is set
     * So method return random number of object (but no more than 10) that mentioned in JSON array response (report's table rows)
     *
     * @param objects     - list of objects identifiers {name, id, guid}
     * @param rowsArray   - report table rows in form of JSON array
     * @param dataKey     - key in JSON which represents seeking object, e.g. 'offerId'
     * @param objectIdKey - object identity key - ant of {name, id, guid} to get object from objects list
     * @return ObjectIdentity list that present in objects list and report's table
     */
    protected List<ObjectIdentityData> getFilterObjects(List<ObjectIdentityData> objects, JSONArray rowsArray, String dataKey, String objectIdKey) {
        List<String> usedItems = new ArrayList<>(new HashSet<>(DataHelper.getListFromJSONArrayByKey(rowsArray, dataKey)));
        usedItems.remove("Total");
        usedItems.remove("");
        log.info(String.format("DEBUG:\tData values by key '%s' in report table = %s", dataKey, usedItems));
        List<String> missedNames = usedItems.stream().filter(name -> !ObjectIdentityData.getAllNames(objects)
                .contains(name)).collect(Collectors.toList());
        log.info(String.format("DEBUG:\tMissed objects (in all objects) by objectIdKey '%s' " +
                "are in report table = %s", objectIdKey, missedNames));
        int count = getRandomInt(1, usedItems.size() > 10 ? 10 : usedItems.size());
        Set<ObjectIdentityData> temp = new HashSet<>();
        for (int i = 0; i < count; i++) {
            temp.add(getObjectByIdentifier(objects, objectIdKey, DataHelper.getRandomValueFromList(usedItems)));
        }
        return new ArrayList<>(temp);
    }

    /**
     * @param objects       - list of objects identifiers {name, id, guid}
     * @param objectIdKey   - object identity key - ant of {name, id, guid} to get object from objects list
     * @param objectIdValue - value by objectIdKey
     * @return ObjectIdentity object that matches objectIdValue by objectIdKey or any of objects if not
     */
    private ObjectIdentityData getObjectByIdentifier(List<ObjectIdentityData> objects, String objectIdKey, String objectIdValue) {
        ObjectIdentityData object;
        switch (objectIdKey) {
            case "name":
                object = ObjectIdentityData.getObjectFromListByName(objects, objectIdValue);
                break;
            case "guid":
                object = ObjectIdentityData.getObjectFromListByGUID(objects, objectIdValue);
                break;
            default:
                object = ObjectIdentityData.getObjectFromListByID(objects, objectIdValue);
                break;
        }
        return object == null ? ObjectIdentityData.getAnyObjectFromList(objects) : object;
    }

    @Override
    public int getRowsPerPage(int rows) {
        return rows + (hasTotalRow ? 1 : 0);
    }

    @Override
    public String toString() {
        return "ReportTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
                '}';
    }
}