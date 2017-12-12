package configuration.dataproviders;

import config.Config;
import configuration.helpers.DataHelper;
import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.reports.audience.AudienceReportTestData;
import px.reports.buyerPerformance.BuyerPerformanceTestData;
import px.reports.dailyMargin.DailyMarginReportTestData;
import px.reports.export.LeadsExportTestData;
import px.reports.pingPost.PingPostReportTestData;
import px.reports.publisherDaily.PublisherDailyReportTestData;
import utils.FailListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 4/21/2017.
 */
public class ExportReportsDataProvider extends SuperDataProvider {

    @DataProvider
    public static Object[][] buyerPerformanceReportExportData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        Object[][] objects = new Object[categoriesList.size()][2];
        for (int i = 0; i < categoriesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new BuyerPerformanceTestData();
                objects[i][1] = categoriesList.get(i);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
                objects[i][1] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] leadsReportExportData() {
        try {
            LeadsExportTestData testData = new LeadsExportTestData();
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] publisherDailyReportExportData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PublisherDailyReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][2];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new PublisherDailyReportTestData();
                objects[i][1] = reportTypesList.get(i);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
                objects[i][1] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] publisherDailyReportGroupingExportData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PublisherDailyReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][3];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                PublisherDailyReportTestData testData = new PublisherDailyReportTestData();
                testData.setGroupingFields();
                testData.setGroupingFieldsToSet(true);
                objects[i][0] = testData;
                objects[i][1] = reportTypesList.get(i);
                objects[i][2] = testData.getGroupingsList();
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
                objects[i][1] = null;
                objects[i][2] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] audienceReportNonVerticalExportData() {
        try {
            FailListener.OWN_INVOCATION_COUNT = 0;
            FailListener.METHOD_ERROR_MAP = new HashMap<>();
            Map<String, String> reportTypesMap = AudienceReportTestData.getReportTypeMap(DEFAULT_VERTICAL);
            List<String> reportTypes = new ArrayList<>(reportTypesMap.keySet());
            Object[][] objects = new Object[reportTypes.size()][2];
            for (int i = 0; i < reportTypes.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    AudienceReportTestData testData = new AudienceReportTestData();
                    objects[i][0] = testData;
                    objects[i][1] = reportTypes.get(i);
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            FailListener.OWN_INVOCATION_COUNT = 0;
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] audienceReportVerticalExportData() {
        try {
            FailListener.OWN_INVOCATION_COUNT = 0;
            FailListener.METHOD_ERROR_MAP = new HashMap<>();
            Map<String, String> verticalsMap = Config.isBetaEnvironment() ? dataProvider.getPossibleValueFromJSON("Verticals_beta")
                    : dataProvider.getPossibleValueFromJSON("Verticals");
            // workaround cause of bug - verticals (enumSequenceIndex=[23; 38]) have no report types
//        verticalsMap = AudienceReportTestData.getProperVerticals(verticalsMap);
            String vertical = DataHelper.getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
            Map<String, String> reportTypesMap = AudienceReportTestData.getReportTypeMap(vertical);
            List<String> reportTypes = new ArrayList<>(reportTypesMap.keySet());
            Object[][] objects = new Object[reportTypes.size()][3];
            for (int i = 0; i < reportTypes.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    AudienceReportTestData testData = new AudienceReportTestData();
                    objects[i][0] = testData;
                    objects[i][1] = reportTypes.get(i);
                    objects[i][2] = vertical;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                    objects[i][2] = null;
                }
            }
            FailListener.OWN_INVOCATION_COUNT = 0;
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] dailyMarginReportExportData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(DailyMarginReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][2];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new DailyMarginReportTestData();
                objects[i][1] = reportTypesList.get(i);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
                objects[i][1] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] pingPostReportExportData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PingPostReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][2];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new PingPostReportTestData();
                objects[i][1] = reportTypesList.get(i);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
                objects[i][1] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }
}
