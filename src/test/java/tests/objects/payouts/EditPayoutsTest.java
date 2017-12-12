package tests.objects.payouts;

import configuration.dataproviders.PayoutDataProvider;
import org.testng.annotations.Test;
import px.objects.payouts.OffersPayoutTestData;
import px.objects.payouts.PublishersPayoutTestData;
import px.objects.payouts.pages.CreatePayoutsPage;
import px.objects.payouts.pages.EditPayoutsPage;
import px.objects.payouts.pages.PayoutsPage;
import tests.LoginTest;

import static px.objects.offers.OffersPageLocators.OFFER_PAYOUTS_ITEM;

/**
 * Created by kgr on 10/24/2016.
 */
public class EditPayoutsTest extends LoginTest {
    {
        this.allowDefaultLogin = false;
        this.url = "http://acceptance-ui.stagingpx.com/";
    }

    @Test(dataProvider = "editOfferPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void editOfferPayoutWithPositiveData(OffersPayoutTestData oldData, OffersPayoutTestData newData) {
        pxDriver.goToURL(url + "offers/" + oldData.getOfferID() + "/edit/generalInfo/");
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
        payoutsPage.waitPageIsLoaded();
        createPayoutsPage.navigateToItem(OFFER_PAYOUTS_ITEM);
        payoutsPage.waitPageIsLoaded();
        // if there is no any payout - create it first
        if (!oldData.isAnyPayout()) {
            payoutsPage.fillInPage();
            createPayoutsPage.checkDefaultValues()
                    .createInstance(oldData)
                    .saveInstance(oldData);
            payoutsPage.checkCreatedInstance(oldData);
        }
        // navigate to edit payout page
        payoutsPage.clickAction(oldData, "Edit");
        EditPayoutsPage editPayoutsPage = new EditPayoutsPage(pxDriver)
//                .checkDefaultValues(oldData)
                .editInstance(newData)
                .saveInstance(newData);
        payoutsPage.waitPageIsLoaded();
        // check edited instance
        editPayoutsPage.checkDefaultValues(newData);
    }

    @Test(dataProvider = "editNegativeOfferPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void editOfferPayoutWithNegativeData(OffersPayoutTestData oldData, OffersPayoutTestData newData) {
        pxDriver.goToURL(url + "offers/" + oldData.getOfferID() + "/edit/generalInfo/");
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
        payoutsPage.waitPageIsLoaded();
        createPayoutsPage.navigateToItem(OFFER_PAYOUTS_ITEM);
        payoutsPage.waitPageIsLoaded();
        // if there is no any payout - create it first
        if (!oldData.isAnyPayout()) {
            payoutsPage.fillInPage();
            createPayoutsPage.checkDefaultValues()
                    .createInstance(oldData)
                    .saveInstance(oldData);
            payoutsPage.checkCreatedInstance(oldData);
        }
        // navigate to edit payout page
        payoutsPage.clickAction(oldData, "Edit");
        new EditPayoutsPage(pxDriver)
//                .checkDefaultValues(oldData)
                .editInstance(newData)
                .saveInstance(newData);
    }

    @Test(dataProvider = "editPublisherPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void editPublisherPayoutWithPositiveData(PublishersPayoutTestData oldData, PublishersPayoutTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getPublisherGUID());
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        payoutsPage.navigateToPublisherPayout();
        // if there is no any payout - create it first
        if (!oldData.isAnyPayout()) {
            payoutsPage.expandPublisherPayoutCreateForm();
            CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
            createPayoutsPage.createInstance(oldData);
            // collapse create form
            payoutsPage.collapsePublisherPayoutCreateForm();
            // perhaps another sort of validation
            payoutsPage.checkCreatedInstance(oldData);
        }
        // edit
        payoutsPage.clickAction(oldData, "Edit");
        payoutsPage.waitPageIsLoaded();
        EditPayoutsPage editPayoutsPage = new EditPayoutsPage(pxDriver)
//                .checkDefaultValues(oldData)
                .editInstance(newData)
                .saveInstance(newData);
        payoutsPage.waitPageIsLoaded();
        // check edited instance
        // or check table instead
        payoutsPage.clickAction(oldData, "Edit");
        payoutsPage.waitPageIsLoaded();
        editPayoutsPage.checkDefaultValues(newData);
    }

    @Test(dataProvider = "editNegativePublisherPayoutData", dataProviderClass = PayoutDataProvider.class)
    public void editPublisherPayoutWithNegativeData(PublishersPayoutTestData oldData, PublishersPayoutTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getPublisherGUID());
        login();
        PayoutsPage payoutsPage = new PayoutsPage(pxDriver);
        payoutsPage.navigateToPublisherPayout();
        // if there is no any payout - create it first
        if (!oldData.isAnyPayout()) {
            payoutsPage.expandPublisherPayoutCreateForm();
            CreatePayoutsPage createPayoutsPage = new CreatePayoutsPage(pxDriver);
            createPayoutsPage.createInstance(oldData);
            // collapse create form
            payoutsPage.collapsePublisherPayoutCreateForm();
            // perhaps another sort of validation
            payoutsPage.checkCreatedInstance(oldData);
        }
        // edit
        payoutsPage.clickAction(oldData, "Edit");
        payoutsPage.waitPageIsLoaded();
        new EditPayoutsPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(newData)
                .saveInstance(newData);
    }
}
