package px.objects.payouts;

import static px.objects.publishers.PublishersPageLocators.OFFER_PAYOUTS_CONTAINER;

/**
 * Created by kgr on 10/18/2016.
 */
public class PayoutsPageLocators {
    // ================================== Payouts page ==================================
    // Offer menu items
    public final static String OFFER_DETAILS_ITEM = "Offer details";
    public final static String ADVANCED_SETTINGS_ITEM = "Advanced settings";
    public final static String CREATE_SIMILAR_ITEM = "Create similar";
    public final static String PREVIEW_ITEM = "Preview";
    // Action items
    public static final String EDIT_LINK = ".//a[text()='Edit']";
    public static final String DELETE_LINK = ".//a[text()='Delete']";
    // =============================== Create Payout page ===============================
    public final static String BASIC_INFO_CONTAINER = ".//div[@id='editBlock']";
    // inputs
    public final static String PAYOUT_INPUT = ".//*[@data-field-name='payout']";
    public final static String PERCENTAGE_PAYOUT_INPUT = ".//*[@data-field-name='percPayout']";
    public final static String REVENUE_INPUT = ".//*[@data-field-name='revenue']";
    public final static String PERCENTAGE_REVENUE_INPUT = ".//*[@data-field-name='percRevenue']";
    public final static String CONVERSION_CAP_INPUT = ".//*[@data-field-name='conversionCap']";
    public final static String MONTHLY_CONVERSION_INPUT = ".//*[@data-field-name='monthlyConversionCap']";
    public final static String PAYOUT_CAP_INPUT = ".//*[@data-field-name='payoutCap']";
    public final static String MONTHLY_PAYOUT_INPUT = ".//*[@data-field-name='monthlyPayoutCap']";
    public final static String REVENUE_CAP_INPUT = ".//*[@data-field-name='revenueCap']";
    public final static String MONTHLY_REVENUE_INPUT = ".//*[@data-field-name='monthlyRevenueCap']";
    // selects
    public final static String PUBLISHER_ID_SELECT = ".//*[@data-field-name='publisherId']";
    public final static String OFFER_ID_SELECT = OFFER_PAYOUTS_CONTAINER + "//*[@data-field-name='offerId']";
    // text
    public final static String DEFAULT_PAYOUT_TEXT = ".//*[@data-field-name='defaultPayout']";
    public final static String PERCENT_PAYOUT_TEXT = ".//*[@data-field-name='percentPayout']";
    public final static String MAX_PAYOUT_TEXT = ".//*[@data-field-name='maxPayout']";
    public final static String MAX_PERCENT_PAYOUT_TEXT = ".//*[@data-field-name='maxPercentPayout']";
}
