package px.objects.buyers;

/**
 * Created by kgr on 10/18/2016.
 */
public class BuyersPageLocators {
    // ================================== Buyers page ==================================
    public final static String CREATE_BUYER_BUTTON = ".//a[@href='/admin/buyers/create']";
    public final static String EDIT_BUYER = ".//a[contains(@href, '/admin/buyers/edit/')]";
    // settings menu items
    public final static String CONTACT_INFO_ITEM = "Contact info";
    public final static String COMPANY_ADDRESS_ITEM = "Company Address";
    public final static String MANAGER_SELECTION_ITEM = "Manager Selection";
    public final static String USERS_ITEM = "Users";
    // =============================== Create Buyer page ===============================
    // ---------------------------- Contact Info section ----------------------------
    public final static String CONTACT_INFO_CONTAINER = ".//div[@id='contactInfo']";
    // inputs
    public final static String BUYER_NAME_INPUT = ".//*[@data-field-name='buyerName']";
    public final static String FUNCTION_TITLE_INPUT = ".//*[@data-field-name='functionTitle']";
    public final static String CREDIT_LIMITS_INPUT = ".//*[@data-field-name='Buyer/CreditLimit']";
    // selects
    public final static String PAYMENT_TERMS_SELECT = ".//*[@data-field-name='PRV/PaymentTerms']";
    public final static String ADDITIONAL_INFO_SELECT = ".//*[@data-field-name='additionalContactItems']";
    // ---------------------------- Company Address section ----------------------------
    public final static String ADDRESS_CONTAINER = ".//div[@id='companyAddress']";
    // ---------------------------- Managers section ----------------------------
    public final static String MANAGERS_CONTAINER = ".//div[@id='managerSelection']";
    // selects
    public final static String CUSTOMER_MANAGERS_SELECT = ".//*[@data-field-name='Cust/Manager']";
    public final static String SALES_MANAGERS_SELECT = ".//*[@data-field-name='Cust/SalesManager']";
    // =============================== Edit Buyer page ===============================
    // ---------------------------- Users section ----------------------------
    public final static String USERS_CONTAINER = ".//div[@id='users']";
}