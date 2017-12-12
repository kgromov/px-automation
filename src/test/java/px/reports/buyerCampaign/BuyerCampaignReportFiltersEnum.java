package px.reports.buyerCampaign;

import config.Config;
import px.reports.Valued;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kgr on 11/25/2016.
 */
public enum BuyerCampaignReportFiltersEnum implements Valued {
    GRAPHIC_TYPE("Graphics"),
    CATEGORY_1("Payouts"),
    CATEGORY_2("Leads"),
    CATEGORY_3("Leads quality"),
    BUYER_CAMPAIGN("All Buyers Campaigns"),
    TRANSACTION_TYPE("All Types"),
    REPORT_TYPE("Day"),
    BUYER_CATEGORY("All Buyer categories");

    private final String value;

    public final static String DEFAULT_BAYER_CATEGORY = "Lead";

    public final static String DEFAULT_REPORT_TYPE = "Day";

    BuyerCampaignReportFiltersEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BuyerCampaignReportFiltersEnum[] filters() {
        List<BuyerCampaignReportFiltersEnum> items = new ArrayList<>(Arrays.asList(values()));
        if (Config.isBuyer()) items.remove(TRANSACTION_TYPE);
        return items.toArray(new BuyerCampaignReportFiltersEnum[items.size()]);
    }
}