package px.objects.brokers.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import px.objects.InstancesTestData;
import pages.OverviewPage;
import pages.groups.Actions;

import static pages.locators.AdminPageLocators.BROKERS_LINK;
import static px.objects.brokers.BrokersPageLocators.DELETE_ITEM;

/**
 * Created by kgr on 10/19/2016.
 */
public class BrokersPage extends OverviewPage implements Actions {

    public BrokersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public BrokersPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(BROKERS_LINK);
        super.checkPage();
        return this;
    }

    public BrokersPage deleteBroker(InstancesTestData testData){
        filterInstanceInTable(testData.getName());
        findAndEditByName(testData);
        setAction(DELETE_ITEM);
        confirm(true);
        return this;
    }
}