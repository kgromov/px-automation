package configuration.dataproviders;

import dto.ObjectIdentityData;
import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.reports.rerun.*;

import java.util.List;
import java.util.stream.Collectors;

import static px.reports.rerun.RerunManagementColumnsEnum.NAME;
import static px.reports.rerun.RerunTaskTestData.NEGATIVE_MODE;
import static px.reports.rerun.RerunTaskTestData.POSITIVE_MODE;

/**
 * Created by konstantin on 06.10.2017.
 */
public class RerunManagementDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] createTaskWithPositiveData() {
        try {
            RerunTaskTestData testData = new RerunTaskTestData(POSITIVE_MODE);
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
    public static Object[][] createTaskWithNegativeData() {
        try {
            RerunTaskTestData testData = new RerunTaskTestData(NEGATIVE_MODE);
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
    public static Object[][] filterPreviewData() {
        try {
            RerunTaskTestData testData = new RerunTaskTestData(POSITIVE_MODE);
            FilterDetailsTestData filterData = new FilterDetailsTestData(testData.getFilter());
            return new Object[][]{
                    {filterData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] createTaskWithExistedFilterPositiveData() {
        try {
            RerunTaskTestData testData = new RerunTaskTestData(POSITIVE_MODE)
                    .withNewFilter(false);
//            FilterDetailsTestData filterData = new FilterDetailsTestData(testData.getFilter());
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
    public static Object[][] createTaskWithExistedFilterNegativeData() {
        try {
            RerunTaskTestData testData = new RerunTaskTestData(NEGATIVE_MODE)
                    .withNewFilter(false);
//            FilterDetailsTestData filterData = new FilterDetailsTestData(testData.getFilter());
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
    public static Object[][] deleteTask() {
        try {
            RerunOverviewTestData overviewData = new RerunOverviewTestData();
            RerunTaskTestData testData = new RerunTaskTestData(getInstanceContainsValue(
                    overviewData.getAllRowsArray(), NAME.getValue(), "RerunTask Name "));
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
    public static Object[][] processTaskExistedFilterData() {
        try {
            RerunedLeadsReportTestData reportData = new RerunedLeadsReportTestData(getAutoFilter());
            return new Object[][]{
                    {reportData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] removeProcessedLeadFromBatch() {
        try {
            ProcessedLeadTestData testData = new ProcessedLeadTestData();
            testData.setLead();
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
    public static Object[][] removeMultipleProcessedLeadFromBatch() {
        try {
            ProcessedLeadTestData testData = new ProcessedLeadTestData();
            testData.setLeads();
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

    private static ObjectIdentityData getAutoFilter() {
        List<ObjectIdentityData> filters = dataProvider.getCreatedInstancesData("rerunTaskFilters");
        List<ObjectIdentityData> autoFilters = filters.stream().filter(item -> item.getName().contains("RerunTask Name ")).collect(Collectors.toList());
        return ObjectIdentityData.getAnyObjectFromList(autoFilters);
    }
}
