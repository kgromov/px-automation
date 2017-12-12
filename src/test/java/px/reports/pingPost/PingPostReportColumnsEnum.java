package px.reports.pingPost;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum PingPostReportColumnsEnum implements Valued {
    DATE("postDate"),
    TRANSACTION_ID("transactionId"),
    PUBLISHER_ID("publisherId"),
    SUB_ID("subId"),
    VERTICAL("vertical"),
    PAYOUT("payoutReported"),
    LEAD_PRICE("payoutCalculated"),
    POST_TYPE("postType"),
    POST_RESULT("publisherPostResultCode");

    private final String value;

    public String getValue() {
        return value;
    }

    PingPostReportColumnsEnum(String value) {
        this.value = value;
    }
}
