package px.reports.outbound;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum OutboundTransactionsColumnsEnum implements Valued {
    DATE("postDate"),
    TRANSACTION_ID("transactionId"),
    PUBLISHER_ID("publisherId"),
    SOURCE_ID("sourceId"),
    SUB_ID("subId"),
    PAYOUT("payout"),
    EMAIL("email"),
    BUYER_CAMPAIGN("buyerName"),
    POST_TYPE("postType"),
    POST_RESULT("buyerPostResultCode");

    private final String value;

    public String getValue() {
        return value;
    }

    OutboundTransactionsColumnsEnum(String value) {
        this.value = value;
    }
}
