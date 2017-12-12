package px.objects.publishers;

import static pages.locators.ElementLocators.INPUT_CONTAINER;

/**
 * Created by kgr on 10/18/2016.
 */
public class PublishersPageLocators {
    // ================================== Publishers page ==================================
    public final static String CREATE_PUBLISHER_BUTTON = ".//a[@href='/admin/publishers/create']";
    public final static String ACTIONS_PUBLISHER = ".//a[contains(@href, '/admin/buyers/edit/') or text()='Edit']";
    public final static String EDIT_ITEM = "Edit";
    // =============================== Create Publisher page ===============================
    public final static String GENERAL_INFO_CONTAINER = ".//div[@id='general']";
    // inputs
    public final static String PUBLISHER_NAME_INPUT = ".//*[@data-field-name='publisherName']";
    public final static String SUB_ID_NAME_INPUT = ".//*[@data-field-name='publisherInstanceName']";
    // selects
    public final static String PUBLISHER_TIER_SELECT = ".//*[@data-field-name='publisherTier']";
    public final static String TYPE_SELECT = ".//*[@data-field-name='type']";
    public final static String ACCESS_MODE_SELECT = ".//*[@data-field-name='accessMode']";
    public final static String LEADING_BALANCE_SELECT = ".//*[@data-field-name='leadingBalance']";
    public final static String PUBLISHER_MANAGER_ID_SELECT = ".//*[@data-field-name='publisherManagerId']";
    // radio buttons
    public final static String PRICING_RADIO = ".//*[@data-field-name='fixedPricing']";
    public final static String UPSELL_BALANCE_RADIO = ".//*[@data-field-name='addUpsellToBalance']";
    // filled in
    public final static String MARGIN_INPUT = ".//*[@data-field-name='margin']";
    public final static String MIN_PRICE_INPUT = ".//*[@data-field-name='minPrice']";
    public final static String CHECK_PERCENTAGE_INPUT = ".//*[@data-field-name='escoreCheckPercentage']";
    public final static String START_BALANCE_INPUT = ".//*[@data-field-name='startBalance']";
    public final static String MAX_NEGATIVE_BALANCE_INPUT = ".//*[@data-field-name='maxNegativeBalance']";
    public final static String FREE_TESTS_INPUT = ".//*[@data-field-name='freeTests']";
    // ping post
    public final static String PING_POST_TYPE_SELECT = ".//*[@data-field-name='pingPostType']";
    public final static String BID_TYPE_SELECT = ".//*[@data-field-name='bidType']";
    public final static String OUTBID_PERCENTAGE_INPUT = ".//*[@data-field-name='outBidPerc']";
    public final static String PING_TIMEOUT_SELECT = ".//*[@data-field-name='pingTimeout']";
    public final static String BID_FLOOR_PERCENTAGE_INPUT = ".//*[@data-field-name='bidFloorPerc']";
    public final static String OUTBID_PERCENTAGE_PERCENTAGE = ".//*[@data-field-name='outBidPercPerc']";
    // =============================== Edit Publisher page ===============================
    // menu 'Publisher settings'
    public final static String GENERAL_INFO_ITEM = "General";
    public final static String TRACKING_DATA_ITEM = "Tracking Data";
    public final static String CONTACT_INFO_ITEM = "Contact Info";
    public final static String ADDRESS_INFO_ITEM = "Address Information";
    public final static String USER_MANAGEMENT_ITEM = "User Management";
    public final static String OFFER_PIXELS_ITEM = "Offer Pixels";
    public final static String OFFER_PAYOUTS_ITEM = "Offer Payouts";
    public final static String OFFER_ACCESS_ITEM = "Offer Access";
    // ---------------------------- General section ----------------------------
    public final static String SPENDING_INPUT = ".//*[@data-field-name='spending']";
    public final static String QUALITY_SCORE_INPUT = ".//*[@data-field-name='qiqScore']";
    public final static String LEAD_PERCENTAGE_INPUT = ".//*[@data-field-name='leadReturnPercentage']";
    public final static String FRAUD_PERCENTAGE_INPUT = ".//*[@data-field-name='fraudCheckPercentage']";
    public final static String STATUS_SELECT = ".//*[@data-field-name='status']";
    public final static String PARENT_INHERITANCE_RADIO = ".//*[@data-field-name='noParentInherit']";
    // ---------------------------- Tracking Data section ----------------------------
    public final static String TRACKING_DATA_CONTAINER = ".//div[@id='trackingData']";
    public final static String ACCOUNT_MANAGER_ID_SELECT = ".//*[@data-field-name='accountManagerId']";
    // ---------------------------- Contact Info section ----------------------------
    public final static String CONTACT_INFO_CONTAINER = ".//div[@id='contactInfo']";
    public final static String COMPANY_NAME_INPUT = ".//*[@data-field-name='companyName']";
    public final static String MARITAL_STATUS_SELECT = ".//*[@data-field-name='maritalState']";
    public final static String BILLING_CYCLE_SELECT = ".//*[@data-field-name='PRV/BillingCycle']";
    // ---------------------------- Address Info section ----------------------------
    public final static String ADDRESS_INFO_CONTAINER = ".//div[@id='addressInformation']";
    public final static String EXTRA_INFO_INPUT = ".//*[@data-field-name='extraInfo']";
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<< NOT COVERED >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // ---------------------------- User Management section ----------------------------
    public final static String USER_MANAGEMENT_CONTAINER = ".//div[@id='userManagement']";
    // ---------------------------- Offer Pixels section ----------------------------
    public final static String OFFER_PIXELS_CONTAINER = ".//div[@id='offerPixels']";
    public final static String OFFER_ID_SELECT = ".//*[@data-field-name='offerId']";
    public final static String CODE_INPUT = ".//*[@data-field-name='code']";
    public final static String PIXEL_STATUS_SELECT = ".//*[@data-field-name='offerPixelStatus']";
    // ---------------------------- Offer payouts section ----------------------------
    public final static String OFFER_PAYOUTS_CONTAINER = ".//div[@id='offerPayouts']";
    // ---------------------------- Offer Access section ----------------------------
    public final static String OFFER_ACCESS_CONTAINER = ".//div[@id='offerAccess']";
    public final static String OFFER_FILTER_INPUT = OFFER_ACCESS_CONTAINER + INPUT_CONTAINER.substring(1);
    // ---------------------------- Approved section ----------------------------
    public final static String APPROVED_TEXTAREA = ".//*[contains(@ng-show, '.approved')]";
    public final static String UNAPPROVE_BUTTON = APPROVED_TEXTAREA + "//a[contains(@*, '.unApprove')]";
    public final static String BLOCK_APPROVED_BUTTON = APPROVED_TEXTAREA + "//a[contains(@*, '.block')]";
    // ---------------------------- Unapproved section ----------------------------
    public final static String UNAPPROVED_TEXTAREA = ".//*[contains(@ng-show, '.unapproved')]";
    public final static String APPROVE_BUTTON = UNAPPROVED_TEXTAREA + "//a[contains(@*, '.approve')]";
    public final static String BLOCK_UNAPPROVED_BUTTON = UNAPPROVED_TEXTAREA + "//a[contains(@*, '.block')]";
    // ---------------------------- Blocked section ----------------------------
    public final static String BLOCKED_TEXTAREA = ".//*[contains(@ng-show, '.blocked')]";
    public final static String UNBLOCK_BUTTON = BLOCKED_TEXTAREA + "//a[contains(@*, '.unblock')]";
    public final static String BLOCK_BUTTON = ".//a[contains(@*, '.block')]";

}