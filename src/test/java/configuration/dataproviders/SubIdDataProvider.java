package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.subIds.SubIdTestData;
import px.objects.subIds.SubIdsPreviewTestData;

/**
 * Created by kgr on 7/14/2017.
 */
public class SubIdDataProvider {

    @DataProvider
    public static Object[][] editSubIdWithPositiveData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            SubIdsPreviewTestData previewTestData = new SubIdsPreviewTestData();
            SubIdTestData testData = new SubIdTestData(dataMode);
            testData.setPublisher(previewTestData.getPublisher());
            testData.setPrevName(previewTestData.getSubId().getName());
            testData.setGuid(previewTestData.getSubId().getGuid());
            testData.setHeaderObjects();
            return new Object[][]{
                    {previewTestData, testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] editSubIdWithNegativeData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            SubIdsPreviewTestData previewTestData = new SubIdsPreviewTestData();
            SubIdTestData testData = new SubIdTestData(dataMode);
            testData.setPublisher(previewTestData.getPublisher());
            testData.setId(previewTestData.getSubId().getId());
            testData.setPrevName(previewTestData.getSubId().getName());
            testData.setGuid(previewTestData.getSubId().getGuid());
            testData.setHeaderObjects();
            return new Object[][]{
                    {previewTestData, testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] subIdDetailsData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            SubIdsPreviewTestData previewTestData = new SubIdsPreviewTestData();
            SubIdTestData testData = new SubIdTestData(dataMode);
            testData.setPublisher(previewTestData.getPublisher());
            testData.setId(previewTestData.getSubId().getId());
            testData.setName(previewTestData.getSubId().getName());
            testData.setGuid(previewTestData.getSubId().getGuid());
            // set subId details
            testData.setHeaderObjects();
            return new Object[][]{
                    {previewTestData, testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] subIdOverviewData() {
        try {
            return new Object[][]{
                    {new SubIdsPreviewTestData()}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }
}
