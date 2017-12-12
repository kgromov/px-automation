package px.funtional.lead;

import px.reports.Valued;

/**
 * Created by kgr on 11/9/2017.
 */
public enum InsertTypes implements Valued {
    INSERT("Lead.Insert"),
    CDS_INSERT("Lead.CDSInsert"), // Directpost = Lead.Inser|Lead.CDSInsert
    PING("Lead.Ping"),
    POST("Lead.Post"),
    DIRECT_POST("Lead.Directpost");

    private final String value;

    public String getValue() {
        return value;
    }

    InsertTypes(String value) {
        this.value = value;
    }
}
