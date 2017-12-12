package px.reports.dashboard;

import px.reports.Valued;

/**
 * Created by kgr on 8/28/2017.
 */
public enum DashboardChartsEnum implements Valued {
    LEAD_STATISTICS_CHART("statistics"),
    LEADS_BUYING_CHART("leadsBuying"),
    AGE_DOUGHNUT_CHART("ageGroup"),
    GENDER_DOUGHNUT_CHART("gender"),
    STATE_PROGRESSBAR_CHART("state"),
    LEADS_COLUMN_CHART("leads"),
    CONVERSIONS_COLUMN_CHART("conversions"),
    LEADS_LIST_CHART("leadNames");

    private final String value;

    public String getValue() {
        return value;
    }

    DashboardChartsEnum(String value) {
        this.value = value;
    }
}
