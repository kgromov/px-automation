package px.reports.pingPost;

import px.reports.Valued;

/**
 * Created by kgr on 11/25/2016.
 */
public enum PingPostReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Net revenues"),
    CATEGORY_2("Net margin"),
    CATEGORY_3("Net spending"),
    PUBLISHERS("All Publishers"),
    REPORT_TYPE("Day");

    private final String value;

    public final static String DEFAULT_REPORT_TYPE = "Day";

    public String getValue() {
        return value;
    }

    PingPostReportFiltersEnum(String value) {
        this.value = value;
    }
}