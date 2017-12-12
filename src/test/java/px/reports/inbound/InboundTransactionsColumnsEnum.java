package px.reports.inbound;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum InboundTransactionsColumnsEnum  implements Valued {
    DATE("date"),
    VERTICAL("vertical"),
    TRANSACTION_ID("transactionId"),
    PUBLISHER_ID("publisherId"),
    SUB_ID("subId"),
    OFFER_ID("offerId"),
    EMAIL("email"),
    PHONE("phone"),
    IP("ipAddress"),
    RESPONSE("apiResponse");

    private final String value;

    public String getValue() {
        return value;
    }

    InboundTransactionsColumnsEnum(String value) {
        this.value = value;
    }
}
