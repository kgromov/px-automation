package tests.objects.roles;

import org.testng.annotations.Test;
import pages.DashboardPage;
import tests.LoginTest;

/**
 * Created by kgr on 6/23/2017.
 */
public class CheckUserRoleTest extends LoginTest {

    @Test
    public void checkAccessibilityByUserRoleTest(){
        DashboardPage dashboardPage = new DashboardPage(pxDriver);
        dashboardPage.checkUserAccess();
    }
}