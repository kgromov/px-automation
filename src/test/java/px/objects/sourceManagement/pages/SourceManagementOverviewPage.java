package px.objects.sourceManagement.pages;

import configuration.browser.PXDriver;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.objects.sourceManagement.SourceManagementTestData;
import px.reports.ReportTestData;
import px.reports.ReportsPage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.ElementLocators.ACTIONS_CELL;
import static px.reports.outbound.OutboundTransactionsColumnsEnum.SOURCE_ID;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementOverviewPage extends ReportsPage implements Actions {

    public SourceManagementOverviewPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public SourceManagementOverviewPage deleteSource(String sourceId) {
        // does not work cause of fucking logic inside cell as HTML
        List<String> sources = tableElement.getColumnCellsText(SOURCE_ID.getValue());
        int rowIndex = sources.indexOf(sourceId);
        assertThat(String.format("SourceId '%s' is not found in sources overview", sourceId), rowIndex != -1);
        WebElement cell = tableElement.getCellAt(rowIndex + 1, tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL)) + 1);
        cell.click();
     /*   WebElement cell = findCellByValue(tableElement, testData.getSourceID());
        cell.click();*/
        confirm(true);
        waitPageIsLoaded();
        return this;
    }

    public SourceManagementOverviewPage checkSourcePresence(SourceManagementTestData testData) {
        assertThat(String.format("Source with name '%s' is absent in campaign's source overview", testData.getSourceID()),
                tableElement.getColumnCellsText(SOURCE_ID.getValue()).contains(testData.getSourceID())
                /*tableElement.isCellPresentByText(testData.getSourceID(),
                        getHeaderIndex(tableElement, SourceManagementColumnsEnum.SOURCE_ID.getValue()) + 1)*/
        );
        return this;
    }

    public SourceManagementOverviewPage checkSourceAbsence(SourceManagementTestData testData) {
        assertThat(String.format("Source with filter name '%s' is present in campaign's source overview", testData.getSourceID()),
                !tableElement.getColumnCellsText(SOURCE_ID.getValue()).contains(testData.getSourceID())
                /*!tableElement.isCellPresentByText(testData.getSourceID(),
                        getHeaderIndex(tableElement, SourceManagementColumnsEnum.SOURCE_ID.getValue()) + 2)*/
        );
        return this;
    }

    public SourceManagementOverviewPage checkAllCells(ReportTestData testData) {
        checkCellsData(testData, testData.getAllRowsArray());
        return this;
    }
}
