package px.reports.buyerPerformance;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */

public enum BuyerPerformanceReportColumnsEnum implements Valued {
    BUYER_NAME("buyerName"),
    BUYER_CATEGORY("buyerCategory"),
    QUANTITY("qty"),
    SPEND("spend"),
    CPL("cpl"),
    // Lead
    PINGS("pings"),
    ACCEPTED_PINGS("acceptedPings"),
    AVERAGE_PINGS("averagePingBid"),
    BID_LOW("bidTooLow"),
    BIDS_WON("bidsWon"),
    LEADS("leads"),
    PAYOUT("payout"),
    FORECAST("forecastReturn"),
    NET_REVENUE("netRevenues"),
    QIQ("qiq"),
    TAKE_RATE("takeRate"),
    WIN_PERCENTAGE("winPercentage"),
    ACCEPTANCE_RATE("acceptanceRate"),
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
    VIEWS("views"),
    CLICKS("clicks"),
    CLICK_RATE("clickTroughRate");

    private final String value;

    public String getValue() {
        return value;
    }

    BuyerPerformanceReportColumnsEnum(String value) {
        this.value = value;
    }
}
