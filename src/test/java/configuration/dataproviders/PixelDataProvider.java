package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.pixels.PixelTestData;

/**
 * Created by kgr on 5/3/2017.
 */
public class PixelDataProvider extends SuperDataProvider {
    // ============================ Create data ============================
    @DataProvider
    public static Object[][] createPixelData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            PixelTestData testData = new PixelTestData(dataMode)
                    .setCreateData()
                    .setUniquePixelData();
            testData.generatePixelDataByOfferProtocol();
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
    public static Object[][] createPixelNegativeData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            PixelTestData testData = new PixelTestData(dataMode)
                    .setCreateData()
                    .setNonUniquePixelData();
            testData.generatePixelDataByOfferProtocol();
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

    // ============================ Preview/Delete data ============================
    @DataProvider
    public static Object[][] deletePixelData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            PixelTestData testData = new PixelTestData(dataMode)
                    .setExistedPixels()
                    .setNonUniquePixelData()
                    .setUpdateData();
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
    public static Object[][] pixelDetailsData() {
        try {
            return new Object[][]{
                    {new PixelTestData()}
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
    public static Object[][] editPixelData() {
        try {
            DataMode dataMode1 = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            PixelTestData oldData = new PixelTestData(dataMode1)
                    .setExistedPixels()
                    .setNonUniquePixelData()
                    .setUpdateData();
            DataMode dataMode2 = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            PixelTestData newData = new PixelTestData(dataMode2)
                    .setCreateData()
                    .setUniquePixelData()
                    .clone(oldData);
            newData.setPixelObject();
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
    public static Object[][] editPixelWithNegativeData() {
        try {
            DataMode dataMode1 = new DataMode.Builder()
                    .updateData()
                    .createByResponse()
                    .positiveData()
                    .build();
            PixelTestData oldData = new PixelTestData(dataMode1)
                    .setExistedPixels()
                    .setNonUniquePixelData()
                    .setUpdateData();
            DataMode dataMode2 = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            PixelTestData newData = new PixelTestData(dataMode2)
                    .setCreateData()
                    .setNonUniquePixelData()
                    .clone(oldData);
            newData.setPixelObject();
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
}
