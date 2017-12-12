package px.objects.sourceManagement;

import px.reports.Valued;

/**
 * Created by kgr on 10/30/2017.
 */
public enum SourceManagementColumnsEnum implements Valued {
    CAMPAIGN("campaignName"),
    PUBLISHER("publisherName"),
    SUB_ID("subId"),
    SOURCE_ID("sourceId"),
    STATUS("status");

    private final String value;

    public String getValue() {
        return value;
    }

    SourceManagementColumnsEnum(String value) {
        this.value = value;
    }
}
