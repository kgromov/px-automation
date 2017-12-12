package tests.objects.brokers;

import configuration.dataproviders.BrokerDataProvider;
import org.testng.annotations.Test;
import px.objects.brokers.BrokerTestData;
import px.objects.brokers.pages.BrokersPage;
import px.objects.brokers.pages.CreateBrokersPage;
import tests.LoginTest;

/**
 * Created by konstantin on 21.10.2016.
 */
public class CreateBrokersTest extends LoginTest {

    @Test(dataProvider = "positiveBrokerData", dataProviderClass = BrokerDataProvider.class)
    public void createBrokerWithPositiveData(BrokerTestData testData) {
        BrokersPage brokersPage = new BrokersPage(pxDriver);
        brokersPage.navigateToObjects()
                .fillInPage();
        new CreateBrokersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
        // check created in brokers table -> search by name
        brokersPage.checkCreatedInstanceByName(testData);
    }

    @Test(dataProvider = "negativeBrokerData", dataProviderClass = BrokerDataProvider.class)
    public void createBrokerWithNegativeData(BrokerTestData testData) {
        new BrokersPage(pxDriver)
                .navigateToObjects()
                .fillInPage();
        new CreateBrokersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
    }

}
