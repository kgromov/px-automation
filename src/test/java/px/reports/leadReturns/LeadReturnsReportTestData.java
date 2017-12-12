package px.reports.leadReturns;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;

import java.util.*;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.*;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 9/4/2017.
 */
public class LeadReturnsReportTestData extends ReportTestData {
    // default data
    public static final String DEFAULT_REPORT_TYPE = "Pending";
    public final String DEFAULT_PERIOD_MONTH;
    // filters data
    private String reportType = DEFAULT_REPORT_TYPE;
    private String periodMonth;
    private String periodMonthLabel;
    private List<JSONObject> periods;
    private Map<String, String> map = new LinkedHashMap<>();
    private final List<String> periodMonths;
    // table data
    private JSONArray itemsByReportType;
    private JSONArray itemsByPeriodMonth;
    private int itemsByReportTypeCount;
    private int itemsByPeriodMonthCount;
    private int month;
    // filters
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String PERIOD_MONTH_FILTER = "periodMonth";
    // static data
    public static final List<String> REPORT_TYPES = Arrays.asList("Pending", "Accepted", "Declined");

    static {
        missedHeadersMetricsMap.put("payout", "Currency");
    }

    public LeadReturnsReportTestData() {
        super();
        setInstanceGroup("singleleadreturns");
        setSorting("buyerName", "asc");
        setHeaders();
        fields.forEach(field -> field.setIndex(field.getIndex() + 1));
      /*  Date temp = setDay(new Date(), 1);
        DEFAULT_PERIOD_MONTH = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, temp);*/
        // move to finalizedperiods
        String requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/finalizedperiods")
                .build().getRequestedURL();
        this.periods = JSONWrapper.toList(new JSONWrapper(dataProvider.getDataAsString(requestURL)).getJSONArray());
        periods.forEach(period ->
                map.put(String.valueOf(period.get("fromPeriod")), String.valueOf(period.get("title")))
        );
        this.periodMonths = new ArrayList<>(map.keySet());
        DEFAULT_PERIOD_MONTH = periodMonths.get(0);
        this.periodMonth = getRandomValueFromList(periodMonths);
        this.periodMonthLabel = map.get(periodMonth);
        // cause DEFAULT_PERIOD_MONTH could not be a constant - it's calculated
        setAllRowsByDateRange();
    }

    public LeadReturnsReportTestData(String reportType) {
        this();
        this.isInstances = true;
        // rows in table by report type
        withReportType(reportType);
        // rows in table by period month
//        this.month = DataHelper.getRandomInt(3);
    /*    this.month = DataHelper.getRandomInt(3);
        withMonth(-month);*/
        this.periodMonth = getRandomValueFromList(periodMonths);
        this.periodMonthLabel = map.get(periodMonth);
        withMonth(periodMonths.indexOf(periodMonth));
    }

    public LeadReturnsReportTestData withReportType(String reportType) {
        this.reportType = reportType;
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, reportType)
                .filter(PERIOD_MONTH_FILTER, DEFAULT_PERIOD_MONTH)
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = reportType.equals(DEFAULT_REPORT_TYPE) ? allRowsArray : dataProvider.getDataAsJSONArray(requestedURL);
        this.itemsByReportTypeCount = reportType.equals(DEFAULT_REPORT_TYPE) ? getItemsTotalCount() : dataProvider.getCurrentTotal();
        return this;
    }

    public LeadReturnsReportTestData withMonth(int monthOffset) {
       /* Date temp = DataHelper.getDateByMonthOffset(monthOffset);
        temp = DataHelper.setDay(temp, 1);
        this.periodMonth = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, temp);
        this.periodMonthLabel = DataHelper.getDateByFormatSimple(PERIOD_MONTH_PATTERN, temp);*/
        try {
            this.periodMonth = periodMonths.get(Math.abs(monthOffset));
            this.periodMonthLabel = map.get(periodMonth);
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams(REPORT_TYPE_FILTER, reportType)
                    .filter(PERIOD_MONTH_FILTER, periodMonth)
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPeriodMonth = periodMonth.equals(DEFAULT_PERIOD_MONTH) ? itemsByReportType : dataProvider.getDataAsJSONArray(requestedURL);
            this.itemsByPeriodMonthCount = periodMonth.equals(DEFAULT_PERIOD_MONTH) ? itemsByReportTypeCount : dataProvider.getCurrentTotal();
        } catch (IndexOutOfBoundsException e) {
            throw new TestDataException(String.format("Unable to get period month by index '%d'" +
                    "\t All available = %s", Math.abs(monthOffset), periodMonths));
        }
        return this;
    }

    public LeadReturnsReportTestData withBuyerID(String buyerID) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, reportType)
                .filter(Arrays.asList(PERIOD_MONTH_FILTER, "buyerId"),
                        Arrays.asList(periodMonth, buyerID))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        return this;
    }

    public LeadReturnsReportTestData withAttempts_() {
        List<Integer> months = new ArrayList<>(Arrays.asList(0, 1, 2));
        months.remove(month);
        while (!isAnyLeadReturnByStatus() && !months.isEmpty()) {
            this.month = months.get(getRandomInt(months.size()));
            months.remove(Integer.valueOf(month));
            withMonth(month);
        }
        return this;
    }

    public LeadReturnsReportTestData withAttempts() {
        List<String> reiterations = new ArrayList<>(periodMonths);
        String tempMonth = getRandomValueFromList(reiterations);
        reiterations.remove(month);
        while (!isAnyLeadReturnByStatus() && !reiterations.isEmpty()) {
            int month = getRandomInt(reiterations.size());
            reiterations.remove(reiterations.get(month));
            withMonth(month);
        }
        return this;
    }


    public Set<JSONObject> forMultipleReturns() {
        if (!isAnyLeadReturnByStatus())
            throw new TestDataException(String.format("No returned leads in last 3 months with status %s", reportType));
        Map<Integer, JSONObject> map = new TreeMap<>();
        int count = DataHelper.getRandomInt(1, itemsByPeriodMonthCount > 5 ? 5 : itemsByPeriodMonthCount);
        for (int i = 0; i < count; i++) {
            int index = getRandomInt(itemsByPeriodMonthCount);
            map.put(index, itemsByPeriodMonth.getJSONObject(index));
        }
        return map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    // filter data
    public String getReportType() {
        return reportType;
    }

    public String getPeriodMonth() {
        return periodMonth;
    }

    public String getPeriodMonthLabel() {
        return periodMonthLabel;
    }

    // table data
    public JSONArray getItemsByReportType() {
        return itemsByReportType;
    }

    public JSONArray getItemsByPeriodMonth() {
        return itemsByPeriodMonth;
    }

    public int getItemsByReportTypeCount() {
        return itemsByReportTypeCount;
    }

    public int getItemsByPeriodMonthCount() {
        return itemsByPeriodMonthCount;
    }

    public boolean isAnyLeadReturnByStatus() {
        return itemsByPeriodMonthCount > 0;
    }

    public boolean isAnyLeadReturnedByCampaign(String campaignID) {
        return getListFromJSONArrayByKey(itemsByPeriodMonth, "buyerId").contains(campaignID);
    }

    public boolean isAnyLeadReturnedByBuyer(Set<String> campaignIDs) {
        return getListFromJSONArrayByKey(itemsByPeriodMonth, "buyerId").stream().map(campaignIDs::contains).findFirst().orElse(false);
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", reportType)
                .build().getRequestedURL();
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, DEFAULT_REPORT_TYPE)
                .filter(PERIOD_MONTH_FILTER, DEFAULT_PERIOD_MONTH)
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
        return "LeadReturnsReportTestData{" +
                "reportType='" + reportType + '\'' +
                ", periodMonth='" + periodMonth + '\'' +
                ", periodMonthLabel='" + periodMonthLabel + '\'' +
                ", itemsByReportTypeCount=" + itemsByReportTypeCount +
                ", itemsByPeriodMonthCount=" + itemsByPeriodMonthCount +
                '}';
    }
}