package tests.objects;

import configuration.dataproviders.PaginationDataProvider;
import configuration.dataproviders.SourceManagementDataProvider;
import configuration.dataproviders.SubIdDataProvider;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.testng.annotations.Test;
import pages.OverviewPage;
import pages.groups.PaginationStrategy;
import px.objects.buyers.pages.EditBuyersPage;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.offers.OffersPageLocators;
import px.objects.payouts.pages.PayoutsPage;
import px.objects.pixels.pages.PixelsPage;
import px.objects.publishers.pages.EditPublishersPage;
import px.objects.publishers.pages.PublishersPage;
import px.objects.sourceManagement.SourceManagementOverviewData;
import px.objects.sourceManagement.pages.SourceManagementOverviewPage;
import px.objects.subIds.SubIdsPreviewTestData;
import tests.LoginTest;

import static pages.locators.ContactInfoLocators.USER_CONTAINER;
import static px.objects.buyers.BuyersPageLocators.USERS_ITEM;
import static px.objects.offers.OffersPageLocators.OFFER_URLS_ITEM;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by konstantin on 21.10.2016.
 */
public class PxChildObjectsPaginationTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;
    private PaginationStrategy defaultStrategy = rows -> rows;

    @Test(dataProvider = "buyerUsersPaginationData", dataProviderClass = PaginationDataProvider.class)
    public void checkBuyerUsersPagination(ObjectIdentityData buyer, JSONArray buyerUsers) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + buyer.getGuid());
        login();
        EditBuyersPage editBuyersPage = new EditBuyersPage(pxDriver);
        editBuyersPage.waitPageIsLoaded();
        // select 'user' item
        editBuyersPage.navigateToItem(USERS_ITEM);
        new OverviewPage(pxDriver) {
        }.checkPagination(USER_CONTAINER, defaultStrategy, buyerUsers.length());
    }

    @Test(dataProvider = "publisherUsersPaginationData", dataProviderClass = PaginationDataProvider.class)
    public void checkPublisherUsersPagination(ObjectIdentityData publisher, JSONArray publisherUsers) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitPageIsLoaded();
        // create user
        editPublishersPage.navigateToItem(USER_MANAGEMENT_ITEM);
        new OverviewPage(pxDriver) {
        }.checkPagination(USER_CONTAINER, defaultStrategy, publisherUsers.length());
    }

    @Test(dataProvider = "publisherPixelsPaginationData", dataProviderClass = PaginationDataProvider.class)
    public void checkPublisherPixelsPagination(ObjectIdentityData publisher, JSONArray publisherPixels) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitPageIsLoaded();
        // create user
        editPublishersPage.navigateToItem(OFFER_PIXELS_ITEM);
        new PixelsPage(pxDriver).checkPagination(OFFER_PIXELS_CONTAINER, defaultStrategy, publisherPixels.length());
    }

    @Test(dataProvider = "publisherPayoutsPaginationData", dataProviderClass = PaginationDataProvider.class)
    public void checkPublisherPayoutsPagination(ObjectIdentityData publisher, JSONArray publisherPayouts) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + publisher.getGuid());
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitPageIsLoaded();
        // create user
        editPublishersPage.navigateToItem(OFFER_PAYOUTS_ITEM);
        new PayoutsPage(pxDriver).checkPagination(OFFER_PAYOUTS_CONTAINER, defaultStrategy, publisherPayouts.length());
    }

    @Test(dataProvider = "offerPayoutsPaginationData", dataProviderClass = PaginationDataProvider.class)
    public void checkOfferPayoutsPagination(ObjectIdentityData offer, JSONArray offerPayouts) {
        pxDriver.goToURL(url + "offers/" + offer.getId() + "/edit/generalInfo/");
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitPageIsLoaded();
        // create user
        editPublishersPage.navigateToItem(OffersPageLocators.OFFER_PAYOUTS_ITEM);
        editPublishersPage.waitPageIsLoaded();
        new PayoutsPage(pxDriver).checkPagination(defaultStrategy, offerPayouts.length());
    }

    @Test(dataProvider = "offerURLsPaginationData", dataProviderClass = PaginationDataProvider.class)
    public void checkOfferURLsPagination(ObjectIdentityData offer, JSONArray offerURLs) {
        pxDriver.goToURL(url + "offers/" + offer.getId() + "/edit/generalInfo/");
        login();
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        editPublishersPage.waitPageIsLoaded();
        // create user
        editPublishersPage.navigateToItem(OFFER_URLS_ITEM);
        editPublishersPage.waitPageIsLoaded();
        new PayoutsPage(pxDriver).checkPagination(defaultStrategy, offerURLs.length());
    }

    @Test(dataProvider = "subIdOverviewData", dataProviderClass = SubIdDataProvider.class)
    public void checkSubIdsPagination(SubIdsPreviewTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        pxDriver.goToURL(url + "admin/publishers/" + testData.getPublisher().getGuid() + "/subids");
        login();
        // subIds management
        publishersPage.checkPagination(testData, testData.getSubIDs().length());
    }

    @Test(dataProvider = "sourceManagementOverviewData", dataProviderClass = SourceManagementDataProvider.class)
    public void checkSourceManagementPagination(SourceManagementOverviewData testData) {
        // navigate to target campaign with source
        new CampaignsPage(pxDriver)
                .navigateToObjects()
                .navigateToSourceManagement(testData.getCampaign());
        // source management
        new SourceManagementOverviewPage(pxDriver).checkPagination(testData, testData.getAllRowsArray().length());
    }
}