package px.objects.users.pages;

import configuration.browser.PXDriver;
import dto.TestData;
import elements.input.InputElement;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.users.UserTestData;

import static pages.locators.ContactInfoLocators.*;
import static pages.locators.ElementLocators.CREATE_BUTTON;
import static pages.locators.ElementLocators.CREATE_TOGGLE;

/**
 * Created by kgr on 1/25/2017.
 */
public class NestedCreateUserPage extends AbstractUserPage implements Creatable {

    public NestedCreateUserPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public NestedCreateUserPage createInstance(TestData pTestData) {
        if (!(pTestData instanceof UserTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        UserTestData testData = (UserTestData) pTestData;
        log.info(String.format("Create user '%s'", testData));
        helper.click(USER_CONTAINER + CREATE_TOGGLE);
        helper.waitUntilDisplayed(USER_EMAIL_INPUT);
        email.setByText(testData.getEmail());
        fullName = new InputElement(helper.getElement(USER_CONTAINER + USER_FULL_NAME_INPUT));
        fullName.setByText(testData.getFirstName(), testData.getLastName());
        password.setByText(testData.getPassword());
        confirmPassword.setByText(testData.getPassword());
        return this;
    }

    @Override
    public Creatable createInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }

    @Override
    public void saveInstance() {
        helper.click(USER_CONTAINER + CREATE_BUTTON);
        pxDriver.waitForAjaxComplete();
    }
}