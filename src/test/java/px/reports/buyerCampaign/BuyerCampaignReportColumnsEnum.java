package px.reports.buyerCampaign;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */

/**
 * These enums are relevant only for default state (no categories)
 * Lead - by default
 */
public enum BuyerCampaignReportColumnsEnum implements Valued {
    DATE("dateTime"),
    TOTAL_LEADS("totalLeads"),
    FILTERING_PINGS("filteredPings"),
    PINGS("pings"),
    ACCEPTED_PINGS("acceptedPings"),
    AVERAGE_PING_BIDS("averagePingBid"),
    LOW_BIDS("bidTooLow"),
    BIDS_WON("bidsWon"),
    LEADS("leads"),
    PAYOUT("payout"),
    CPL("cpl"),
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
    CLICKS("clicks"),
    VIEWS("views"),
    CLICK_RATE("clickTroughRate"),
    // Data
    QUANTITY("qty");

    private final String value;

    public String getValue() {
        return value;
    }

    BuyerCampaignReportColumnsEnum(String value) {
        this.value = value;
    }
}
