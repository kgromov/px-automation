package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.payouts.OffersPayoutTestData;
import px.objects.payouts.PublishersPayoutTestData;
import px.objects.payouts.PayoutTestData;

/**
 * Created by konstantin on 21.10.2016.
 */
public class PayoutDataProvider {
    // ============================ Create data ============================
    @DataProvider
    public static Object[][] createOfferPayoutData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            OffersPayoutTestData testData = new OffersPayoutTestData(dataMode)
                    .setCreateData().setUniquePayoutData();
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
    public static Object[][] createNegativeOfferPayoutData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            OffersPayoutTestData testData = new OffersPayoutTestData(dataMode)
                    .setCreateData().setNonUniquePayoutData();
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
    public static Object[][] createPublisherPayoutData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            PublishersPayoutTestData testData = new PublishersPayoutTestData(dataMode)
                    .setCreateData().setUniquePayoutData();
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
    public static Object[][] createNegativePublisherPayoutData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            PublishersPayoutTestData testData = new PublishersPayoutTestData(dataMode)
                    .setCreateData().setNonUniquePayoutData();
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

    // ============================ Edit data ============================
    @DataProvider
    public static Object[][] editOfferPayoutData() {
        try {
            DataMode dataMode1 = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            OffersPayoutTestData oldData = new OffersPayoutTestData(dataMode1)
                    .setUpdateData().setNonUniquePayoutData();
            oldData.setPayoutObject();
            DataMode dataMode2 = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            PayoutTestData newData = new OffersPayoutTestData(dataMode2)
                    .clone(oldData);
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
    public static Object[][] editNegativeOfferPayoutData() {
        try {
            DataMode dataMode1 = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            OffersPayoutTestData oldData = new OffersPayoutTestData(dataMode1)
                    .setUpdateData().setNonUniquePayoutData();
            oldData.setPayoutObject();
            DataMode dataMode2 = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            PayoutTestData newData = new OffersPayoutTestData(dataMode2)
                    .clone(oldData);
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
    public static Object[][] editPublisherPayoutData() {
        try {
            DataMode dataMode1 = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            PublishersPayoutTestData oldData = new PublishersPayoutTestData(dataMode1)
                    .setUpdateData().setNonUniquePayoutData();
            oldData.setPayoutObject();
//            oldData.setOfferObject();
            DataMode dataMode2 = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            PayoutTestData newData = new PublishersPayoutTestData(dataMode2)
                    .clone(oldData);
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
    public static Object[][] editNegativePublisherPayoutData() {
        try {
            DataMode dataMode1 = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            PublishersPayoutTestData oldData = new PublishersPayoutTestData(dataMode1)
                    .setUpdateData().setNonUniquePayoutData();
            oldData.setPayoutObject();
//            oldData.setOfferObject();
            DataMode dataMode2 = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            PayoutTestData newData = new PublishersPayoutTestData(dataMode2)
                    .clone(oldData);
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

    // ============================ Delete data ============================
    @DataProvider
    public static Object[][] deleteOfferPayoutData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .deleteData()
                    .positiveData()
                    .build();
            OffersPayoutTestData testData = new OffersPayoutTestData(dataMode)
                    .setDeleteData().setNonUniquePayoutData();
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
    public static Object[][] deletePublisherPayoutData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .deleteData()
                    .positiveData()
                    .build();
            PublishersPayoutTestData testData = new PublishersPayoutTestData(dataMode)
                    .setDeleteData().setNonUniquePayoutData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            return new Object[][]{
                    {null}
            };
        }
    }
}
