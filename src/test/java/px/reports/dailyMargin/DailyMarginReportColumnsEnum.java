
package px.reports.dailyMargin;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */

/**
 * These enums are relevant only for default state (no categories)
 * daily - by default
 */
public enum DailyMarginReportColumnsEnum implements Valued {
    // Hourly, daily, weekly - forecastPubReturn
    DATE("dateTime"),
    LEAD_SUBMITS("leadSubmits"),
    LEADS("leads"),
    SOLD_RATION("soldRatio"),
    REVENUES("revenues"),
    UP_REVENUES("upRevenues"),
    FORECAST_RETURN("forecastReturn"),
    RPL("rpl"),
    RPLS("rpls"),
    REPORTED_LEADS("reportedLeads"),
    FORECAST_PUB("forecastPubReturn"),
    NET_SPEND("netSpend"),
    CPL("cpl"),
    CPLS("cpls"),
    MARGIN("margin"),
    NET_MARGIN("netMargin"),
    MARGIN_PERC("marginPerc"),
    NET_MARGIN_PERC("netMarginPerc"),
    QIQ("qiq");
    // Vertical - {forecastPubReturn, netSpend}
    // Publisher manager - {forecastPubReturn, netSpend}
    // Quality tier - {forecastPubReturn, netSpend}

    private final String value;

    public String getValue() {
        return value;
    }

    DailyMarginReportColumnsEnum(String value) {
        this.value = value;
    }
}
