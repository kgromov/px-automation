package px.objects.users.pages;

import configuration.browser.PXDriver;
import dto.TestData;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.users.UserTestData;

import java.util.Arrays;

import static pages.locators.ContactInfoLocators.*;
import static pages.locators.ElementLocators.DELETE_POPUP;
import static pages.locators.ElementLocators.POPUP_CONFIRM;

/**
 * Created by kgr on 1/25/2017.
 */
public abstract class AbstractUserPage extends ObjectPage {
    @FindBy(xpath = USER_EMAIL_INPUT)
    protected InputElement email;
    @FindBy(xpath = USER_FULL_NAME_INPUT)
    protected InputElement fullName;
    @FindBy(xpath = PASSWORD_INPUT)
    protected InputElement password;
    @FindBy(xpath = CONFIRM_PASSWORD_INPUT)
    protected InputElement confirmPassword;

    public AbstractUserPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public AbstractUserPage resetPassword(UserTestData testData) {
        helper.waitUntilDisplayed(DELETE_POPUP + PASSWORD_INPUT);
        password = new InputElement(helper.getElement(DELETE_POPUP + PASSWORD_INPUT));
        confirmPassword = new InputElement(helper.getElement(DELETE_POPUP + CONFIRM_PASSWORD_INPUT));
        password.setByText(testData.getPassword());
        confirmPassword.setByText(testData.getPassword());
        // save password reset
        helper.click(POPUP_CONFIRM);
        if (testData.isPositive()) {
            checkErrorMessage();
            helper.waitUntilToBeInvisible(DELETE_POPUP);
        } else checkErrorMessage(Arrays.asList(password, confirmPassword));
        return this;
    }

    public void saveInstance(TestData testData) {
        saveInstance();
        if (testData.isPositive()) checkErrorMessage();
        else {
            checkErrorMessage(Arrays.asList(email, fullName, password, confirmPassword));
//            checkErrorMessageByLocators(Arrays.asList(USER_EMAIL_INPUT, USER_FULL_NAME_INPUT, PASSWORD_INPUT));
        }
    }

    public abstract void saveInstance();

    @Override
    public Creatable saveInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}