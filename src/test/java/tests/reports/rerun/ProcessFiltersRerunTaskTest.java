package tests.reports.rerun;

import configuration.dataproviders.RerunManagementDataProvider;
import org.testng.annotations.Test;
import px.reports.rerun.ProcessedLeadTestData;
import px.reports.rerun.RerunTaskTestData;
import px.reports.rerun.RerunedLeadsReportTestData;
import px.reports.rerun.pages.RerunedLeadsPage;
import px.reports.rerun.pages.RerunsPage;
import tests.LoginTest;

/**
 * Created by konstantin on 06.10.2017.
 */
public class ProcessFiltersRerunTaskTest extends LoginTest {

    @Test(dataProvider = "processTaskExistedFilterData", dataProviderClass = RerunManagementDataProvider.class)
    public void processTaskFilterToLeadsPagination(RerunedLeadsReportTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter())
                .processFilters();
        // check that leads overview id filtered correctly
        rerunsPage.waitPageIsLoaded(150);
        // check select all
        if (testData.getItemsTotalCount() > 0)
            rerunsPage.checkSelectAllColumn();
        // processed leads table
        new RerunedLeadsPage(pxDriver).checkPagination(testData, testData.getItemsTotalCount());
    }

    @Test(dataProvider = "processTaskExistedFilterData", dataProviderClass = RerunManagementDataProvider.class)
    public void processTaskWithExistedFilter(RerunedLeadsReportTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter())
                .processFilters();
        // check that leads overview id filtered correctly
        rerunsPage.waitPageIsLoaded(150);
        // processed leads table
        new RerunedLeadsPage(pxDriver).checkAllCells(testData);
    }

    @Test(dataProvider = "createTaskWithPositiveData", dataProviderClass = RerunManagementDataProvider.class)
    public void processTaskFiltersWithPositiveData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .createInstance(testData)
                .processFilters()
                .checkErrors(testData);
        // set filter of supposed created
        testData.setFilterByNewTask();
        RerunedLeadsReportTestData reportData = new RerunedLeadsReportTestData(testData);
        // check that leads overview id filtered correctly
        rerunsPage.waitPageIsLoaded();
        // processed leads table
        new RerunedLeadsPage(pxDriver).checkAllCells(reportData);
    }

    @Test(dataProvider = "createTaskWithNegativeData", dataProviderClass = RerunManagementDataProvider.class)
    public void processTaskFiltersWithNegativeData(RerunTaskTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .createInstance(testData)
                .processFilters()
                .processFilters()
                .checkErrors(testData);
    }

    @Test(dataProvider = "removeProcessedLeadFromBatch", dataProviderClass = RerunManagementDataProvider.class)
    public void removeProcessedLeadFromBatch(ProcessedLeadTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter())
                .processFilters();
        // check that leads overview id filtered correctly
        rerunsPage.waitPageIsLoaded(150);
        // set max items per page cause there is no lookup
        rerunsPage.setItemPerPage(100);
        // remove from batch by actions item
        new RerunedLeadsPage(pxDriver).removeFromBatch(testData.getLead());
    }

    @Test(dataProvider = "removeMultipleProcessedLeadFromBatch", dataProviderClass = RerunManagementDataProvider.class)
    public void removeMultipleProcessedLeadFromBatch(ProcessedLeadTestData testData) {
        RerunsPage rerunsPage = new RerunsPage(pxDriver);
        rerunsPage.navigateToObjects();
        // click schedule and create new task
        rerunsPage.scheduleNewTask()
                .setExistedFilter(testData.getFilter())
                .processFilters();
        // check that leads overview id filtered correctly
        rerunsPage.waitPageIsLoaded(150);
        // set max items per page cause there is no lookup
        rerunsPage.setItemPerPage(100);
        // remove from batch with button
        new RerunedLeadsPage(pxDriver).removeFromBatch(testData.getLeadResponses());
    }

}