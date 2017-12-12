package px.objects.customRights.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import pages.groups.Actions;
import px.objects.InstancesTestData;
import px.objects.customRights.CustomRightsColumnsEnum;
import px.reports.ReportTestData;
import px.reports.ReportsPage;

import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.AdminPageLocators.CUSTOM_RIGHTS_LINK;
import static px.objects.customRights.CustomRightsLocators.CREATE_BUTTON;

/**
 * Created by kgr on 11/8/2017.
 */
public class CustomRightsOverviewPage extends ReportsPage implements Actions {

    public CustomRightsOverviewPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CustomRightsOverviewPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(CUSTOM_RIGHTS_LINK);
        waitPageIsLoaded();
        displayAllTableColumns();
        return this;
    }

    public CreateCustomRightPage createCreateCustomRight(InstancesTestData testData) {
        helper.waitUntilDisplayed(CREATE_BUTTON);
        helper.click(CREATE_BUTTON);
        waitPageIsLoaded();
        return new CreateCustomRightPage(pxDriver);
    }

    public CustomRightsOverviewPage checkCredSetPresence(InstancesTestData testData) {
        filterInstanceInTable(testData.getName());
        assertThat(String.format("Rerun task with filter name '%s' is present in rerun overview", testData),
                tableElement.isCellPresentByText(testData.getName(),
                        getHeaderIndex(tableElement, CustomRightsColumnsEnum.NAME.getValue()) + 1));
        return this;
    }

    public CustomRightsOverviewPage checkAllCells(ReportTestData testData) {
        checkCellsData(testData, testData.getAllRowsArray());
        return this;
    }
}
