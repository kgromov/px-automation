package tests.objects.pixels;

import configuration.dataproviders.PixelDataProvider;
import org.testng.annotations.Test;
import px.objects.pixels.pages.CreatePixelsPage;
import px.objects.pixels.PixelTestData;
import px.objects.pixels.pages.PixelsPage;
import tests.LoginTest;

import static px.objects.publishers.PublishersPageLocators.OFFER_PIXELS_ITEM;

/**
 * Created by kgr on 5/3/2017.
 */
public class CreatePixelsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "createPixelData", dataProviderClass = PixelDataProvider.class)
    public void createPixelTestWithPositiveData(PixelTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisher().getGuid());
        login();
        PixelsPage pixelsPage = new PixelsPage(pxDriver);
        CreatePixelsPage createPixelsPage = new CreatePixelsPage(pxDriver);
        pixelsPage.waitPageIsLoaded();
        // create user
        createPixelsPage.navigateToItem(OFFER_PIXELS_ITEM);
        createPixelsPage
                .createInstance(testData)
                .saveInstance(testData);
        // check presence in table
        pixelsPage.setTableElement();
        pixelsPage.checkCreatedInstanceByName(testData.getOfferName(), 1, false);
        // check preview popup
        pixelsPage.checkPixelPreview(testData);
    }

    @Test(dataProvider = "createPixelNegativeData", dataProviderClass = PixelDataProvider.class)
    public void createPixelWithNegativeDataTest(PixelTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisher().getGuid());
        login();
        CreatePixelsPage createPixelsPage = new CreatePixelsPage(pxDriver);
        createPixelsPage.waitPageIsLoaded();
        // create user
        createPixelsPage.navigateToItem(OFFER_PIXELS_ITEM);
        createPixelsPage
                .createInstance(testData)
                .saveInstance(testData);
    }
}
