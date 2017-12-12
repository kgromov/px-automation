package px.reports.publisherDaily;

import config.Config;
import px.reports.Valued;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by konstantin on 10.04.2017.
 */
public enum PublisherDailyReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Leads"),
    CATEGORY_2("Net revenues"),
    CATEGORY_3("Payout"),
    PUBLISHERS("All Publishers"),
    PUBLISHER_MANAGERS("All Publisher Managers"),
    REPORT_TYPE("Report type"),
    GROUPING("Grouping"),
    OFFERS("All Offers"),
    BROWSERS("All Browsers");

    private final String value;

    public final static String DEFAULT_REPORT_TYPE = "Month";

    public String getValue() {
        return value;
    }

    PublisherDailyReportFiltersEnum(String value) {
        this.value = value;
    }

    public static PublisherDailyReportFiltersEnum[] filters() {
        List<PublisherDailyReportFiltersEnum> items = new ArrayList<>(Arrays.asList(values()));
        if (Config.isPublisher()) items.removeAll(Arrays.asList(PUBLISHERS, PUBLISHER_MANAGERS));
        return items.toArray(new PublisherDailyReportFiltersEnum[items.size()]);
    }
}
