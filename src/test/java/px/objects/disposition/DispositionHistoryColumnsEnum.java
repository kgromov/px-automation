package px.objects.disposition;

/**
 * Created by kgr on 10/7/2016.
 */
public enum DispositionHistoryColumnsEnum {
    DATE("dispositionDate"),
    STATUS("disposition"),
    BUYER_ID("buyerId"),
    DATE_DIFF("reachTime"),
    EXPLANATION("dispositionExplanation");

    private final String value;

    public String getValue() {
        return value;
    }

    DispositionHistoryColumnsEnum(String value) {
        this.value = value;
    }
}