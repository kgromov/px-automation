package px.reports;

/**
 * Created by kgr on 11/25/2016.
 */
public enum SearchByEnum implements Valued {
    TRANSACTION_ID("Transaction ID"),
    LEAD_GUID("Lead Guid"),
    EMAIL("Email"),
    PHONE_NUMBER("Phone number");

    private final String value;

    public String getValue() {
        return value;
    }

    SearchByEnum(String value) {
        this.value = value;
    }
}
