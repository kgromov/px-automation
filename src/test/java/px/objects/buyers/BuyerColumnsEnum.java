package px.objects.buyers;

/**
 * Created by kgr on 10/7/2016.
 */
public enum BuyerColumnsEnum {
    NAME("Buyer name"),
    ID("MTD"),
    LEADS("MTD Leads"),
    CLICKS("MTD Clicks"),
    CALLS("MTD Calls"),
    DATA("MTD Data"),
    OTHER("MTD Other"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    BuyerColumnsEnum(String value) {
        this.value = value;
    }
}
