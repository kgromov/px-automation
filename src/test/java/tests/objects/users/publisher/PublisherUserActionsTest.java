package tests.objects.users.publisher;

import configuration.dataproviders.PublisherDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import pages.LoginPage;
import px.objects.publishers.pages.EditPublishersPage;
import px.objects.users.UserRoleTestData;
import px.objects.users.pages.NestedCreateUserPage;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.USER_MANAGEMENT_ITEM;

/**
 * Created by konstantin on 21.10.2016.
 */
public class PublisherUserActionsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "deleteUserData", dataProviderClass = PublisherDataProvider.class)
    public void deletePublisherUser(UserRoleTestData testData, ObjectIdentityData publisher) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if publisher does not contain any
        if (testData.getUser() == null) {
            new NestedCreateUserPage(pxDriver)
                    .createInstance(testData)
                    .saveInstance(testData);
            // check presence in table
            usersPage.checkCreatedInstanceByName(testData.getEmail(), 0, true);
        }
        // delete user
        usersPage.deleteUser(testData.getEmail())
                // check changed status
                .checkUserIsDeleted(testData.getEmail());
    }

    @Test(dataProvider = "resetUserPasswordPositiveData", dataProviderClass = PublisherDataProvider.class)
    public void resetPublisherUserPasswordWithPositiveData(UserRoleTestData testData, ObjectIdentityData publisher) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if publisher does not contain any
        if (testData.getUser() == null) {
            new NestedCreateUserPage(pxDriver)
                    .createInstance(testData)
                    .saveInstance(testData);
            // check presence in table
            usersPage.checkCreatedInstanceByName(testData.getEmail(), 0, true);
        }
        // invoke reset password popup
        usersPage.resetPassword(testData.getEmail())
                // reset password
                .resetPassword(testData);
        // check that ability to login with changed credentials
        logout();
        new LoginPage(pxDriver).loginAs(testData);
        logout();
    }

    @Test(dataProvider = "resetUserPasswordNegativeData", dataProviderClass = PublisherDataProvider.class)
    public void resetPublisherUserPasswordWithNegativeData(UserRoleTestData testData, ObjectIdentityData publisher) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if publisher does not contain any
        if (testData.getUser() == null) {
            new NestedCreateUserPage(pxDriver)
                    .createInstance(testData)
                    .saveInstance(testData);
            // check presence in table
            usersPage.checkCreatedInstanceByName(testData.getEmail(), 0, true);
            testData.withPassword("").withDataMode(false);
        }
        // invoke reset password popup
        usersPage.resetPassword(testData.getEmail())
                // reset password
                .resetPassword(testData);
    }

    @Test(dataProvider = "resetUserPasswordPositiveData", dataProviderClass = PublisherDataProvider.class)
    public void loginAsPublisherUser(UserRoleTestData testData, ObjectIdentityData publisher) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if publisher does not contain any
        if (testData.getUser() == null) {
            new NestedCreateUserPage(pxDriver)
                    .createInstance(testData)
                    .saveInstance(testData);
            // check presence in table
            usersPage.checkCreatedInstanceByName(testData.getEmail(), 0, true);
        }
        // check login as
        usersPage.checkLoginAs(testData, publisher);
    }
}