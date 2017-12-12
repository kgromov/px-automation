package tests.objects.users.buyer;

import configuration.dataproviders.BuyerDataProvider;
import org.testng.annotations.Test;
import px.objects.buyers.pages.EditBuyersPage;
import px.objects.users.UsersData;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.buyers.BuyersPageLocators.USERS_ITEM;

/**
 * Created by kgr on 8/15/2017.
 */
public class BuyerUsersOverviewTest extends LoginTest {

    @Test(dataProvider = "buyerUsersOverviewData", dataProviderClass = BuyerDataProvider.class)
    public void buyerUsersOverview(UsersData testData) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + testData.getParent().getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // check all rows
        usersPage.checkAllCells(testData);
    }
}
