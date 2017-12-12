package px.reports.leads;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum LeadsReportColumnsEnum implements Valued {
//    TD_0(""),
    DATE("creationDate"),
    TRANSACTION_ID("transactionId"),
    VERTICAL("vertical"),
    CAMPAIGN_ID("buyerId"),
    PAYOUT("payout"),
    SLOTS("numSlots"),
    PUBLISHER("publisherName"),
    SHORT("shortOrigin"),
    SUB_ID("subID"),
    OFFER_ID("offerId"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PHONE("phoneNumber"),
    STATE("state"),
    QUALITY("qiqScore"),
    FRAUD("fraudScore"),
    DISPOSITION("disposition");

    private final String value;

    public String getValue() {
        return value;
    }

    LeadsReportColumnsEnum(String value) {
        this.value = value;
    }
}
