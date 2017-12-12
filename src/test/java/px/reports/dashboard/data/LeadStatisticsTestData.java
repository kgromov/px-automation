package px.reports.dashboard.data;

import configuration.helpers.DashboardRequestedURL;
import configuration.helpers.JSONWrapper;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.DateRange;

import java.util.Arrays;
import java.util.Date;

import static configuration.helpers.DataHelper.getDateByFormatSimple;

/**
 * Created by kgr on 8/25/2017.
 */
public class LeadStatisticsTestData extends ReportTestData {
    {
        startMonthOffset = 0;
        durationDays = 1;
    }

    private JSONObject leadsStatistics;
    private JSONArray leadsNames;
    private int leadsNamesCount;

    public LeadStatisticsTestData() {
        setDateRanges();
    }

    public LeadStatisticsTestData withLeadsStatistics() {
        String time = getDateByFormatSimple("HH:mm");
        String requestedURL = new DashboardRequestedURL.Builder()
                .withRelativeURL("api/leadstotalreport")
                .withParams("cacheSeed", time)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .build().getRequestedURL();
        this.leadsStatistics = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSON();
        return this;
    }

    public LeadStatisticsTestData withLeadsNames() {
        DateRange dateRange = new DateRange(new Date());
        String requestedURL = new DashboardRequestedURL.Builder()
                .withRelativeURL("api/shortleaddetailsfastreport")
                .filter(Arrays.asList("FromPeriod", "ToPeriod"),
                        Arrays.asList(dateRange.getFromPeriod(), dateRange.getToPeriod()))
                .sort("CreationDate", "DESC")
                .withCount(30)
                .withPage(1)
                .build().getRequestedURL();
        this.leadsNames = dataProvider.getDataAsJSONArray(requestedURL);
        this.leadsNamesCount = dataProvider.getCurrentTotal();
        return this;
    }

    public JSONObject getLeadsStatistics() {
        return leadsStatistics;
    }

    public JSONArray getLeadsNames() {
        return leadsNames;
    }

    public int getLeadsNamesCount() {
        return leadsNamesCount;
    }
}
