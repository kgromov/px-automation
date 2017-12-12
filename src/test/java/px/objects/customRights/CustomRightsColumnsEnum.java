package px.objects.customRights;

import px.reports.Valued;

/**
 * Created by kgr on 10/30/2017.
 */
public enum CustomRightsColumnsEnum implements Valued {
    NAME("rightName"),
    DESCRIPTION("association"),
    STATUS("Status"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    CustomRightsColumnsEnum(String value) {
        this.value = value;
    }
}
