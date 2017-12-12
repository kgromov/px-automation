package px.reports.buyers;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */

/**
 * These enums are relevant only for default state (no categories)
 * Lead - by default
 */
public enum BuyerReportColumnsEnum  implements Valued {
    BUYER_CAMPAIGN("buyerInstance"),
    VERTICAL("vertical"),
    PINGS("pings"),
    BIDS_WON("bidsWon"),
    PAYOUT("payout"),
    CPL("cpl"),
    WIN_PERCENTAGE("winPercentage"),
    ACCEPTANCE_RATE("acceptanceRate"),
    QIQ("qiq"),
    LATEST_RETURN("latestReturns"),
    SALES_MANAGERS("salesManagerUsername"),
    // Call
    CALLS("calls"),
    PAID_CALLS("paidCalls"),
    TRANSACTIONS("transactions"),
    EARNED("earned"),
    PAID("paid"),
    MARGIN("margin"),
    AVERAGE_PAID("avgPaid"),
    CONVERSION("conversion"),
    // Click
    CLICKS("clicks"),
    VIEWS("views"),
    CLICK_RATE("clickTroughRate"),
    // Data
    QUANTITY("qty");

    private final String value;

    public String getValue() {
        return value;
    }

    BuyerReportColumnsEnum(String value) {
        this.value = value;
    }
}
