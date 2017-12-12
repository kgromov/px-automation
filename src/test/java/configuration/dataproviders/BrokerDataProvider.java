package configuration.dataproviders;

import dto.TestDataError;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.brokers.BrokerTestData;

/**
 * Created by konstantin on 21.10.2016.
 */
public class BrokerDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] positiveBrokerData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            BrokerTestData testData = new BrokerTestData(dataMode);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] negativeBrokerData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            BrokerTestData testData = new BrokerTestData(dataMode);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] editBrokerData() {
        try {
            JSONObject jsonObject = getInstanceDetails("brokers", "brokerName", "Broker Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            // cause broker name is not updated
            BrokerTestData newData = new BrokerTestData(dataMode);
            newData.setName(String.valueOf(jsonObject.get("brokerName")));
            // cause potentially each new TestData could throw exception
            BrokerTestData oldData = new BrokerTestData(jsonObject);
            return new Object[][]{
                    {oldData, newData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] editNegativeBrokerData() throws Exception {
        try {
            JSONObject jsonObject = getInstanceDetails("brokers", "brokerName", "Broker Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            // cause potentially each new TestData could throw exception
            BrokerTestData oldData = new BrokerTestData(jsonObject);
            BrokerTestData newData = new BrokerTestData(dataMode);
            return new Object[][]{
                    {oldData, newData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] deleteBrokerData() {
        try {
            JSONObject jsonObject = getInstanceDetails("brokers", "brokerName", "Broker Name ");
            BrokerTestData testData = new BrokerTestData(jsonObject);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

}
