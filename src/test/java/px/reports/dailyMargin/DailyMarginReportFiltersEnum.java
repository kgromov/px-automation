package px.reports.dailyMargin;

import px.reports.Valued;

/**
 * Created by kgr on 11/25/2016.
 */
public enum DailyMarginReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Net revenues"),
    CATEGORY_2("Net margin"),
    CATEGORY_3("Net spending"),
    PUBLISHERS("All Publishers"),
    REPORT_TYPE("Day"),
    VERTICALS("All Verticals"),
    BUYER_CATEGORIES("All Buyer categories");

    private final String value;

    public final static String DEFAULT_REPORT_TYPE = "Day";

    public String getValue() {
        return value;
    }

    DailyMarginReportFiltersEnum(String value) {
        this.value = value;
    }
}