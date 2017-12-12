package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.customRights.CustomRightsTestData;


/**
 * Created by kgr on 10/30/2017.
 */
public class CustomRightsDataProvider extends SuperDataProvider {

    @DataProvider
    public static Object[][] createCustomRightsWithPositiveData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            CustomRightsTestData testData = new CustomRightsTestData(dataMode);
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
    public static Object[][] createCustomRightsWithNegativeData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            CustomRightsTestData testData = new CustomRightsTestData(dataMode);
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
}
