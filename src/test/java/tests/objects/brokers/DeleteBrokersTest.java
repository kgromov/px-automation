package tests.objects.brokers;

import configuration.dataproviders.BrokerDataProvider;
import org.testng.annotations.Test;
import px.objects.brokers.BrokerTestData;
import px.objects.brokers.pages.BrokersPage;
import tests.LoginTest;

/**
 * Created by konstantin on 21.10.2016.
 */
public class DeleteBrokersTest extends LoginTest {

    @Test(dataProvider = "deleteBrokerData", dataProviderClass = BrokerDataProvider.class)
    public void deleteBroker(BrokerTestData testData) {
        new BrokersPage(pxDriver)
                .navigateToObjects()
                .deleteBroker(testData)
                .checkDeletedInstanceByName(testData)
                // check edited fields
                .checkDeletedInstanceByResponse(testData);
    }
}
