package tests.objects.credSets;

import org.testng.annotations.Test;
import px.objects.credSets.CredSetsOverviewData;
import px.objects.credSets.pages.CredSetsOverviewPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/30/2017.
 */
public class CredSetsOverviewTest extends LoginTest {

    @Test
    public void checkCredSetsOverview() {
        CredSetsOverviewData overviewData = new CredSetsOverviewData();
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // check all cells
        credSetsPage.checkAllCells(overviewData);
    }

}