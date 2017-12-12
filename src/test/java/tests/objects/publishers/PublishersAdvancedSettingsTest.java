package tests.objects.publishers;

import configuration.dataproviders.PublisherDataProvider;
import org.testng.annotations.Test;
import pages.OverviewPage;
import px.objects.access.AccessPage;
import px.objects.access.AccessTestData;
import px.objects.publishers.PublisherTestData;
import px.objects.publishers.pages.EditPublishersPage;
import px.objects.users.pages.NestedCreateUserPage;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.OFFER_ACCESS_ITEM;
import static px.objects.publishers.PublishersPageLocators.USER_MANAGEMENT_ITEM;

/**
 * Created by kgr on 1/26/2017.
 */
public class PublishersAdvancedSettingsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;
//    private String url = super.url = "http://sprint_99-ui.stagingpx.com/";

    @Test(dataProvider = "checkOffersAccess", dataProviderClass = PublisherDataProvider.class)
    public void checkOffersAccessTest(AccessTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getParent().getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitPageIsLoaded();
        // check offers access
        editPublishersPage.navigateToItem(OFFER_ACCESS_ITEM);
        AccessPage accessPage = new AccessPage(pxDriver);
        accessPage.waitPageLoaded();
        accessPage.checkAccessStatuses(testData);
    }

}