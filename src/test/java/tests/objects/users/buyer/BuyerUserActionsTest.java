package tests.objects.users.buyer;

import configuration.dataproviders.BuyerDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import pages.LoginPage;
import px.objects.buyers.pages.EditBuyersPage;
import px.objects.users.UserRoleTestData;
import px.objects.users.pages.NestedCreateUserPage;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.buyers.BuyersPageLocators.USERS_ITEM;

/**
 * Created by konstantin on 21.10.2016.
 */
public class BuyerUserActionsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "deleteUserData", dataProviderClass = BuyerDataProvider.class)
    public void deleteBuyerUser(UserRoleTestData testData, ObjectIdentityData buyer) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if buyer does not contain any
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

    @Test(dataProvider = "resetUserPasswordPositiveData", dataProviderClass = BuyerDataProvider.class)
    public void resetBuyerUserPasswordWithPositiveData(UserRoleTestData testData, ObjectIdentityData buyer) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if buyer does not contain any
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

    @Test(dataProvider = "resetUserPasswordNegativeData", dataProviderClass = BuyerDataProvider.class)
    public void resetBuyerUserPasswordWithNegativeData(UserRoleTestData testData, ObjectIdentityData buyer) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if buyer does not contain any
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

    @Test(dataProvider = "resetUserPasswordPositiveData", dataProviderClass = BuyerDataProvider.class)
    public void loginAsBuyerUser(UserRoleTestData testData, ObjectIdentityData buyer) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // create user if buyer does not contain any
        if (testData.getUser() == null) {
            new NestedCreateUserPage(pxDriver)
                    .createInstance(testData)
                    .saveInstance(testData);
            // check presence in table
            usersPage.checkCreatedInstanceByName(testData.getEmail(), 0, true);
        }
        // check login as
        usersPage.checkLoginAs(testData, buyer);
    }
}