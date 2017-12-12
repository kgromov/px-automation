package px.reports.rerun;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import px.reports.ReportTestData;

import java.util.Arrays;

import static pages.groups.MetaData.*;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 10/9/2017.
 */
public class RerunedLeadsReportTestData extends ReportTestData {
    private String ageMin = "";
    private String ageMax = "";
    private ObjectIdentityData filter;

    static {
        missedHeadersMetricsMap.put("creationDate", DATE_TIME_FORMAT);
        missedHeadersMetricsMap.put("payout", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("qiqScore", PERCENTAGE_FORMAT);
     /*   // mapping
        dataMap.put("43", "Not Sold");
        dataMapping.add(new ValuesMapper("", dataMap));*/
    }

    public RerunedLeadsReportTestData(RerunTaskTestData rerunData) {
        this.ageMin = rerunData.getAgeMin();
        this.ageMax = rerunData.getAgeMax();
        this.filter = rerunData.getFilter();
        setInstanceGroup("rerunplanningtask/leads");
        setSorting("dateTime", "desc");
        setHeaders();
        setAllRowsByDateRange();
        fields.forEach(field -> field.setIndex(field.getIndex() + 1));
    }

    public RerunedLeadsReportTestData(ObjectIdentityData filter) {
        this.filter = filter;
        setInstanceGroup("rerunplanningtask/leads");
        setSorting("dateTime", "desc");
        setHeaders();
        setAllRowsByDateRange();
        fields.forEach(field -> field.setIndex(field.getIndex() + 1));
    }

    public String getAgeMin() {
        return ageMin;
    }

    public String getAgeMax() {
        return ageMax;
    }

    public ObjectIdentityData getFilter() {
        return filter;
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList("leadagemin", "leadagemax", "filterguid"),
                        Arrays.asList(ageMin, ageMax, filter.getGuid()))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    @Override
    public String toString() {
        return "RerunedLeadsReportTestData{" +
                "ageMin='" + ageMin + '\'' +
                ", ageMax='" + ageMax + '\'' +
                ", filter=" + filter +
                '}';
    }


}
