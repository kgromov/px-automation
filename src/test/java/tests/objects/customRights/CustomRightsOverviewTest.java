package tests.objects.customRights;

import org.testng.annotations.Test;
import px.objects.customRights.CustomRightsOverviewData;
import px.objects.customRights.pages.CustomRightsOverviewPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/8/2017.
 */
public class CustomRightsOverviewTest extends LoginTest {

    @Test
    public void checkCustomRightsOverview() {
        CustomRightsOverviewData overviewData = new CustomRightsOverviewData();
        CustomRightsOverviewPage overviewPage = new CustomRightsOverviewPage(pxDriver);
        overviewPage.navigateToObjects();
        // check all cells
        overviewPage.checkAllCells(overviewData);
    }
}
