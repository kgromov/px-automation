package px.objects.credSets;

/**
 * Created by kgr on 10/30/2017.
 */
public class CredSetLocators {
    // =============================== CredSets Overview page ===============================
    public static final String CREATE_BUTTON = ".//a[contains(@href, 'credsetscreate') and contains(@class, 'button')]";
    // =============================== Create CredSet page ===============================
    public static final String GENERAL_CONTAINER = ".//*[@id='general']";
    public static final String CRED_SET_NAME = ".//*[@data-field-name='credSetName']";
    public static final String CRED_SET_DESCRIPTION = ".//*[@data-field-name='description']";
    public static final String CRED_SET_GUID = ".//*[@data-field-name='credSetGuid']";
}
