package px.reports.sourceQuality;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum SourceQualityReportColumnsEnum  implements Valued {
    PUBLISHER("publisher"),
    TYPE("type"),
    CAMPAIGN("campaign"),
    PUBLISHER_ID("publisherID"),
    SUB_ID("subID"),
    SOURCE("shortOrigin"),
    LEADS("leads"),
    PAYOUT("totalPayout"),
    CONVERSIONS("conversions"),
    CATEGORY("category"),
    QIQ("qiqScore"),
    VERTICAL("vertical"),
    LAST_ATTEMPT("lastAttempt"),
    IP_CHECK("ipCheckPerc"),
    LEAD_RETURN("lrPercentage");

    private final String value;

    public String getValue() {
        return value;
    }

    SourceQualityReportColumnsEnum(String value) {
        this.value = value;
    }
}
