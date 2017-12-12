package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.reports.audience.AudienceReportTestData;
import px.reports.audience.AudienceReportUnderPublisherTestData;
import px.reports.inbound.InboundTransactionTestData;
import px.reports.inbound.InboundTransactionUnderPublisherTestData;
import px.reports.publisherConversion.PublisherConversionTestData;
import px.reports.publisherConversion.PublisherConversionUnderPublisherTestData;
import px.reports.publisherDaily.PublisherDailyReportTestData;
import px.reports.publisherDaily.PublisherDailyReportUnderPublisherTestData;
import px.reports.sourceQuality.SourceQualityScoreUnderBuyerTestData;
import utils.FailListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static px.reports.ReportTestData.REPORT_TYPE_MAP;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 7/28/2017.
 */
public class PublisherReportsDataProvider extends ReportsDataProvider {

    @DataProvider
    public static Object[][] audienceReportCommonData() {
        try {
            FailListener.OWN_INVOCATION_COUNT = 0;
            FailListener.METHOD_ERROR_MAP = new HashMap<>();
            Map<String, String> reportTypesMap = AudienceReportTestData.getReportTypeMap(DEFAULT_VERTICAL);
            List<String> reportTypes = new ArrayList<>(reportTypesMap.keySet());
            Object[][] objects = new Object[reportTypes.size()][2];
            for (int i = 0; i < reportTypes.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    AudienceReportTestData testData = new AudienceReportUnderPublisherTestData(false);
                    String reportType = reportTypes.get(i);
                    testData.setItemsByReportType(reportType);
                    objects[i][0] = testData;
                    objects[i][1] = reportType;
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
    public static Object[][] audienceReportInstancesData() {
        try {
            AudienceReportTestData testData = new AudienceReportUnderPublisherTestData(true);
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
    public static Object[][] inboundTransactionsReportInstancesData() {
        try {
            InboundTransactionTestData testData = new InboundTransactionUnderPublisherTestData();
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
    public static Object[][] publisherConversionReportInstancesData() {
        try {
            PublisherConversionTestData testData = new PublisherConversionUnderPublisherTestData();
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
    public static Object[][] publisherDailyReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PublisherDailyReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][1];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new PublisherDailyReportUnderPublisherTestData(reportTypesList.get(i), false);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] publisherDailyReportGroupingInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PublisherDailyReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][1];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new PublisherDailyReportUnderPublisherTestData(reportTypesList.get(i), true);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] sourceQualityScoreReportInstancesData() {
        try {
            SourceQualityScoreUnderBuyerTestData testData = new SourceQualityScoreUnderBuyerTestData(true);
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