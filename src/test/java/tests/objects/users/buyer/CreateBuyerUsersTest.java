package tests.objects.users.buyer;

import configuration.dataproviders.BuyerDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import pages.OverviewPage;
import px.objects.buyers.pages.EditBuyersPage;
import px.objects.users.UserTestData;
import px.objects.users.pages.NestedCreateUserPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.buyers.BuyersPageLocators.USERS_ITEM;

/**
 * Created by konstantin on 21.10.2016.
 */
public class CreateBuyerUsersTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "createPositiveUserData", dataProviderClass = BuyerDataProvider.class)
    public void createBuyerUserWithPositiveData(UserTestData testData, ObjectIdentityData buyer) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        // create user
        new NestedCreateUserPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
        // check presence in table
        new OverviewPage(pxDriver) {
        }.setTable(USER_CONTAINER)
                .checkCreatedInstanceByName(testData.getEmail(), 0, true);
    }

    @Test(dataProvider = "createNegativeUserData", dataProviderClass = BuyerDataProvider.class)
    public void createBuyerUserWithNegativeData(UserTestData testData, ObjectIdentityData buyer) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // navigate to users section
        editBuyersPage.navigateToItem(USERS_ITEM);
        // create user
        new NestedCreateUserPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
    }
}
