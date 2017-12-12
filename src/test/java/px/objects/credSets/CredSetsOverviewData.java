package px.objects.credSets;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import px.reports.ReportTestData;

import static pages.groups.MetaData.DATE_TIME_FORMAT;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 10/30/2017.
 */
public class CredSetsOverviewData extends ReportTestData {
    {
        missedHeadersMetricsMap.put("loginDate", DATE_TIME_FORMAT);
    }

    public CredSetsOverviewData() {
        setInstanceGroup("credsets");
        setSorting("credSetName", "asc");
        setAllRowsByDateRange();
        setHeaders();
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withEmptyFilter()
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

}
