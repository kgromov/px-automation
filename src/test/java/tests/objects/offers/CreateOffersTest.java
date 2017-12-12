package tests.objects.offers;

import configuration.dataproviders.OfferDataProvider;
import org.testng.annotations.Test;
import px.objects.offers.OfferTestData;
import px.objects.offers.pages.CreateOffersPage;
import px.objects.offers.pages.OffersPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/24/2016.
 */
public class CreateOffersTest extends LoginTest {

    @Test(dataProvider = "positiveOfferData", dataProviderClass = OfferDataProvider.class)
    public void createOfferWithPositiveData(OfferTestData testData) {
        OffersPage offersPage = new OffersPage(pxDriver);
        offersPage.navigateToObjects();
        offersPage.fillInPage();
        new CreateOffersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
        // check created in offers table -> search by name
        offersPage.checkCreatedInstanceByName(testData);
    }

    @Test(dataProvider = "negativeOfferData", dataProviderClass = OfferDataProvider.class)
    public void createOfferWithNegativeData(OfferTestData testData) {
        OffersPage offersPage = new OffersPage(pxDriver);
        offersPage.navigateToObjects();
        offersPage.fillInPage();
        new CreateOffersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
    }
}
