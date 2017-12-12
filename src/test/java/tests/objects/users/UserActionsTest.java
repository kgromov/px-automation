package tests.objects.users;

import configuration.dataproviders.UsersDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import pages.LoginPage;
import px.objects.users.UserRoleTestData;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

/**
 * Created by kgr on 8/15/2017.
 */
public class UserActionsTest extends LoginTest {

    @Test(dataProvider = "deleteUserData", dataProviderClass = UsersDataProvider.class)
    public void deleteUser(ObjectIdentityData user) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        // delete user
        usersPage.deleteUser(user.getName())
                // check changed status
                .checkUserIsDeleted(user.getName());
    }

    @Test(dataProvider = "resetUserPasswordPositiveData", dataProviderClass = UsersDataProvider.class)
    public void resetUserPasswordWithPositiveData(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        // invoke reset password popup
        usersPage.resetPassword(testData.getUser().getName())
                // reset password
                .resetPassword(testData);
        // check that ability to login with changed credentials
        logout();
        new LoginPage(pxDriver).loginAs(testData);
        logout();
    }

    @Test(dataProvider = "resetUserPasswordNegativeData", dataProviderClass = UsersDataProvider.class)
    public void resetUserPasswordWithNegativeData(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        // invoke reset password popup
        usersPage.resetPassword(testData.getUser().getName())
                // reset password
                .resetPassword(testData);
    }
}
