package configuration.dataproviders;

import dto.TestDataError;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.credSets.CredSetTestData;
import px.objects.credSets.CredSetsOverviewData;

/**
 * Created by kgr on 10/30/2017.
 */
public class CredentialSettingsDataProvider extends SuperDataProvider {
    // ----------------------- Create -----------------------
    @DataProvider
    public static Object[][] createCredSetWithPositiveData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            CredSetTestData testData = new CredSetTestData(dataMode);
            testData.setPositiveData();
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
    public static Object[][] createCredSetWithNegativeData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            CredSetTestData testData = new CredSetTestData(dataMode);
            testData.setNegativeData();
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
    public static Object[][] cloneCredSetWithPositiveData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            CredSetsOverviewData overviewData = new CredSetsOverviewData();
            CredSetTestData oldData = overviewData.getItemsTotalCount() > 0 ? new CredSetTestData(overviewData) : new CredSetTestData(dataMode);
            CredSetTestData newData = new CredSetTestData(dataMode);
            newData.setPositiveData();
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
    public static Object[][] cloneCredSetWithNegativeData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            CredSetsOverviewData overviewData = new CredSetsOverviewData();
            CredSetTestData oldData = overviewData.getItemsTotalCount() > 0 ? new CredSetTestData(overviewData) : new CredSetTestData(dataMode);
            DataMode dataMode2 = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            CredSetTestData newData = new CredSetTestData(dataMode2);
            newData.setNegativeData();
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
    // ----------------------- Edit -----------------------
    @DataProvider
    public static Object[][] editCredSetWithPositiveData() {
        try {
            JSONObject jsonObject = getInstanceDetails("credsets", "credSetName", "CredSetName");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            CredSetTestData testData = new CredSetTestData(dataMode);
            testData.setPositiveData();
            testData.setPrevName(String.valueOf(jsonObject.get("credSetName")));
            testData.setGuid(String.valueOf(jsonObject.get("credSetGuid")));
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
    public static Object[][] editCredSetWithNegativeData() {
        try {
            JSONObject jsonObject = getInstanceDetails("credsets", "credSetName", "CredSetName");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            CredSetTestData testData = new CredSetTestData(dataMode);
            testData.setNegativeData();
            testData.setPrevName(String.valueOf(jsonObject.get("credSetName")));
            testData.setGuid(String.valueOf(jsonObject.get("credSetGuid")));
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
    // ----------------------- Preview -----------------------
    @DataProvider
    public static Object[][] previewCredSetData() {
        try {
            JSONObject jsonObject = getInstanceDetails("credsets", "credSetName", "CredSetName");
            CredSetTestData testData = new CredSetTestData(jsonObject);
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
