package px.objects.offers;

/**
 * Created by kgr on 10/7/2016.
 */
public enum OfferColumnsEnum {
    NAME("Offer Name"),
    ID("Offer ID"),
    CATEGORY("Offer category"),
    CLICKS("Clicks"),
    COST("Total cost"),
    QUALITY("Quality"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    OfferColumnsEnum(String value) {
        this.value = value;
    }
}
