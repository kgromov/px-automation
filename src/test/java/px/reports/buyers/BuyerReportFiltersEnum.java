package px.reports.buyers;

import config.Config;
import px.reports.Valued;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kgr on 11/25/2016.
 */
public enum BuyerReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Pings"),
    CATEGORY_2("Bids won"),
    CATEGORY_3("Payout"),
    BUYER("All Buyers"),
    SALES_MANAGER("All Sales Managers"),
    BUYER_CATEGORY("All Buyer categories");

    private final String value;

    public final static String DEFAULT_BAYER_CATEGORY = "Lead";

    BuyerReportFiltersEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BuyerReportFiltersEnum[] filters() {
        List<BuyerReportFiltersEnum> items = new ArrayList<>(Arrays.asList(values()));
        if (Config.isBuyer()) items.remove(BUYER);
        return items.toArray(new BuyerReportFiltersEnum[items.size()]);
    }
}