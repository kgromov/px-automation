package configuration.dataproviders;

import dto.TestDataError;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.offers.OfferTestData;

/**
 * Created by konstantin on 21.10.2016.
 */
public class OfferDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] positiveOfferData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            OfferTestData testData = new OfferTestData(dataMode);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] negativeOfferData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            OfferTestData testData = new OfferTestData(dataMode);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] editOfferData() {
        try {
            JSONObject jsonObject = getInstanceDetails("offers", "offerName", "Offer Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            OfferTestData newData = new OfferTestData(dataMode);
            newData.setId(String.valueOf(jsonObject.get("offerId")));
            OfferTestData oldData = new OfferTestData(jsonObject);
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
    public static Object[][] editNegativeOfferData() {
        try {
            JSONObject jsonObject = getInstanceDetails("offers", "offerName", "Offer Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            OfferTestData newData = new OfferTestData(dataMode);
            OfferTestData oldData = new OfferTestData(jsonObject);
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
    public static Object[][] deleteOfferData() {
        try {
            JSONObject jsonObject = getInstanceDetails("offers", "offerName", "Offer Name ");
            OfferTestData testData = new OfferTestData(jsonObject);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    // -------------------------- Edit Advanced settings --------------------------
    @DataProvider
    public static Object[][] createOfferURLData() {
        try {
            JSONObject jsonObject = getInstanceDetails("offers", "offerName", "Offer Name ");
            OfferTestData testData = new OfferTestData(jsonObject);
            testData.setOfferURLData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] assignGroupData() {
        try {
            JSONObject jsonObject = getInstanceDetails("offers", "offerName", "Offer Name ");
            OfferTestData testData = new OfferTestData(jsonObject);
            testData.setOfferGroupsData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] createTargetingData() {
        try {
            JSONObject jsonObject = getInstanceDetails("offers", "offerName", "Offer Name ");
            OfferTestData testData = new OfferTestData(jsonObject);
            testData.setOfferTargetingData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }
}
