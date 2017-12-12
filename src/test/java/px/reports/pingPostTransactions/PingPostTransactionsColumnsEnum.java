package px.reports.pingPostTransactions;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum PingPostTransactionsColumnsEnum implements Valued {
    DATE("postDate"),
    TRANSACTION_ID("transactionId"),
    PUBLISHER_ID("publisherId"),
    SUB_ID("subId"),
    VERTICAL("vertical"),
    PAYOUT("payoutReported"),
    LEAD_PRICE("payoutCalculated"),
    TRANSACTION_DURATION("transactionDuration"),
    EMAIL("email"),
    POST_TYPE("postType"),
    POST_RESULT("publisherPostResultCode"),
    LEAD_GUID("leadGuid");

    private final String value;

    public String getValue() {
        return value;
    }

    PingPostTransactionsColumnsEnum(String value) {
        this.value = value;
    }
}
