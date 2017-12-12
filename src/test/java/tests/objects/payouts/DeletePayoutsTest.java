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
public class DeletePayoutsTest extends LoginTest {
    {
        this.allowDefaultLogin = false;
        this.url = "http://acceptance-ui.stagingpx.com/";
    }

    @Test(dataProvider = "deleteOfferPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void deleteOfferPayout(OffersPayoutTestData testData) {
        pxDriver.goToURL(url + "offers/" + testData.getOfferID() + "/edit/generalInfo/");
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
        payoutsPage.waitPageIsLoaded();
        createPayoutsPage.navigateToItem(OFFER_PAYOUTS_ITEM);
        payoutsPage.waitPageIsLoaded();
        // if there are no payouts in chosen offer (cause such sort of requests is missed)
        // change to isAnyAutomationPayout
        if (!testData.isAnyPayout()) {
            payoutsPage.fillInPage();
            createPayoutsPage.checkDefaultValues();
            createPayoutsPage.createInstance(testData);
            payoutsPage.checkCreatedInstance(testData);
            testData.addToExistedPayouts(testData.getPublisherID());
        }
        payoutsPage.deletePayout(testData);
        // perhaps another sort of validation
        payoutsPage.checkDeletedInstanceByName(testData);
    }

    @Test(dataProvider = "deletePublisherPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void deletePublisherPayout(PublishersPayoutTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisherGUID());
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        payoutsPage.navigateToPublisherPayout();
        // if there are no payouts in chosen publisher (cause such sort of requests is missed)
        // change to isAnyAutomationPayout
        if (!testData.isAnyPayout()) {
            payoutsPage.expandPublisherPayoutCreateForm();
            CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
            createPayoutsPage.createInstance(testData);
            // collapse create form
            payoutsPage.collapsePublisherPayoutCreateForm();
            // perhaps another sort of validation
            payoutsPage.checkCreatedInstance(testData);
            testData.addToExistedPayouts(testData.getOfferID());
        }
        payoutsPage.deletePayout(testData);
        // perhaps another sort of validation
        payoutsPage.checkDeletedInstanceByName(testData);
    }
}