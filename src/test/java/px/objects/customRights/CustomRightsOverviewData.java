package px.objects.customRights;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import px.reports.ReportTestData;

import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 11/8/2017.
 */
public class CustomRightsOverviewData extends ReportTestData {

    public CustomRightsOverviewData() {
        setInstanceGroup("customrights");
        setSorting("rightName", "asc");
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
