package px.objects.sourceManagement;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementPageLocators {
    public static final String GENERAL_CONTAINER = ".//*[@id='accountStatus']";
    // =============================== Campaign status section  ===============================
    public static final String CAMPAIGN_STATUS_SECTION = ".//*[contains(@class, '-account-status')]";
    public static final String CAMPAIGN_STATUS = ".//*[@data-field-name='accountStatus']";
    public static final String CHERRY_PICK_CHECKBOX = ".//*[contains(@class, 'px-field-checkbox')]"; // .//*[@id='accountCherryPick']/../..
    // =============================== Campaign status section  ===============================
    public static final String ADD_EXCEPTION_SECTION = ".//*[contains(@class, '-add-exception')]";
    public static final String SOURCE_ID = ".//*[@data-field-name='sourceId']";
    public static final String STATUS = ".//*[@data-field-name='status']";
    public static final String PROCEED_BUTTON = ".//a[contains(@class, 'button')]"; //text()='Proceed'
}
