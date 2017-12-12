package px.objects.publishers.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.openqa.selenium.WebElement;
import pages.OverviewPage;
import pages.groups.Actions;
import pages.groups.Exportable;
import pages.groups.Searchable;
import pages.locators.AdminPageLocators;

/**
 * Created by kgr on 10/19/2016.
 */
public class PublishersPage extends OverviewPage implements Exportable, Searchable, Actions {

    public PublishersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public PublishersPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(AdminPageLocators.PUBLISHERS_LINK);
        super.checkPage();
        return new PublishersPage(pxDriver);
    }

    public void navigateToSubIdsPage(ObjectIdentityData publisher) {
        // filter by publisher name and click 'Manage subIDs'
        filterInstanceInTable(publisher.getName(), "Publisher");
        // click on 'Actions' and select 'Delete' option
        WebElement cell = findCellByValue(tableElement, publisher.getName());
        cell.click();
        setActionByItemText("Manage subIDs");
        waitPageIsLoaded();
    }
}
