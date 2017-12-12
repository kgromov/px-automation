package px.objects.brokers;

import px.reports.Valued;

/**
 * Created by kgr on 10/7/2016.
 */
public enum BrokerColumnsEnum implements Valued{
    NAME("Broker name"),
    DESCRIPTION("Description"),
    ID("Broker ID"),
    STATUS("Status"),
    DAILY_CAP("Daily cap"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    BrokerColumnsEnum(String value) {
        this.value = value;
    }
}
