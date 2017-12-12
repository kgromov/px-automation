package px.reports.buyerPerformance;

import px.reports.Valued;

/**
 * Created by kgr on 11/25/2016.
 */
public enum BuyerPerformanceReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Pings"),
    CATEGORY_2("Bids won"),
    CATEGORY_3("Payout"),
    PUBLISHER("All Publishers"),
    BUYER_CATEGORY("All Buyer categories"),
    VERTICAL("All Verticals");

    private final String value;

    public String getValue() {
        return value;
    }

    BuyerPerformanceReportFiltersEnum(String value) {
        this.value = value;
    }
}