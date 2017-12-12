package px.objects.leads;

import static elements.ElementsHelper.getTextContainsToIgnoreCaseLocator;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsPreviewPageLocators {
    // ==================================  Leads report page ==================================
    public static final String PREVIEW_LINK_ITEM = "Preview";
    public static final String PREVIEW_LINK = String.format(".//a[text()='%s']", PREVIEW_LINK_ITEM);
    public static final String UPDATE_DISPOSITION_LINK_ITEM = "Update Disposition";
    public static final String UPDATE_DISPOSITION_LINK = getTextContainsToIgnoreCaseLocator("a", UPDATE_DISPOSITION_LINK_ITEM);
    public static final String RETURN_LEAD_LINK_ITEM = "Return Lead";
    public static final String RETURN_LEAD_LINK = getTextContainsToIgnoreCaseLocator("a", RETURN_LEAD_LINK_ITEM);
    public static final String RERUN_LEAD_LINK_ITEM = "Rerun This Lead";
    public static final String RERUN_LEAD_LINK = String.format(".//a[text()='%s']", RERUN_LEAD_LINK_ITEM);
    public static final String UPDATE_DISPOSITION_BUTTON = ".//*[@buttons]//*[contains(text(), 'Update')]";
    // ==================================  Leads preview page ==================================
    public static final String LEAD_OVERVIEW_CONTAINER = ".//div[@id='leadoverview']";
    public static final String LEAD_CONTACT_CONTAINER = ".//div[@id='contact']";
    public static final String LEAD_DETAILS_CONTAINER = ".//div[@id='lead']";
    public static final String BUYER_DETAILS_CONTAINER = ".//div[@id='buyer']";
    public static final String LEAD_TCPA_CONTAINER = ".//div[@id='tcpa']";
    public static final String LEAD_REQUEST_CONTAINER = ".//div[@id='inbound']";
    public static final String LEAD_ATTRIBUTES_CONTAINER = ".//div[@id='answers']";
    public static final String LEAD_TRANSACTIONS_CONTAINER = ".//div[@id='transactions']";
    public static final String DISPOSITION_HISTORY_CONTAINER = ".//div[@id='disposition']";
    // menu 'Lead details'
    public static final String LEAD_OVERVIEW_ITEM = "Lead overview";
    public static final String LEAD_CONTACT_ITEM = "Contact information";
    public static final String LEAD_DETAILS_ITEM = "Lead details";
    public static final String BUYER_DETAILS_ITEM = "Buyer details";
    public static final String LEAD_TCPA_ITEM = "Consent language";
    public static final String LEAD_REQUEST_ITEM = "Inbound Request";
    public static final String LEAD_ATTRIBUTES_ITEM = "Lead Attributes";
    public static final String LEAD_TRANSACTIONS_ITEM = "Transactions";
    // Lead attributes
    public static final String BLOCK_BUTTON = ".//a[text()='Block']";
    public static final String SHOW_ALL_BUTTON = ".//a[@translate='ui.ShowAll']";
    public static final String SHOW_LESS_BUTTON = ".//a[@translate='ui.ShowLess']";
    // Table drop down links of Transactions table
    public static final String REQUEST_ITEM = "Request Data";
    public static final String REQUEST_ITEM_LINK = getTextContainsToIgnoreCaseLocator("a", REQUEST_ITEM);
    public static final String RESPONSE_ITEM = "Response Data";
    public static final String RESPONSE_ITEM_LINK = getTextContainsToIgnoreCaseLocator("a", RESPONSE_ITEM);
    // ================================== Update Disposition dialogue ==================================
    // email, buyerId columns - on Continue stage
    public static final String DISPOSITION_STATUS_SELECT = ".//*[@data-field-name='disposition']";
    public static final String DISPOSITION_DATE_CALENDAR = ".//*[@data-field-name='dispositionDate']";
    public static final String DISPOSITION_EXPLANATION_TEXTAREA = ".//*[@data-field-name='dispositionExplanation']";
    // ====================================== Single lead return ========================================
    public static final String BUYER_ID_TEXT = "//*[@data-field-name='buyerId']";   // for return lead
    public static final String EMAIL_INPUT = "//*[@data-field-name='emailAddress']";
    public static final String RETURN_REASON_SELECT = "//*[@data-field-name='standardReason']";
    public static final String EXPLANATION_TEXTAREA = "//*[@data-field-name='theirReason']";
    public static final String DECLINE_REASON_TEXTAREA = "//*[@data-field-name='declineReason']";
    // =================================== Single lead return report =====================================
    public static final String ACCEPT_LINK = ".//a[contains(@class, 'px-link button') and text()='Accept']";
    public static final String DECLINE_LINK = ".//a[contains(@class, 'px-link button') and text()='Decline']";
}
