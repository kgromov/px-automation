package px.reports.rerun.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import elements.SectionTitleElement;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.reports.ReportsPage;
import px.reports.rerun.RerunOverviewTestData;
import px.reports.rerun.RerunTaskTestData;

import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.AdminPageLocators.RERUN_MANAGEMENT_LINK;
import static px.objects.pixels.PixelPageLocators.DELETE_LINK;
import static px.reports.rerun.RerunManagementColumnsEnum.NAME;
import static px.reports.rerun.RerunPageLocators.PLANNED_TASKS_CONTAINER;
import static px.reports.rerun.RerunPageLocators.SCHEDULE_BUTTON;

/**
 * Created by konstantin on 05.10.2017.
 */
public class RerunsPage extends ReportsPage implements Actions {

    public RerunsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public RerunsPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(RERUN_MANAGEMENT_LINK);
        waitPageIsLoaded();
        expandTable();
        displayAllTableColumns();
       /* super.checkPage();
        expandTable();*/
        return this;
    }

    private RerunsPage expandTable() {
        SectionTitleElement titleElement = new SectionTitleElement(helper.getElement(PLANNED_TASKS_CONTAINER));
        if (titleElement.isSectionCollapsed()) titleElement.expandSection();
        return this;
    }

    public RerunsPage deleteTask(RerunTaskTestData testData) {
        log.info(String.format("Delete task by filter name '%s'", testData.getName()));
//        WebElement cell = findCellByValue(tableElement, testData.getName());
        WebElement cell = findCellByValue(tableElement, testData.getName());
        helper.getElement(cell, DELETE_LINK).click();
        confirm(true);
        waitPageIsLoaded();
        return this;
    }

    public CreateTaskPage scheduleNewTask() {
        helper.waitUntilDisplayed(SCHEDULE_BUTTON);
        helper.getElement(SCHEDULE_BUTTON).click();
        waitPageIsLoaded();
        return new CreateTaskPage(pxDriver);
    }

    public RerunsPage checkTaskPresence(String filterName) {
        assertThat(String.format("Rerun task with filter name '%s' is present in rerun overview", filterName),
                tableElement.isCellPresentByText(filterName, getHeaderIndex(tableElement, NAME.getValue()) + 1));
        return this;
    }

    public RerunsPage checkTaskPresence(RerunTaskTestData testData) {
        assertThat(String.format("Rerun task with filter name '%s' is present in rerun overview", testData.getName()),
                tableElement.isCellPresentByText(testData.getName(), getHeaderIndex(tableElement, NAME.getValue()) + 1));
        return this;
    }

    public RerunsPage checkTaskAbsence(RerunTaskTestData testData) {
        assertThat(String.format("Rerun task with filter name '%s' is present but should be absent", testData.getName()),
                !tableElement.isCellPresentByText(testData.getName(), getHeaderIndex(tableElement, NAME.getValue()) + 1));
        return this;
    }

    public RerunsPage checkAllCells(RerunOverviewTestData testData) {
//        checkCellsData(testData, testData.getReruns()); // till no pagination
        checkCellsData(testData.getAllRowsArray(), HTMLHelper.getTableCells(tableElement), testData.getFields());
        return this;
    }
}
