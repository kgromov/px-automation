package px.objects.credSets.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.objects.InstancesTestData;
import px.objects.credSets.CredSetColumnsEnum;
import px.objects.credSets.CredSetTestData;
import px.reports.ReportTestData;
import px.reports.ReportsPage;

import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.AdminPageLocators.CRED_SET_LINK;
import static px.objects.credSets.CredSetLocators.CREATE_BUTTON;

/**
 * Created by kgr on 10/30/2017.
 */
public class CredSetsOverviewPage extends ReportsPage implements Actions {

    public CredSetsOverviewPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CredSetsOverviewPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(CRED_SET_LINK);
        waitPageIsLoaded();
        displayAllTableColumns();
        return this;
    }

    public CreateCredSetPage createCredSet() {
        helper.waitUntilDisplayed(CREATE_BUTTON);
        helper.click(CREATE_BUTTON);
        waitPageIsLoaded();
        return new CreateCredSetPage(pxDriver);
    }

    public CreateCredSetPage cloneCredSet(CredSetTestData testData) {
        filterInstanceInTable(testData.getName());
        WebElement cell = findCellByValue(tableElement, testData.getName());
        cell.click();
        setActionByItemText("Clone");
        return new CreateCredSetPage(pxDriver);
    }

    public EditCredSetPage editCredSet(CredSetTestData testData) {
        filterInstanceInTable(testData.getPrevName());
        WebElement cell = findCellByValue(tableElement, testData.getPrevName());
        cell.click();
        setActionByItemText("Edit");
        return new EditCredSetPage(pxDriver);
    }

    public CredSetsOverviewPage checkCredSetPresence(InstancesTestData testData) {
        filterInstanceInTable(testData.getName());
        assertThat(String.format("Rerun task with filter name '%s' is present in rerun overview", testData),
                tableElement.isCellPresentByText(testData.getName(),
                        getHeaderIndex(tableElement, CredSetColumnsEnum.NAME.getValue()) + 1));
        return this;
    }

    public CredSetsOverviewPage checkAllCells(ReportTestData testData) {
        checkCellsData(testData, testData.getAllRowsArray());
        return this;
    }
}
