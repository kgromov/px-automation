package tests.objects.users;

import org.testng.annotations.Test;
import px.objects.users.UsersData;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

/**
 * Created by kgr on 8/15/2017.
 */
public class UsersOverviewTest extends LoginTest {

    @Test//(dataProvider = "subIdOverviewData", dataProviderClass = SubIdDataProvider.class)
    public void checkUsersOverview() {
        UsersData testData = new UsersData();
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.navigateToObjects();
        // check all rows
        usersPage.checkAllCells(testData);
    }
}
