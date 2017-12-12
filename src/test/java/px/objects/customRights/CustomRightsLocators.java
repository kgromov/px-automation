package px.objects.customRights;

/**
 * Created by kgr on 10/30/2017.
 */
public class CustomRightsLocators {
    // =============================== CustomRights Overview page ===============================
    public static final String CREATE_BUTTON = ".//a[contains(@href, 'customrights/create') and contains(@class, 'button')]";
    // =============================== Create CustomRight page ===============================
    public static final String GENERAL_CONTAINER = ".//*[@id='general']";
    public static final String CUSTOM_RIGHT_NAME = ".//*[@data-field-name='rightName']";
    public static final String CUSTOM_RIGHT_DESCRIPTION = ".//*[@data-field-name='association']";
}
