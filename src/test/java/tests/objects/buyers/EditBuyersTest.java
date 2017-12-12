package tests.objects.buyers;

import configuration.dataproviders.BuyerDataProvider;
import org.testng.annotations.Test;
import px.objects.buyers.BuyerTestData;
import px.objects.buyers.pages.EditBuyersPage;
import tests.LoginTest;

/**
 * Created by konstantin on 21.10.2016.
 */
public class EditBuyersTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "editBuyerData", dataProviderClass = BuyerDataProvider.class)
    public void editBuyerWithPositiveData(BuyerTestData testData) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + testData.getGuid());
        login();
        new EditBuyersPage(pxDriver)
                .checkDefaultValues(testData)
                .editInstance(testData)
                .saveInstance(testData);
        // check edited fields - only name indeed, no sense - get request by id/name
//        CheckTestData.checkEditedBuyer(newData);
    }

    @Test(dataProvider = "editNegativeBuyerData", dataProviderClass = BuyerDataProvider.class)
    public void editBuyerWithNegativeData(BuyerTestData testData) {
        pxDriver.goToURL(url + "admin/buyers/edit/" + testData.getGuid());
        login();
        new EditBuyersPage(pxDriver)
                .checkDefaultValues(testData)
                .editInstance(testData)
                .saveInstance(testData);
    }
}
