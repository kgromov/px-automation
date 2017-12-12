package px.objects.offers;

/**
 * Created by konstantin on 12.10.2016.
 */
public enum OfferSettingsEnum {
    GENERAL("General info"),
    PAYOUT("Payout settings"),
    REVENUE("Revenue settings"),
    TRACKING("Tracking settings");

    private final String value;

    public String getValue() {
        return value;
    }

    OfferSettingsEnum(String value) {
        this.value = value;
    }
}
