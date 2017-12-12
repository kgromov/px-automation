package tests.reports.rerun;

import configuration.dataproviders.RerunManagementDataProvider;
import org.testng.annotations.Test;
import px.reports.rerun.RerunTaskTestData;
import px.reports.rerun.pages.RerunsPage;
import tests.LoginTest;

/**
 * Created by konstantin on 06.10.2017.
 */
public class CreateRerunTaskTest extends LoginTest {

    @Test(dataProvider = "createTaskWithPositiveData", dataProviderClass = RerunManagementDataProvider.class)
    public void creteTaskWithPositiveData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .createInstance(testData)
                .saveInstance()
                .checkErrors(testData);
        // check that task successfully created
        rerunsPage.checkTaskPresence(testData);
    }

    @Test(dataProvider = "createTaskWithNegativeData", dataProviderClass = RerunManagementDataProvider.class)
    public void creteTaskWithNegativeData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .createInstance(testData)
                .saveInstance()
                .checkErrors(testData);
    }
}
