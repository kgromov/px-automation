package px.reports.sourceQuality;

import config.Config;
import px.reports.Valued;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kgr on 11/25/2016.
 */
public enum SourceQualityScoreFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Conversions"),
    CATEGORY_2("Quality"),
    CATEGORY_3("Lead return %"),
    PUBLISHER("All Publishers"),
    BUYER("All Buyers");

    private final String value;

    public String getValue() {
        return value;
    }

    SourceQualityScoreFiltersEnum(String value) {
        this.value = value;
    }

    public static SourceQualityScoreFiltersEnum[] filters() {
        List<SourceQualityScoreFiltersEnum> items = new ArrayList<>(Arrays.asList(values()));
        if (!Config.isAdmin()) items.remove(PUBLISHER);
        if (Config.isPublisher()) items.remove(BUYER);
        return items.toArray(new SourceQualityScoreFiltersEnum[items.size()]);
    }
}