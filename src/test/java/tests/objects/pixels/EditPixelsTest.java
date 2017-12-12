package tests.objects.pixels;

import configuration.dataproviders.PixelDataProvider;
import org.testng.annotations.Test;
import px.objects.pixels.PixelTestData;
import px.objects.pixels.pages.CreatePixelsPage;
import px.objects.pixels.pages.EditPixelsPage;
import px.objects.pixels.pages.PixelsPage;
import px.objects.pixels.pages.PreviewPixelPage;
import tests.LoginTest;

import static px.objects.publishers.PublishersPageLocators.OFFER_PIXELS_ITEM;

/**
 * Created by kgr on 5/3/2017.
 */
public class EditPixelsTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "pixelDetailsData", dataProviderClass = PixelDataProvider.class)
    public void checkPixelDetailsTest(PixelTestData testData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + testData.getPublisher().getGuid());
        login();
        PreviewPixelPage previewPixelPage = new PreviewPixelPage(pxDriver);
        // bring into view pixel section
        previewPixelPage.navigateToItem(OFFER_PIXELS_ITEM);
        // navigate from publisher page to pixel details page
        previewPixelPage.testPixel(testData);
        // check pixel details data
        previewPixelPage.checkPixelDetails(testData);
        // check pixel test links
        previewPixelPage.checkLinks(testData);
    }

    @Test(dataProvider = "editPixelData", dataProviderClass = PixelDataProvider.class)
    public void editPixelWithPositiveDataTest(PixelTestData oldData, PixelTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getPublisher().getGuid());
        login();
        PreviewPixelPage previewPixelPage = new PreviewPixelPage(pxDriver);
        // bring into view pixel section
        previewPixelPage.navigateToItem(OFFER_PIXELS_ITEM);
        // if there is no any payout - create it first
        if (!oldData.isAnyAvailablePixel()) {
            CreatePixelsPage createPixelsPage = new CreatePixelsPage(pxDriver);
            createPixelsPage.createInstance(oldData).saveInstance(oldData);
            // check presence in table
            PixelsPage pixelsPage = new PixelsPage(pxDriver)
                    .setTableElement();
            pixelsPage.checkCreatedInstanceByName(oldData.getOfferName(), 1, false);
            oldData.setPixelObject(oldData.getPublisher().getId(), oldData.getOffer().getId());
//            pixelsPage.setPixelID(oldData);
        }
        // navigate from publisher page to pixel details page
        previewPixelPage.testPixel(oldData);
        // edit instance
        new EditPixelsPage(pxDriver)
                .editInstance(newData)
                .saveInstance(newData);
        // check pixel details data
        previewPixelPage.checkPixelDetails(newData);
        // check in pixels table as well - row by pixelID
    }

    @Test(dataProvider = "editPixelWithNegativeData", dataProviderClass = PixelDataProvider.class)
    public void editPixelWithNegativeDataTest(PixelTestData oldData, PixelTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getPublisher().getGuid());
        login();
        PreviewPixelPage previewPixelPage = new PreviewPixelPage(pxDriver);
        // bring into view pixel section
        previewPixelPage.navigateToItem(OFFER_PIXELS_ITEM);
        // if there is no any payout - create it first
        if (!oldData.isAnyAvailablePixel()) {
            CreatePixelsPage createPixelsPage = new CreatePixelsPage(pxDriver);
            createPixelsPage.createInstance(oldData).saveInstance(oldData);
            // check presence in table
            PixelsPage pixelsPage = new PixelsPage(pxDriver);
            pixelsPage.setTableElement();
            pixelsPage.checkCreatedInstanceByName(oldData.getOfferName(), 1, false);
            oldData.setPixelObject(oldData.getPublisher().getId(), oldData.getOffer().getId());
//            pixelsPage.setPixelID(oldData);
        }
        // navigate from publisher page to pixel details page
        previewPixelPage.testPixel(oldData);
        // edit instance
        new EditPixelsPage(pxDriver)
                .editInstance(newData)
                .saveInstance(newData);
    }
}
