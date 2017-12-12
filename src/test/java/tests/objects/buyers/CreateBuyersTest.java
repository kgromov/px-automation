package tests.objects.buyers;

import configuration.dataproviders.BuyerDataProvider;
import org.testng.annotations.Test;
import px.objects.buyers.BuyerTestData;
import px.objects.buyers.pages.BuyersPage;
import px.objects.buyers.pages.CreateBuyersPage;
import tests.LoginTest;

/**
 * Created by konstantin on 21.10.2016.
 */
public class CreateBuyersTest extends LoginTest {

    @Test(dataProvider = "positiveBuyerData", dataProviderClass = BuyerDataProvider.class)
    public void createBuyerWithPositiveData(BuyerTestData testData) {
        BuyersPage buyersPage = new BuyersPage(pxDriver);
        buyersPage.navigateToObjects();
        buyersPage.fillInPage();
        new CreateBuyersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
        // check created in buyers table -> search by name
        buyersPage.checkCreatedInstanceByName(testData);
    }

    @Test(dataProvider = "negativeBuyerData", dataProviderClass = BuyerDataProvider.class)
    public void createBuyerWithNegativeData(BuyerTestData testData) {
        BuyersPage buyersPage = new BuyersPage(pxDriver);
        buyersPage.navigateToObjects();
        buyersPage.fillInPage();
        CreateBuyersPage createBuyersPage = new CreateBuyersPage(pxDriver);
        createBuyersPage.checkDefaultValues();
        createBuyersPage.createInstance(testData).saveInstance(testData);
    }
}
