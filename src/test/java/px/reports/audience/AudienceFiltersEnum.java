package px.reports.audience;

import config.Config;
import px.reports.Valued;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kgr on 4/24/2017.
 */
public enum AudienceFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Leads"),
    CATEGORY_2("Net revenues"),
    CATEGORY_3("RPL"),
    REPORT_TYPE("State"),
    VERTICALS("All Verticals"),
    BUYERS("All Buyers"),
    //    BUYER_CAMPAIGNS("All Buyer campaigns"),
    PUBLISHERS("All Verticals");
//    PUBLISHER_SUB_IDS("All Publisher SubIDs");

    private final String value;

    public final static String DEFAULT_REPORT_TYPE = "State";

    public final static String DEFAULT_VERTICAL = "All";

    public String getValue() {
        return value;
    }

    AudienceFiltersEnum(String value) {
        this.value = value;
    }

    public static AudienceFiltersEnum[] filters() {
        List<AudienceFiltersEnum> items = new ArrayList<>(Arrays.asList(values()));
        if (!Config.isAdmin()) items.remove(PUBLISHERS);
        if (Config.isPublisher()) items.removeAll(Arrays.asList(VERTICALS, BUYERS));
        return items.toArray(new AudienceFiltersEnum[items.size()]);
    }
}