package px.reports.leadReturns;

import px.reports.Valued;

/**
 * Created by kgr on 9/6/2017.
 */
public enum LeadReturnStatusEnum implements Valued {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    DECLINED("Declined");

    private final String value;

    public String getValue() {
        return value;
    }

    LeadReturnStatusEnum(String value) {
        this.value = value;
    }
}
