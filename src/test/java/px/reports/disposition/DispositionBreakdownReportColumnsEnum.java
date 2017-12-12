package px.reports.disposition;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum DispositionBreakdownReportColumnsEnum implements Valued {
    PUBLISHER("disposition"),
    LEADS("totalLeads"),
    PAYOUT("totalPayout"),
    CONVERSIONS("cpl"),
    CATEGORY("averageReachTime");

    private final String value;

    public String getValue() {
        return value;
    }

    DispositionBreakdownReportColumnsEnum(String value) {
        this.value = value;
    }
}
