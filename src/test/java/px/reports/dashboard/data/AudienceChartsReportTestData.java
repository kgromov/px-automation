package px.reports.dashboard.data;

import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import px.reports.ReportTestData;

import java.util.Arrays;
import java.util.Map;

import static configuration.helpers.DataHelper.getKeyByValue;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;
import static px.reports.audience.AudienceReportTestData.getReportTypeMap;

/**
 * Created by kgr on 8/25/2017.
 */
public class AudienceChartsReportTestData extends ReportTestData {
    private JSONArray itemsByGender;
    private JSONArray itemsByAgeGroup;
    private JSONArray itemsByState;
    // filter constants
    public static final String REPORT_TYPE_FILTER = "reportType";
    public static final String GENDER_TYPE = "gender";
    public static final String AGE_GROUP_TYPE = "ageGroup";
    public static final String STATE_TYPE = "state";
    // links verification
    public static final String REPORT_LINK = "reports/audience";
    // audience report type map
    public static final Map<String, String> reportTypesMap = getReportTypeMap(DEFAULT_VERTICAL);

    public AudienceChartsReportTestData() {
        setInstanceGroup("audience/report");
        setSorting("leads", "desc");
        setDateRanges();
    }

    public AudienceChartsReportTestData withAgeGroups() {
        // items by age group
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, AGE_GROUP_TYPE)
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "Viewmode"),
                        Arrays.asList(fromPeriod, toPeriod, "short"))
                .sort(sortBy, sortHow)
                .withCount(10)
                .build().getRequestedURL();
        this.itemsByAgeGroup = dataProvider.getDataAsJSONArray(requestedURL);
        return this;
    }

    public AudienceChartsReportTestData withGenders() {
        // items by gender
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, GENDER_TYPE)
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "Viewmode"),
                        Arrays.asList(fromPeriod, toPeriod, "short"))
                .sort(sortBy, sortHow)
                .withCount(10)
                .build().getRequestedURL();
        this.itemsByGender = dataProvider.getDataAsJSONArray(requestedURL);
        return this;
    }

    public AudienceChartsReportTestData withStates() {
        // items by states
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, STATE_TYPE)
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "Viewmode"),
                        Arrays.asList(fromPeriod, toPeriod, "all"))
                .sort(sortBy, sortHow)
                .withCount(5)
                .build().getRequestedURL();
        this.itemsByState = dataProvider.getDataAsJSONArray(requestedURL);
        return this;
    }

    public JSONArray getItemsByGender() {
        return itemsByGender;
    }

    public JSONArray getItemsByAgeGroup() {
        return itemsByAgeGroup;
    }

    public JSONArray getItemsByState() {
        return itemsByState;
    }

    // to check links
    public String getAgeGroupLink() {
        return new RequestedURL.Builder()
                .withRelativeURL(REPORT_LINK)
                .withParams("query", getQuery(AGE_GROUP_TYPE))
                .build().getRequestedURL();
    }

    public String getGenderLink() {
        return new RequestedURL.Builder()
                .withRelativeURL(REPORT_LINK)
                .withParams("query", getQuery(GENDER_TYPE))
                .build().getRequestedURL();
    }

    public String getStateLink() {
        return new RequestedURL.Builder()
                .withRelativeURL(REPORT_LINK)
                .withParams("query", getQuery(STATE_TYPE))
                .build().getRequestedURL();
    }

    private String getQuery(String reportType) {
        /*
        JSONObject group = new JSONObject();
        group.put("ReportType", reportType);
        JSONObject filter = new JSONObject();
        filter.put("FromPeriod", fromPeriod);
        filter.put("ToPeriod", toPeriod);*/
        String group = String.format("{\"ReportType\":\"%s\"}", reportType);
        String filter = String.format("{\"FromPeriod\":\"%s\",\"ToPeriod\":\"%s\"}", fromPeriod, toPeriod);
        return String.format("{\"group\":%s,\"filter\":%s}", group, filter);
    }

    public static String getReportTypeFilter(String reportType) {
        return getKeyByValue(reportTypesMap, reportType);
    }
}
