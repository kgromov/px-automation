package tests.objects.users.publisher;

import configuration.dataproviders.PublisherDataProvider;
import org.testng.annotations.Test;
import px.objects.publishers.pages.EditPublishersPage;
import px.objects.users.UsersData;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.USER_MANAGEMENT_ITEM;

/**
 * Created by kgr on 8/15/2017.
 */
public class PublisherUsersOverviewTest extends LoginTest {

    @Test(dataProvider = "publisherUsersOverviewData", dataProviderClass = PublisherDataProvider.class)
    public void publisherUsersOverview(UsersData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getParent().getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        UsersPage usersPage = new UsersPage(pxDriver);
        usersPage.setTable(USER_CONTAINER);
        // check all rows
        usersPage.checkAllCells(testData);
    }
}
