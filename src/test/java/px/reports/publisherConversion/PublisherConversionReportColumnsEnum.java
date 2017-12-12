package px.reports.publisherConversion;

import px.reports.Valued;

public enum PublisherConversionReportColumnsEnum implements Valued {
    OFFER_NAME("offerID"),
    OFFER_ID("offer"),
    PUBLISHER_ID("affiliateID"),
    PUBLISHER_NAME("affiliate"),
    PUBLISHER_MANAGER("affiliateManager"),
    OFFER_URL("offerUrl"),              // all default
    SESSION_DATE("sessionDateTime"),    // 0000-00-00 00:00:00
    CREATION_DATE("creationDate"),
    DATE_DIFF("dateTimeDiff"),         // all 838:59:59
    CONVERSION_STATUS("convStatus"),  // all rejected
    STATUS_MESSAGE("convStatusMessage"), // total is missed in 1st row
    NOTE("note"),                       // total is missed in 1st row
    PAYOUT("payout"),
    REVENUE("revenue"),
    SALE_AMOUNT("saleAmount"),
    SESSION_IP("sessionIP"),        // empty is it ok?
    CONVERSION_IP("conversionIP"),
    TRANSACTION_ID("transactionID"),
    SOURCE("affiliateSource"),      // 106%5F1%5F332 - not decoded
    SUB_ID_1("affiliateSubID_1"),
    SUB_ID_2("affiliateSubID_2"),
    SUB_ID_3("affiliateSubID_3"),
    SUB_ID_4("affiliateSubID_4"),
    SUB_ID_5("affiliateSubID_5"),
    BROWSER("browser"),             // all null
    USER_AGENT("userAgent"),
    ADJUSTMENT("adjustment");

    private final String value;

    public String getValue() {
        return value;
    }

    PublisherConversionReportColumnsEnum(String value) {
        this.value = value;
    }
}
