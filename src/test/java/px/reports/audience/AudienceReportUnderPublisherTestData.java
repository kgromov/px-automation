package px.reports.audience;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static px.reports.audience.AudienceFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 4/24/2017.
 */
public class AudienceReportUnderPublisherTestData extends AudienceReportTestData {

    public AudienceReportUnderPublisherTestData(int filters) {
        super(filters);
    }

    public AudienceReportUnderPublisherTestData(boolean isInstances) {
        super(0);
        this.isInstances = isInstances;
        setInstanceGroup("audience/report");
        setSorting("conversions", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        if (isInstances) {
            // set proper reportType, in this data class from defaultReportTypesMap
            this.reportType = DataHelper.getRandomValueFromList(new ArrayList<>(defaultReportTypesMap.keySet()));
            // rows in table by report type and default vertical
            setItemsByReportType(reportType);
        }
        super.setHeaders();
    }

    public void setItemsByReportType(String reportType) {
        log.info("Rows in table by report type and default vertical");
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        this.itemsByReportTypeCount = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getCurrentTotal() : totalCount;
    }

    @Override
    public List<String> filters() {
        return Collections.singletonList(REPORT_TYPE_FILTER);
    }

    @Override
    public String toString() {
        return "AudienceReportUnderPublisherTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
                "reportType=" + reportType +
                ", vertical=" + DEFAULT_VERTICAL +
                '}';
    }
}