package tests.objects.offers;

import configuration.dataproviders.OfferDataProvider;
import dto.CheckTestData;
import org.testng.annotations.Test;
import px.objects.offers.OfferTestData;
import px.objects.offers.pages.EditOffersPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/24/2016.
 */
public class EditOffersTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;
//    private String url = super.url = "http://rvmd-10880-px.stagingrevi.com/";

    @Test(dataProvider = "editOfferData", dataProviderClass = OfferDataProvider.class)
    public void editOfferWithPositiveData(OfferTestData oldData, OfferTestData newData) {
        pxDriver.goToURL(super.url + "offers/" + oldData.getId() + "/edit/generalInfo/");
        login();
        new EditOffersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData)
                .saveInstance(newData);
        CheckTestData.checkEditedOffer(newData);
    }

    @Test(dataProvider = "editNegativeOfferData", dataProviderClass = OfferDataProvider.class)
    public void editOfferWithNegativeData(OfferTestData oldData, OfferTestData newData) {
        pxDriver.goToURL(super.url + "offers/" + oldData.getId() + "/edit/generalInfo/");
        login();
        new EditOffersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData)
                .saveInstance(newData);
    }
}