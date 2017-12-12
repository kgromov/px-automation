package px.objects.subIds.pages;

import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.reports.ReportsPage;
import px.objects.subIds.SubIdsPreviewTestData;

/**
 * Created by kgr on 7/14/2017.
 */
public class SubIdsPage extends ReportsPage implements Actions {

    public SubIdsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditSubIdPage editSubId(ObjectIdentityData subId) {
        setItemPerPage(100);
        WebElement cell = findCellByValue(tableElement, subId.getId());
        cell.click();
        return new EditSubIdPage(pxDriver);
    }

    public SubIdsPage checkAllCells(SubIdsPreviewTestData testData) {
        checkCellsData(testData, testData.getSubIDs());
        return this;
    }
}
