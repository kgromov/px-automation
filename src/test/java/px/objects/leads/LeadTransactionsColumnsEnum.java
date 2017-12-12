package px.objects.leads;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum LeadTransactionsColumnsEnum implements Valued {
    LEAD_ID("Lead id"),
    DATE("Post date"),
    PAYOUT("Payout"),
    BUYER_NAME("Buyer name"),
    POST_TYPE("Post type"),
    RESULT_CODE("Post result code"),
    ACTIONS("");

    private final String value;

    public String getValue() {
        return value;
    }

    LeadTransactionsColumnsEnum(String value) {
        this.value = value;
    }
}
