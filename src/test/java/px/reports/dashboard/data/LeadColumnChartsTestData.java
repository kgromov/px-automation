package px.reports.dashboard.data;

import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.dto.ChartMetaData;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kgr on 8/25/2017.
 */
public class LeadColumnChartsTestData extends ReportTestData {
    private JSONArray leadData;
    private JSONArray conversionData;
    // static data for links verification
    public static final String LEADS_LINK = "leads";
    public static final String CONVERSIONS_LINK = "reports/publisherconversions";

    /* breakdown interval according to [from; to] days diff; max = 48
     * <=2 - hour;
     * [2; 44] - day
     * > 44 - week
     */
    public LeadColumnChartsTestData() {
        setDateRanges();
    }

    public LeadColumnChartsTestData withLeads() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/breakdownperformance/leads")
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "breakdownInterval"),
                        Arrays.asList(fromPeriod, toPeriod, getBreakDownInterval()))
                .build().getRequestedURL();
        this.leadData = dataProvider.getDataAsJSONArray(requestedURL);
        return this;
    }

    public LeadColumnChartsTestData withConversions() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/breakdownperformance/conversions")
                .filter(Arrays.asList("FromPeriod", "ToPeriod", "breakdownInterval"),
                        Arrays.asList(fromPeriod, toPeriod, getBreakDownInterval()))
                .build().getRequestedURL();
        this.conversionData = dataProvider.getDataAsJSONArray(requestedURL);
        return this;
    }

    // chart specific
    public void updateDateTimePattern(List<ChartMetaData> chartFields) {
        if (getBreakDownInterval().equals("hour")) {
            ChartMetaData dateTimeField = ChartMetaData.getFieldObjectFromListByName(chartFields, "dateTime");
            dateTimeField.setInPattern(dateTimeField.getInPattern() + " HH:mm");
            dateTimeField.setOutPattern(dateTimeField.getOutPattern() + " HH:mm");
        }

    }

    private String getBreakDownInterval() {
        long hoursDiff = TimeUnit.HOURS.convert(toPeriodDate.getTime() - fromPeriodDate.getTime(), TimeUnit.MILLISECONDS);
        long daysDiff = TimeUnit.DAYS.convert(toPeriodDate.getTime() - fromPeriodDate.getTime(), TimeUnit.MILLISECONDS);
        log.info(String.format("Date range: [%s; %s], daysDiff = %d, hoursDiff = %d", fromPeriod, toPeriod, daysDiff, hoursDiff));
        return daysDiff >= 2 && hoursDiff > 0 ? (daysDiff > 44 ? "week" : "day") : "hour";
    }

    public JSONArray getLeadData() {
        return leadData;
    }

    public JSONArray getConversionData() {
        return conversionData;
    }
}
