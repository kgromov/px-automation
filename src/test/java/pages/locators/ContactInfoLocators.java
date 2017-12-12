package pages.locators;

/**
 * Created by kgr on 10/18/2016.
 */
public class ContactInfoLocators {
    // ---------------------------- Contact Data section ----------------------------
    // inputs
    public final static String FULL_NAME_INPUT = ".//*[@data-field-name='fullName']";
    public final static String FIRST_NAME_INPUT = ".//*[@data-field-name='firstName']";
    public final static String LAST_NAME_INPUT = ".//*[@data-field-name='lastName']";
    public final static String MIDDLE_NAME_INPUT = ".//*[@data-field-name='middleName']";
    public final static String INITIALS_INPUT = ".//*[@data-field-name='initials']";

    public final static String BIRTH_DATE_INPUT = ".//*[@data-field-name='birthDate']";
    public final static String GENDER_RADIO = ".//*[@data-field-name='gender']";

    public final static String MOBILE_PHONE_INPUT = ".//*[@data-field-name='TEL/Mobile']";
    public final static String BUSINESS_PHONE_INPUT = ".//*[@data-field-name='TEL/Business']";
    public final static String EMAIL_INPUT = ".//*[@data-field-name='emailAddress' or @data-field-name='email']";
    public final static String INTERNAL_EMAIL_INPUT = ".//*[contains(@data-field-name, '/email')]";
    public final static String WEB_INPUT = ".//*[@data-field-name='INT/web']";
    // ----------------------------  Address section ----------------------------
    public final static String ADDRESS_CONTAINER = ".//div[@id='companyAddress']";
    // inputs
    public final static String STREET_INPUT = ".//*[@data-field-name='street']";
    public final static String STREET_NUMBER_INPUT = ".//*[@data-field-name='streetNr']";
    public final static String CITY_INPUT = ".//*[@data-field-name='city']";
    public final static String ZIP_CODE_INPUT = ".//*[@data-field-name='zipCode']";
    // selects
    public final static String STATE_SELECT = ".//*[@data-field-name='state']";
    public final static String COUNTRY_SELECT = ".//*[@data-field-name='country']";
    // ---------------------------- Users section ----------------------------
    public final static String USER_CONTAINER = ".//div[@id='userManagement' or @id='users']";
    public final static String RIGHT_MANAGEMENT_CONTAINER = ".//div[@id='rightsManagement']";
    // inputs
    public final static String USER_EMAIL_INPUT = "//div[@data-field-name='userName']";
    public final static String USER_FULL_NAME_INPUT = "//div[@data-field-name='description']";
    public final static String PASSWORD_INPUT = "//div[@data-field-name='password']";
    public final static String CONFIRM_PASSWORD_INPUT = "//div[@data-field-name='confirmPassword']";
    public final static String USER_ROLE = "//div[@data-field-name='userRole']";
    public final static String CUSTOM_RIGHTS = "//div[@data-field-name='customRights']";
    // publisher field
    public final static String USER_PASSWORD_INPUT = ".//div[@data-field-name='userPassword']";
}