package px.objects.users.pages;

import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import dto.TestData;
import elements.dropdown.FilteredDropDown;
import org.openqa.selenium.support.FindBy;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.users.UserRoleTestData;
import px.objects.users.UserTestData;

import static pages.locators.ContactInfoLocators.USER_EMAIL_INPUT;
import static pages.locators.ContactInfoLocators.USER_ROLE;
import static px.objects.brokers.BrokersPageLocators.GENERAL_CONTAINER;

/**
 * Created by kgr on 1/25/2017.
 */
public class CreateUserPage extends AbstractUserPage implements Creatable {
    @FindBy(xpath = USER_ROLE)
    private FilteredDropDown userRole;

    public CreateUserPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateUserPage createInstance(TestData pTestData) {
        log.info("Create user");
        if (!(pTestData instanceof UserTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        UserTestData testData = (UserTestData) pTestData;
        helper.waitUntilDisplayed(USER_EMAIL_INPUT);
        email.setByText(testData.getEmail());
        fullName.setByText(testData.getFirstName(), testData.getLastName());
        password.setByText(testData.getPassword());
        confirmPassword.setByText(testData.getPassword());
        return this;
    }

    public CreateUserPage setUserRole(UserRoleTestData testData) {
        userRole.setByText(ObjectIdentityData.getAllNames(testData.getUserRoles()));
        return this;
    }

    @Override
    public void saveInstance() {
        saveInstance(GENERAL_CONTAINER);
    }

    @Override
    public Creatable createInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}