package tests.reports.rerun;

import configuration.dataproviders.RerunManagementDataProvider;
import org.testng.annotations.Test;
import px.reports.rerun.FilterDetailsTestData;
import px.reports.rerun.RerunTaskTestData;
import px.reports.rerun.pages.CreateTaskPage;
import px.reports.rerun.pages.EditTaskPage;
import px.reports.rerun.pages.RerunsPage;
import tests.LoginTest;

/**
 * Created by konstantin on 06.10.2017.
 */
public class CreateRerunTaskByFilterTest extends LoginTest {

    @Test(dataProvider = "filterPreviewData", dataProviderClass = RerunManagementDataProvider.class)
    public void checkFilterPreview(FilterDetailsTestData filterData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .setExistedFilter(filterData.getFilter());
        new EditTaskPage(pxDriver).checkFilterPreview(filterData);
    }

    @Test(dataProvider = "createTaskWithExistedFilterPositiveData", dataProviderClass = RerunManagementDataProvider.class)
    public void createTaskWithExistedFilterPositiveData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        CreateTaskPage taskPage = rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter())
                // set only task
                .setTaskData(testData)
                .saveInstance()
                .checkErrors(testData);
        // check that task successfully created
        rerunsPage.checkTaskPresence(testData.getFilter().getName());
    }

    @Test(dataProvider = "createTaskWithExistedFilterNegativeData", dataProviderClass = RerunManagementDataProvider.class)
    public void createTaskWithExistedFilterNegativeData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        CreateTaskPage taskPage = rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter())
                // set only task
                .setTaskData(testData)
                .saveInstance()
                .checkErrors(testData);
    }
}
