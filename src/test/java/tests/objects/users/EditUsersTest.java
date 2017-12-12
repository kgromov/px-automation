package tests.objects.users;

import configuration.dataproviders.UsersDataProvider;
import org.testng.annotations.Test;
import px.objects.users.UserRoleTestData;
import px.objects.users.pages.UserPreviewPage;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

/**
 * Created by kgr on 8/15/2017.
 */
public class EditUsersTest extends LoginTest {

    @Test(dataProvider = "editUserPositiveData", dataProviderClass = UsersDataProvider.class)
    public void editUserWithPositiveData(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects()
                // edit user from users overview
                .editUser(testData)
                .editInstance(testData)
                .setUserRole(testData)
                .saveInstance(testData);
        // check edited data
        usersPage.navigateToObjects()
                .editUser(testData);
        if (!testData.isUserDeleted())
            new UserPreviewPage(pxDriver).checkPreview(testData.asMap());
    }

    @Test(dataProvider = "editUserNegativeData", dataProviderClass = UsersDataProvider.class)
    public void editUserWithNegativeData(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        // edit user from users overview
        usersPage.editUser(testData)
                .editInstance(testData)
                .setUserRole(testData)
                .saveInstance(testData);
        ;
    }

    @Test(dataProvider = "userPreviewData", dataProviderClass = UsersDataProvider.class)
    public void userPreview(UserRoleTestData testData) {
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        // edit user from users overview
        usersPage.editUser(testData);
        new UserPreviewPage(pxDriver).checkPreview(testData.getDetailsMap());
    }
}
