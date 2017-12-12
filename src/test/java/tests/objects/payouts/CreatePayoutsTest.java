package tests.objects.payouts;

import configuration.dataproviders.PayoutDataProvider;
import org.testng.annotations.Test;
import px.objects.payouts.OffersPayoutTestData;
import px.objects.payouts.PublishersPayoutTestData;
import px.objects.payouts.pages.CreatePayoutsPage;
import px.objects.payouts.pages.PayoutsPage;
import tests.LoginTest;

import static px.objects.offers.OffersPageLocators.OFFER_PAYOUTS_ITEM;

/**
 * Created by kgr on 10/24/2016.
 */
public class CreatePayoutsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "createOfferPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void createOfferPayoutWithPositiveData(OffersPayoutTestData testData) {
        pxDriver.goToURL(url + "offers/" + testData.getOfferID() + "/edit/generalInfo/");
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
        payoutsPage.waitPageIsLoaded();
        createPayoutsPage.navigateToItem(OFFER_PAYOUTS_ITEM);
        payoutsPage.waitPageIsLoaded();
        payoutsPage.fillInPage();
        // create itself
        createPayoutsPage.checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
        // perhaps another sort of validation
        payoutsPage.checkCreatedInstance(testData);
    }

    @Test(dataProvider = "createNegativeOfferPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void createOfferPayoutWithNegativeData(OffersPayoutTestData testData) {
        pxDriver.goToURL(url + "offers/" + testData.getOfferID() + "/edit/generalInfo/");
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
        payoutsPage.waitPageIsLoaded();
        createPayoutsPage.navigateToItem(OFFER_PAYOUTS_ITEM);
        payoutsPage.waitPageIsLoaded();
        payoutsPage.fillInPage();
        // create itself
        createPayoutsPage.checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
    }

    @Test(dataProvider = "createPublisherPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void createPublisherPayoutWithPositiveData(PublishersPayoutTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisherGUID());
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        payoutsPage.navigateToPublisherPayout();
        payoutsPage.expandPublisherPayoutCreateForm();
        // create itself
        new CreatePayoutsPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
        // collapse create form
        payoutsPage.collapsePublisherPayoutCreateForm();
        // perhaps another sort of validation
        payoutsPage.checkCreatedInstance(testData);
    }

    @Test(dataProvider = "createNegativePublisherPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void createPublisherPayoutWithNegativeData(PublishersPayoutTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisherGUID());
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        payoutsPage.navigateToPublisherPayout();
        payoutsPage.expandPublisherPayoutCreateForm();
        // create itself
        new CreatePayoutsPage(pxDriver)
                .createInstance(testData)
                .saveInstance(testData);
        // collapse create form
        payoutsPage.collapsePublisherPayoutCreateForm();
    }
}
