package tests.objects.offers;

import configuration.dataproviders.OfferDataProvider;
import org.testng.annotations.Test;
import pages.OverviewPage;
import px.objects.offers.OfferTestData;
import px.objects.offers.pages.EditOffersPage;
import px.objects.offers.pages.OfferURLsPage;
import tests.LoginTest;

import static px.objects.offers.OffersPageLocators.*;

/**
 * Created by kgr on 10/24/2016.
 */
public class OffersAdvancedSettingsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;
//    private String url = super.url = "http://rvmd-11986-ui.stagingpx.com/";

    @Test(dataProvider = "createOfferURLData", dataProviderClass = OfferDataProvider.class)
    public void createOfferURLTest(OfferTestData testData) {
        pxDriver.goToURL(super.url + "offers/" + testData.getId() + "/edit/generalInfo/");
        login();
        EditOffersPage editOffersPage = new EditOffersPage(pxDriver);
        editOffersPage.waitAllProgressBarsLoaded();
        editOffersPage.navigateToItem(OFFER_URLS_ITEM);
        // create offer url
        new OfferURLsPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
        // check presence in table
//        offerURLsPage.checkPagination(testData.getOfferURLCount() + 1);
        new OverviewPage(pxDriver) {
        }.checkCreatedInstanceByName(testData.getUrlName(), 1, false);
    }

    @Test(dataProvider = "assignGroupData", dataProviderClass = OfferDataProvider.class)
    public void checkOfferGroup(OfferTestData testData) {
        pxDriver.goToURL(super.url + "offers/" + testData.getId() + "/edit/generalInfo/");
        login();
        EditOffersPage editOffersPage = new EditOffersPage(pxDriver);
        editOffersPage.waitAllProgressBarsLoaded();
        editOffersPage.navigateToItem(OFFER_GROUPS_ITEM);
        // assign/check offer group
        editOffersPage.checkOfferGroup(testData);
    }

    @Test(dataProvider = "createTargetingData", dataProviderClass = OfferDataProvider.class)
    public void createOfferTargetingTest(OfferTestData testData) {
        pxDriver.goToURL(super.url + "offers/" + testData.getId() + "/edit/generalInfo/");
        login();
        EditOffersPage editOffersPage = new EditOffersPage(pxDriver);
        editOffersPage.waitPageIsLoaded();
        editOffersPage.navigateToItem(OFFER_TARGETING_ITEM);
        editOffersPage.waitPageIsLoaded();
        // create/check offer targeting
        editOffersPage.checkOfferTargeting(testData);
    }
}
