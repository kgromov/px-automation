package px.reports.rerun;

import px.reports.Valued;

/**
 * Created by konstantin on 06.10.2017.
 */
public enum RerunManagementColumnsEnum implements Valued {
    NAME("rerunFilterName"),
    RERUN_TIME("rerunTime"),
    RUN_CYCLE("runCycle"),
    FROM_DATE("filterFromDate"),
    TO_DATE("filterToDate"),
    AGE_MIN("leadAgeMin"),
    AGE_MAX("leadAgeMax"),
    ALLOW_REPOSTED("allowReposted"),
    IGNORE_STATUS("ignoreRerunStatus"),
    CREATION_DATE("creationDate");

    private final String value;

    public String getValue() {
        return value;
    }

    RerunManagementColumnsEnum(String value) {
        this.value = value;
    }
}
