package px.objects.publishers;

/**
 * Created by kgr on 10/7/2016.
 */
public enum PublisherColumnsEnum {
    NAME("Publisher"),
    ID("Publisher ID"),
    MARGIN("Margin"),
    RETURN("Lead return percentage"),
    MODE("Access mode"),
    UPSELL("Add upsell to balance"),
    PRICING("Fixed pricing"),
    CLICKS("Clicks"),
    CONVERSION("Conversions"),
    COST("Cost"),
    FILTERS(""),
    // not in use
    ESCORE_PERCENTAGE("Escore check percentage"),
    FRAUD_PERCENTAGE("Fraud check percentage"),
    TIER("Publisher tier"),
    TYPE("Type");

    private final String value;

    public String getValue() {
        return value;
    }

    PublisherColumnsEnum(String value) {
        this.value = value;
    }
}
