package tests.reports.rerun;

import configuration.dataproviders.RerunManagementDataProvider;
import org.testng.annotations.Test;
import px.reports.rerun.RerunTaskTestData;
import px.reports.rerun.pages.CreateTaskPage;
import px.reports.rerun.pages.RerunsPage;
import tests.LoginTest;

/**
 * Created by konstantin on 06.10.2017.
 */
public class EditRerunTaskByFilterTest extends LoginTest {


    @Test(dataProvider = "createTaskWithPositiveData", dataProviderClass = RerunManagementDataProvider.class)
    public void createTaskWithClonedFilterData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        CreateTaskPage taskPage = rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter());
        // set only task
        taskPage.cloneFilter()
                .createInstance(testData)
                .saveInstance()
                .checkErrors(testData);
        // check that task successfully created
        rerunsPage.checkTaskPresence(testData);
    }

    @Test(dataProvider = "createTaskWithPositiveData", dataProviderClass = RerunManagementDataProvider.class)
    public void createTaskWithEditExistedData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        CreateTaskPage taskPage = rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter());
        // set only task
        taskPage.editFilter()
                .createInstance(testData)
                .saveInstance()
                .checkErrors(testData);
        // check that task successfully created
        rerunsPage.checkTaskPresence(testData.getFilter().getName());
    }
}
