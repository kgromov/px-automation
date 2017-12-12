package tests.objects.users;

import configuration.dataproviders.UsersDataProvider;
import org.testng.annotations.Test;
import px.objects.users.UserRoleTestData;
import px.objects.users.pages.CreateUserPage;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

/**
 * Created by kgr on 8/15/2017.
 */
public class CreateUsersTest extends LoginTest {

    @Test(dataProvider = "createUserPositiveData", dataProviderClass = UsersDataProvider.class)
    public void createUserWithPositiveData(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        usersPage.fillInPage();
        new CreateUserPage(pxDriver)
                .createInstance(testData)
                .setUserRole(testData)
                .saveInstance(testData);
        // check presence in table
        usersPage.checkCreatedInstanceByName(testData.getEmail(), 0, true);
    }

    @Test(dataProvider = "createUserNegativeData", dataProviderClass = UsersDataProvider.class)
    public void createUserWithNegativeData(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        usersPage.fillInPage();
        new CreateUserPage(pxDriver)
                .createInstance(testData)
                .setUserRole(testData)
                .saveInstance(testData);
    }
}
