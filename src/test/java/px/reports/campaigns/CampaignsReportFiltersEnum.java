package px.reports.campaigns;

import px.reports.Valued;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static config.Config.isBuyer;

/**
 * Created by kgr on 11/25/2016.
 */
public enum CampaignsReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Leads"),
    CATEGORY_2("Quality"),
    CATEGORY_3("Total spend"),
    VERTICALS("All Verticals"),
    BUYERS("All Buyers");

    private final String value;

    CampaignsReportFiltersEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CampaignsReportFiltersEnum[] filters() {
        List<CampaignsReportFiltersEnum> items = new ArrayList<>(Arrays.asList(values()));
        if (isBuyer()) items.remove(BUYERS);
        return items.toArray(new CampaignsReportFiltersEnum[items.size()]);
    }
}