package tests.objects.users.publisher;

import configuration.dataproviders.PublisherDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import pages.OverviewPage;
import px.objects.publishers.pages.EditPublishersPage;
import px.objects.users.UserTestData;
import px.objects.users.pages.NestedCreateUserPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.USER_MANAGEMENT_ITEM;

/**
 * Created by konstantin on 21.10.2016.
 */
public class CreatePublisherUsersTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "createPositiveUserData", dataProviderClass = PublisherDataProvider.class)
    public void createPublisherUserWithPositiveData(UserTestData testData, ObjectIdentityData publisher) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        new NestedCreateUserPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
        // check presence in table
        new OverviewPage(pxDriver) {
        }.setTable(USER_CONTAINER)
                .checkCreatedInstanceByName(testData.getEmail(), 0, true);
    }

    @Test(dataProvider = "createNegativeUserData", dataProviderClass = PublisherDataProvider.class)
    public void createPublisherUserWithNegativeData(UserTestData testData, ObjectIdentityData publisher) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitAllProgressBarsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        new NestedCreateUserPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
    }
}
