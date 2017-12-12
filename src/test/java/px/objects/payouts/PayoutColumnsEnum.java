package px.objects.payouts;

/**
 * Created by kgr on 10/7/2016.
 */
public enum PayoutColumnsEnum {
    ID("Publisher ID"),
    PAYOUT("Payout"),
    PERC_PAYOUT("Perc payout"),
    REVENUE(" Revenue"),
    PERC_REVENUE("Perc revenue"),
    CONVERSION("Conversion cap"),
    MONTHLY_CONVERSION("Monthly conversion cap"),
    PAYOUT_CAP("Payout cap"),
    MONTHLY_PAYOUT(" Monthly payout cap"),
    REVENUE_CAP("Revenue cap"),
    MONTHLY_REVENUE("Monthly revenue cap"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    PayoutColumnsEnum(String value) {
        this.value = value;
    }
}
