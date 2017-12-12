package px.objects.buyers.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import pages.OverviewPage;

import static pages.locators.AdminPageLocators.BUYERS_LINK;

/**
 * Created by kgr on 10/19/2016.
 */
public class BuyersPage extends OverviewPage {

    public BuyersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public BuyersPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(BUYERS_LINK);
        super.checkPage();
        return this;
    }
}