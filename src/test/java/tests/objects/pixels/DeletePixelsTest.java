package tests.objects.pixels;

import configuration.dataproviders.PixelDataProvider;
import org.testng.annotations.Test;
import px.objects.pixels.PixelTestData;
import px.objects.pixels.pages.CreatePixelsPage;
import px.objects.pixels.pages.PixelsPage;
import px.objects.publishers.pages.EditPublishersPage;
import tests.LoginTest;

import static px.objects.publishers.PublishersPageLocators.OFFER_PIXELS_ITEM;

/**
 * Created by kgr on 5/3/2017.
 */
public class DeletePixelsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "deletePixelData", dataProviderClass = PixelDataProvider.class)
    public void deletePixelTest(PixelTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisher().getGuid());
        login();
        PixelsPage pixelsPage = new PixelsPage(pxDriver);
        EditPublishersPage editPublishersPage = new EditPublishersPage(pxDriver);
        pixelsPage.waitPageIsLoaded();
        // create user
        editPublishersPage.navigateToItem(OFFER_PIXELS_ITEM);
        // if there is no any payout - create it first
        if (!testData.isAnyAvailablePixel()) {
            new CreatePixelsPage(pxDriver)
                    .createInstance(testData)
                    .saveInstance(testData);
            // check presence in table
            pixelsPage.setTableElement();
            pixelsPage.checkCreatedInstanceByName(testData.getOfferName(), 1, false);
            testData.setPixelObject(testData.getPublisher().getId(), testData.getOffer().getId());
//            pixelsPage.setPixelID(testData);
        }
        pixelsPage.deletePixel(testData);
    }

}