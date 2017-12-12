package px.reports.publisherPerformance;

import px.reports.Valued;

enum PublisherPerformanceReportColumnsEnum implements Valued {
    PUBLISHER_ID("publisher"),
    PUBLISHER_NAME("publisherName"),
    PUBLISHER_TYPE("publisherType"),
    LEADS_SUBMIT("leadSubmits"),
    LEADS("leads"),
    TOTAL_REVENUE("revenues"),
    UP_REVENUES("upRevenues"),
    FORECAST_RETURN("forecastReturn"),
    NET_REVENUES("netRevenues"),
    REVENUE_PER_LEAD("rpl"),
    NET_LEADS("reportedLeads"),
    SPENDING("spending"),
    COST_PER_LEAD("cpl"),
    CPLS("cpls"),
    MARGIN("margin"),
    MARGIN_PERCENTAGE("marginPerc"),
    QIQ("qiq");

    private final String value;

    public String getValue() {
        return value;
    }

    PublisherPerformanceReportColumnsEnum(String value) {
        this.value = value;
    }
}
