package px.objects.campaigns;

/**
 * Created by kgr on 10/18/2016.
 */
public class CampaignsPageLocators {
    // ================================== Campaigns page ==================================
    public static final String CAMPAIGN_SETTING_ITEM = "Campaign Settings";
    public static final String CAMPAIGN_SETTINGS_LINK = String.format(".//a[text()='%s']", CAMPAIGN_SETTING_ITEM);
    public static final String API_CONFIGURATION_ITEM = "API Configuration";
    public static final String API_CONFIGURATION_LINK = String.format(".//a[text()='%s']", API_CONFIGURATION_ITEM);;
    public static final String FILTER_MANAGEMENT_ITEM = "Filter Management";
    public static final String FILTER_MANAGEMENT_LINK = String.format(".//a[text()='%s']", FILTER_MANAGEMENT_ITEM);
    public static final String SOURCE_MANAGEMENT_ITEM = "Source Management";
    public static final String SOURCE_MANAGEMENT_LINK = String.format(".//a[text()='%s']", SOURCE_MANAGEMENT_ITEM);
    public static final String PAYOUT_MANAGEMENT_ITEM = "Payout Management";
    public static final String PAYOUT_MANAGEMENT_LINK = String.format(".//a[text()='%s']", PAYOUT_MANAGEMENT_ITEM);
    public static final String DISPOSITION_MANAGEMENT_ITEM = "Disposition Management";
    public static final String DISPOSITION_MANAGEMENT_LINK = String.format(".//a[text()='%s']", DISPOSITION_MANAGEMENT_ITEM);
    public static final String CLONE_CAMPAIGN_ITEM = "Clone Campaign";
    public static final String CLONE_CAMPAIGN_LINK = String.format(".//a[text()='%s']", CLONE_CAMPAIGN_ITEM);
    public static final String CAMPAIGN_REPORT_ITEM = "Campaign Report";
    public static final String CAMPAIGN_REPORT_LINK = String.format(".//a[text()='%s']", CAMPAIGN_REPORT_ITEM);
    // =============================== Create Campaign page ===============================
    // menu settings
    public final static String GENERAL_ITEM = "General";
    public final static String COMMERCIAL_ITEM = "Commercial";
    public final static String VOLUME_ITEM = "Volume and Quality";
    public final static String GOALS_ITEM = "Goals";
    // ---------------------------- General section ----------------------------
    public final static String GENERAL_CONTAINER = ".//div[@id='general']";
    // inputs
    public final static String CAMPAIGN_NAME_INPUT = ".//*[@data-field-name='buyerInstanceName']";
    public final static String DAYS_FOR_DUPLICATE_INPUT = ".//*[@data-field-name='prevDupDays']";
    public final static String LEG_NAME_INPUT = ".//*[@data-field-name='legName']";
    public final static String HASH_LEG_NAME_INPUT = ".//*[@data-field-name='hashLegName']";
    // selects
    public final static String BUYER_SELECT = ".//*[@data-field-name='parentBuyerGuid']";
    public final static String COUNTRY_SELECT = ".//*[@data-field-name='country']";
    public final static String CURRENCY_SELECT = ".//*[@data-field-name='currency']";
    public final static String BUYER_CATEGORY_SELECT = ".//*[@data-field-name='buyerCategory']";
    public final static String BUYER_TYPE_SELECT = ".//*[@data-field-name='buyerType']";
    public final static String VERTICAL_SELECT = ".//*[@data-field-name='vertical']";
    public final static String DELIVERY_TYPE_SELECT = ".//*[@data-field-name='buyerInstanceDeliveryType']";
    // ---------------------------- Commercial section ----------------------------
    public final static String COMMERCIAL_CONTAINER = ".//div[@id='commercial']";
    // inputs
    public final static String DIRECT_BID_INPUT = ".//*[@data-field-name='directBid']";
    public final static String PAYOUT_QUALITY_INPUT = ".//*[@data-field-name='payoutQuality']";
    public final static String FLOOR_PAYOUT_INPUT = ".//*[@data-field-name='floorPayout']";
    public final static String MAX_LR_INPUT = ".//*[@data-field-name='fixedLRPerc']";
    public final static String LR_PAYOUT_QUALITY_INPUT = ".//*[@data-field-name='incLRinPayoutQualityPerc']";
    // selects
    public final static String BUYER_TIER_SELECT = ".//*[@data-field-name='tier']";
    // ---------------------------- Volume and Quality section ----------------------------
    public final static String VOLUME_CONTAINER = ".//div[@id='volume']";
    // inputs
    public final static String MONTHLY_CAP_INPUT = ".//*[@data-field-name='monthlyCap']";
    public final static String DAILY_CAP_INPUT = ".//*[@data-field-name='dailyCap']";
    public final static String FLOOR_SCORE_INPUT = ".//*[@data-field-name='floorScore']";
    // selects
    public final static String SOURCE_QUALITY_SELECT = ".//*[@data-field-name='qiqScore']";
    public final static String PUBLISHER_SCORE_SELECT = ".//*[@data-field-name='qiqCeiling']";
    // ---------------------------- Goals section ----------------------------
    public final static String GOALS_CONTAINER = ".//div[@id='goals']";
    public final static String BUYER_GOAL_SELECT = ".//*[@data-field-name='buyerGoal']";
    public final static String GOAL_PROFIT_INPUT = ".//*[@data-field-name='goalProfit']";
}