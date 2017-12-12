package tests.reports.rerun;

import configuration.dataproviders.RerunManagementDataProvider;
import org.testng.annotations.Test;
import px.reports.rerun.RerunOverviewTestData;
import px.reports.rerun.RerunTaskTestData;
import px.reports.rerun.pages.RerunsPage;
import tests.LoginTest;

/**
 * Created by konstantin on 05.10.2017.
 */
public class RerunOverviewTest extends LoginTest {

    @Test
    public void checkRerunOverview() {
        RerunOverviewTestData testData = new RerunOverviewTestData();
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // check all cells
        rerunsPage.checkAllCells(testData);
    }

    @Test(dataProvider = "deleteTask", dataProviderClass = RerunManagementDataProvider.class)
    public void deleteRerunTask(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        rerunsPage.deleteTask(testData)
                .checkTaskAbsence(testData);
    }
}
