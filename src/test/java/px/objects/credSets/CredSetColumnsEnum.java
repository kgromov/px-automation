package px.objects.credSets;

import px.reports.Valued;

/**
 * Created by kgr on 10/30/2017.
 */
public enum CredSetColumnsEnum implements Valued {
    NAME("credSetName"),
    DESCRIPTION("description"),
    STATUS("Status"),
    DAILY_CAP("loginDate"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    CredSetColumnsEnum(String value) {
        this.value = value;
    }
}
