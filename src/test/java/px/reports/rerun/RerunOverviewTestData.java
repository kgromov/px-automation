package px.reports.rerun;

import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import px.reports.ReportTestData;

import static pages.groups.MetaData.DATE_TIME_FORMAT;

/**
 * Created by konstantin on 05.10.2017.
 */
// TODO: -> later on implement MetaData
public class RerunOverviewTestData extends ReportTestData {

    static {
        missedHeadersMetricsMap.put("filterFromDate", DATE_TIME_FORMAT);
        missedHeadersMetricsMap.put("filterToDate", DATE_TIME_FORMAT);
        missedHeadersMetricsMap.put("creationDate", DATE_TIME_FORMAT);
        // mapping
        dataMap.put("0", "No");
        dataMap.put("1", "Yes");
        dataMap.put("false", "No");
        dataMap.put("true", "Yes");
        dataMap.put("", "Once");  // ?
        dataMapping.add(new ValuesMapper("allowReposted", dataMap));
        dataMapping.add(new ValuesMapper("ignoreRerunStatus", dataMap));
        dataMapping.add(new ValuesMapper("runCycle", dataMap));
    }

    public RerunOverviewTestData() {
        // set fields
        String requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/rerunplannedtasks")
                .build().getRequestedURL();
        this.fields = getFields(requestURL);
        // users in table
        requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/rerunplannedtasks")
                .withEmptyFilter()
                .sort("rerunFilterName", "asc")
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestURL);
        // update with days
        JSONWrapper.toList(allRowsArray).stream().filter(row ->
                row.has("leadAgeMin") && row.has("leadAgeMax")).forEach(row -> {
            row.put("leadAgeMin", String.valueOf(row.get("leadAgeMin")) + " day(s)");
            row.put("leadAgeMax", String.valueOf(row.get("leadAgeMax")) + " day(s)");
        });
    }
}