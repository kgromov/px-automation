package px.reports.disposition;

import px.reports.Valued;

/**
 * Created by kgr on 11/25/2016.
 */
public enum DispositionBreakdownFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Total leads"),
    CATEGORY_2("Totals payout"),
    CATEGORY_3("Cost per lead"),
    PUBLISHER("All Publishers"),
    BUYER("All Buyers"),
    VERTICAL("All Verticals");

    private final String value;

    public String getValue() {
        return value;
    }

    DispositionBreakdownFiltersEnum(String value) {
        this.value = value;
    }

}