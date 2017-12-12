package tests.objects.brokers;

import configuration.dataproviders.BrokerDataProvider;
import dto.CheckTestData;
import org.testng.annotations.Test;
import px.objects.brokers.BrokerTestData;
import px.objects.brokers.pages.EditBrokersPage;
import tests.LoginTest;

/**
 * Created by konstantin on 21.10.2016.
 */
public class EditBrokersTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "editBrokerData", dataProviderClass = BrokerDataProvider.class)
    public void editBrokerWithPositiveData(BrokerTestData oldData, BrokerTestData newData) {
        pxDriver.goToURL(super.url + "admin/brokers/" + oldData.getGuid() + "/details");
        login();
        new EditBrokersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData)
                .saveInstance(newData);
        // check edited fields
        CheckTestData.checkEditedBroker(newData);
    }

    @Test(dataProvider = "editNegativeBrokerData", dataProviderClass = BrokerDataProvider.class)
    public void editBrokerWithNegativeData(BrokerTestData oldData, BrokerTestData newData) {
        pxDriver.goToURL(super.url + "admin/brokers/" + oldData.getGuid() + "/details");
        login();
        new EditBrokersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData)
                .saveInstance(newData);
    }
}
