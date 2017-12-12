package px.objects.users.pages;

import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import dto.TestData;
import elements.dropdown.FilteredDropDown;
import elements.dropdown.SelectElement;
import org.openqa.selenium.support.FindBy;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.users.UserRoleTestData;
import px.objects.users.UserTestData;

import java.util.Arrays;

import static pages.locators.ContactInfoLocators.*;
import static px.objects.brokers.BrokersPageLocators.GENERAL_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.STATUS_SELECT;

/**
 * Created by kgr on 1/25/2017.
 */
public class EditUserPage extends AbstractUserPage implements Editable {
    @FindBy(xpath = USER_ROLE)
    private FilteredDropDown userRole;
    @FindBy(xpath = STATUS_SELECT)
    private SelectElement status;
    @FindBy(xpath = CUSTOM_RIGHTS)
    private FilteredDropDown customRights;

    public EditUserPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public EditUserPage editInstance(TestData pTestData) {
        if (!(pTestData instanceof UserTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        UserTestData testData = (UserTestData) pTestData;
        log.info(String.format("Update user '%s' general info", testData));
        helper.waitUntilDisplayed(USER_EMAIL_INPUT);
        editInstance(GENERAL_CONTAINER);
        listMenuElement.setByIndex(1);
//        email.setByText(testData.getEmail());
        fullName.setByText(testData.getFirstName(), testData.getLastName());
        return this;
    }

    public EditUserPage setUserRole(UserRoleTestData testData) {
        log.info("Update user rights");
        userRole.setByText(ObjectIdentityData.getAllNames(testData.getUserRoles()));
        status.setByTitle(testData.getStatus());
        listMenuElement.setByIndex(2);
        customRights.setByText(ObjectIdentityData.getAllNames(testData.getCustomRights()));
        return this;
    }

    @Override
    public void saveInstance(TestData testData) {
        saveInstance();
        if (testData.isPositive()) checkErrorMessage();
        else {
            checkErrorMessage(Arrays.asList(email, fullName));
        }
    }

    @Override
    public void saveInstance() {
        saveInstance(RIGHT_MANAGEMENT_CONTAINER);
    }

    @Override
    public Editable editInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}