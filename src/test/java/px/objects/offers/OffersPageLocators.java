package px.objects.offers;

/**
 * Created by konstantin on 05.10.2016.
 */
public class OffersPageLocators {
    public final static String CREATE_OFFER_BUTTON = ".//a[text()='Create Offer' or @href='/offers/create/']";
    // =============================== Create Offers page ===============================
    // ---------------------------- General section ----------------------------
    public final static String GENERAL_CONTAINER = ".//div[@id='general']";
    // calendar
    public final static String CALENDAR_DROPDOWN = ".//div[contains(@class, 'dropdown-menu') and contains(@class, 'datetimepicker')]";
    public final static String CALENDAR_RANGE_DROPDOWN = ".//div[contains(@class, 'dropdown-menu') and contains(@class, 'daterangepicker')]";
    // inputs
    public final static String OFFER_NAME_INPUT = ".//*[@data-field-name='offerName']";
    public final static String PREVIEW_URL_INPUT = ".//*[@data-field-name='previewUrl']";
    public final static String OFFER_URL_INPUT = "//*[@data-field-name='offerUrl']";
    public final static String EXPIRATION_DATE_INPUT = ".//*[@data-field-name='expirationDate']";
    public final static String DESCRIPTION = ".//*[@data-field-name='description']";
    public final static String REF_ID_INPUT = ".//*[@data-field-name='refId']";
    public final static String NOTE_INPUT = ".//*[@data-field-name='note']";
    // selects
    public final static String CONVERSION_TRACKING_SELECT = ".//*[@data-field-name='protocol']";
    public final static String OFFER_CATEGORIES_SELECT = ".//*[@data-field-name='offerCategories']";
    public final static String STATUS_SELECT = ".//*[@data-field-name='offerStatus']";
    public final static String CURRENCY_SELECT = ".//*[@data-field-name='currency']";
    // ---------------------------- Payout section ----------------------------
    public final static String PAYOUT_CONTAINER = ".//div[@id='payout']";
    public final static String PAYOUT_TYPE_SELECT = ".//*[@data-field-name='payoutType']";
    public final static String PAYOUT_METHOD_RADIO = ".//*[@data-field-name='tieredPayout']";
    public final static String DEFAULT_PAYOUT_INPUT = ".//*[@data-field-name='defaultPayout']";
    public final static String PERC_PAYOUT_INPUT = ".//*[@data-field-name='percentPayout']";
    public final static String TIER_1_PAYOUT_INPUT = "(.//*[@data-field-name='payout'])[1]";
    public final static String TIER_2_PAYOUT_INPUT = "(.//*[@data-field-name='payout'])[2]";
    public final static String TIER_3_PAYOUT_INPUT = "(.//*[@data-field-name='payout'])[3]";
    public final static String TIER_1_PERC_PAYOUT_INPUT = "(.//*[@data-field-name='percPayout'])[1]";
    public final static String TIER_2_PERC_PAYOUT_INPUT = "(.//*[@data-field-name='percPayout'])[2]";
    public final static String TIER_3_PERC_PAYOUT_INPUT = "(.//*[@data-field-name='percPayout'])[3]";
    // ---------------------------- Revenue section ----------------------------
    public final static String REVENUE_CONTAINER = ".//div[@id='revenue']";
    public final static String REVENUE_TYPE_SELECT = ".//*[@data-field-name='revenueType']";
    public final static String REVENUE_METHOD_RADIO = ".//*[@data-field-name='tieredRevenue']";
    public final static String MAX_PAYOUT_INPUT = ".//*[@data-field-name='maxPayout']";
    public final static String MAX_PERC_PAYOUT_INPUT = ".//*[@data-field-name='maxPercentPayout']";
    public final static String TIER_1_MAX_PAYOUT_INPUT = "(.//*[@data-field-name='revenue'])[1]";
    public final static String TIER_2_MAX_PAYOUT_INPUT = "(.//*[@data-field-name='revenue'])[2]";
    public final static String TIER_3_MAX_PAYOUT_INPUT = "(.//*[@data-field-name='revenue'])[3]";
    public final static String TIER_1_MAX_PERC_PAYOUT_INPUT = "(.//*[@data-field-name='percRevenue'])[1]";
    public final static String TIER_2_MAX_PERC_PAYOUT_INPUT = "(.//*[@data-field-name='percRevenue'])[2]";
    public final static String TIER_3_MAX_PERC_PAYOUT_INPUT = "(.//*[@data-field-name='percRevenue'])[3]";
    // ---------------------------- Tracking section ----------------------------
    public final static String TRACKING_CONTAINER = ".//div[@id='tracking']";
    // selects
    public final static String TRACKING_DOMAIN_SELECT = ".//*[@data-field-name='hostnameId']";
    public final static String REDIRECT_OFFER_SELECT = ".//*[@data-field-name='redirectOfferId']";
    public final static String SESSION_HOURS_SELECT = ".//*[@data-field-name='sessionHours']";
    public final static String CUSTOM_SESSION_HOURS_INPUT = ".//*[@data-field-name='customSessionHours']";
    public final static String SESSION_IMPRESSION_HOURS_SELECT = ".//*[@data-field-name='sessionImpressionHours']";
    public final static String CUSTOM_SESSION_IMPRESSION_HOURS_INPUT = ".//*[@data-field-name='customSessionImpressionHours']";
    public final static String SECONDARY_OFFER_SELECT = ".//*[@data-field-name='convertedOfferType']";
    public final static String NETWORK_OFFER_SELECT = ".//*[@data-field-name='convertedOfferId']";
    public final static String SECONDARY_OFFER_URL_INPUT = "//*[@data-field-name='convertedOfferUrl']";
    // checkbox
    // input
    public final static String CONVERSION_CAP_INPUT = ".//*[@data-field-name='conversionCap']";
    // radio
    public final static String START_SESSION_RADIO = ".//*[@data-field-name='setSessionOnImpression']";
    // =============================== Edit Offer page ===============================
    // menu 'Offer settings'
    public final static String GENERAL_INFO_ITEM = "General Info";
    public final static String OFFER_URLS_ITEM = "Offer Urls";
    public final static String OFFER_PAYOUTS_ITEM = "Offer Payouts";
    public final static String OFFER_GROUPS_ITEM = "Offer Groups";
    public final static String OFFER_TARGETING_ITEM = "Offer Targeting";

    public final static String GENERAL_EDIT_CONTAINER = ".//div[@id='editBlock']";
    // ---------------------------- URLS section ----------------------------
    public final static String CREATE_OFFER_URL_BUTTON = ".//a[contains(@href, 'offerUrls/create')]";
    public final static String OFFER_URL_NAME_INPUT = ".//*[@data-field-name='offerUrlName']";
    public final static String OFFER_URL_STATUS_SELECT = ".//*[@data-field-name='offerUrlStatus']";
    public final static String URL_INPUT = ".//*[@data-field-name='url']";    
    // ---------------------------- Groups section ----------------------------
    public final static String OFFER_GROUPS_CONTAINER = ".//*[@id='offergroups']";
    public final static String OFFER_GROUPS_SELECT = ".//*[@data-field-name='newOfferRelatedGroups']";
    // ---------------------------- Targeting section ----------------------------
    public final static String OFFER_TARGETING_CONTAINER = ".//*[@id='offerTargeting']";
    public final static String TARGETING_DEVICE_TYPE_RADIO = "(.//*[@class='px-field'])[1]";
    public final static String TARGETING_ACTION_RADIO = ".//*[@data-field-name='action']";
    public final static String TARGETING_DEVICE_SELECT = ".//*[@data-field-name='targetRuleId']";
    public final static String CREATE_TARGETING =  "//a"; //"//a[text()='Create']";
}
