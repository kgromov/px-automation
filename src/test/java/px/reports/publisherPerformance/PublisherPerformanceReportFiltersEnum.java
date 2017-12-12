package px.reports.publisherPerformance;

import px.reports.Valued;

/**
 * Created by kgr on 11/25/2016.
 */
public enum PublisherPerformanceReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Leads"),
    CATEGORY_2("Net revenues"),
    CATEGORY_3("Payout"),
    PUBLISHER_MANAGER("All Publisher Managers"),
    VERTICAL("All Verticals"),
    BUYER_CATEGORY("All Buyer categories");

    private final String value;

    public final static String DEFAULT_BAYER_CATEGORY = "Lead";

    public String getValue() {
        return value;
    }

    PublisherPerformanceReportFiltersEnum(String value) {
        this.value = value;
    }
}