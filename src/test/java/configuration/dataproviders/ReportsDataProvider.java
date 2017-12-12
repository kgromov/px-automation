package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.reports.audience.AudienceNonVerticalReportTestData;
import px.reports.audience.AudienceReportTestData;
import px.reports.audience.AudienceVerticalReportTestData;
import px.reports.buyerCampaign.BuyerCampaignReportTestData;
import px.reports.buyerPerformance.BuyerPerformanceTestData;
import px.reports.buyers.BuyerReportTestData;
import px.reports.campaigns.CampaignDetailsTestData;
import px.reports.campaigns.CampaignsReportTestData;
import px.reports.dailyMargin.DailyMarginReportTestData;
import px.reports.disposition.DispositionBreakdownReportTestData;
import px.reports.inbound.InboundTransactionTestData;
import px.reports.leads.LeadsReportTestData;
import px.reports.outbound.OutboundTransactionTestData;
import px.reports.pingPost.PingPostReportTestData;
import px.reports.pingPostTransactions.PingPostTransactionsTestData;
import px.reports.publisherConversion.PublisherConversionTestData;
import px.reports.publisherConversionDetails.PublisherConversionDetailTestData;
import px.reports.publisherDaily.PublisherDailyReportTestData;
import px.reports.publisherPerformance.PublisherPerformanceTestData;
import px.reports.sourceQuality.SourceQualityScoreTestData;
import utils.FailListener;

import java.util.*;

import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;
import static px.reports.buyerCampaign.BuyerCampaignReportTestData.REPORT_TYPE_MAP;

/**
 * Created by konstantin on 21.10.2016.
 */
public class ReportsDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] sourceQualityScoreReportCommonData() {
        try {
            SourceQualityScoreTestData testData = new SourceQualityScoreTestData();
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
    public static Object[][] sourceQualityScoreReportInstancesData() {
        try {
            SourceQualityScoreTestData testData = new SourceQualityScoreTestData(true);
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
    public static Object[][] buyerReportCommonData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        Object[][] objects = new Object[categoriesList.size()][2];
        for (int i = 0; i < categoriesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                BuyerReportTestData testData = new BuyerReportTestData();
                String reportType = categoriesList.get(i);
                testData.setItemsByReportType(reportType);
                objects[i][0] = testData;
                objects[i][1] = reportType;
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] buyerReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        Object[][] objects = new Object[categoriesList.size()][1];
        for (int i = 0; i < categoriesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new BuyerReportTestData(categoriesList.get(i));
            } catch (Exception e) {
                TestDataError.collect(e);
//                TestDataError.collect(e.getMessage());
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] inboundTransactionsReportCommonData() {
        try {
            InboundTransactionTestData testData = new InboundTransactionTestData();
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
            InboundTransactionTestData testData = new InboundTransactionTestData(true);
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
    public static Object[][] outboundTransactionsReportCommonData() {
        try {
            OutboundTransactionTestData testData = new OutboundTransactionTestData();
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
    public static Object[][] outboundTransactionsReportInstancesData() {
        try {
            OutboundTransactionTestData testData = new OutboundTransactionTestData(true);
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
    public static Object[][] leadsReportCommonData() {
        try {
            LeadsReportTestData testData = new LeadsReportTestData();
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
    public static Object[][] leadsReportInstancesData() {
        try {
            LeadsReportTestData testData = new LeadsReportTestData(true);
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
    public static Object[][] publisherPerformanceReportCommonData() {
        try {
            PublisherPerformanceTestData testData = new PublisherPerformanceTestData();
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
    public static Object[][] publisherPerformanceReportInstancesData() {
        try {
            PublisherPerformanceTestData testData = new PublisherPerformanceTestData(true);
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
    public static Object[][] buyerCampaignReportCommonData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        List<String> reportTypesList = new ArrayList<>(REPORT_TYPE_MAP.keySet());
        int categoriesCount = categoriesList.size();
        int reportTypesCount = reportTypesList.size();
        Object[][] objects = new Object[categoriesCount * reportTypesCount][3];
        for (int i = 0; i < reportTypesCount; i++) {
            for (int j = 0; j < categoriesCount; j++) {
                int rowIndex = i * categoriesCount + j;
                try {
                    FailListener.OWN_INVOCATION_COUNT = rowIndex;
                    String reportType = reportTypesList.get(i);
                    String buyerCategory = categoriesList.get(j);
                    objects[rowIndex][0] = new BuyerCampaignReportTestData(reportType, buyerCategory, false);
                    objects[rowIndex][1] = reportType;
                    objects[rowIndex][2] = buyerCategory;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[rowIndex][0] = null;
                    objects[rowIndex][1] = null;
                    objects[rowIndex][2] = null;
                }
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] buyerCampaignReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        List<String> reportTypesList = new ArrayList<>(REPORT_TYPE_MAP.keySet());
        int categoriesCount = categoriesList.size();
        int reportTypesCount = reportTypesList.size();
        Object[][] objects = new Object[categoriesCount * reportTypesCount][1];
        for (int i = 0; i < reportTypesCount; i++) {
            for (int j = 0; j < categoriesCount; j++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i * categoriesCount + j;
                    objects[i * categoriesCount + j][0] = new BuyerCampaignReportTestData(reportTypesList.get(i), categoriesList.get(j));
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i * categoriesCount + j][0] = null;
                }
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] buyerPerformanceReportCommonDataByBuyerCategories() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        Object[][] objects = new Object[categoriesList.size()][2];
        for (int i = 0; i < categoriesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                BuyerPerformanceTestData testData = new BuyerPerformanceTestData();
                String buyerCategory = categoriesList.get(i);
                testData.setItemsByReportType(buyerCategory);
                objects[i][0] = testData;
                objects[i][1] = buyerCategory;
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
    public static Object[][] buyerPerformanceReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        Object[][] objects = new Object[categoriesList.size()][1];
        for (int i = 0; i < categoriesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new BuyerPerformanceTestData(categoriesList.get(i));
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] publisherConversionReportCommonData() {
        try {
            PublisherConversionTestData testData = new PublisherConversionTestData();
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
    public static Object[][] publisherConversionReportInstancesData() {
        try {
            PublisherConversionTestData testData = new PublisherConversionTestData(true);
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
    public static Object[][] publisherConversionDetailsData() {
        try {
            PublisherConversionDetailTestData testData = new PublisherConversionDetailTestData();
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
    public static Object[][] publisherDailyReportGroupingCommonData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PublisherDailyReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][3];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                PublisherDailyReportTestData testData = new PublisherDailyReportTestData();
                String reportType = reportTypesList.get(i);
                testData.setReportTypeItems(reportType);
                objects[i][0] = testData;
                objects[i][1] = reportType;
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
    public static Object[][] publisherDailyReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PublisherDailyReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][1];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new PublisherDailyReportTestData(reportTypesList.get(i), false);
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
                objects[i][0] = new PublisherDailyReportTestData(reportTypesList.get(i), true);
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] audienceReportNonVerticalCommonData() {
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
    public static Object[][] audienceReportNonVerticalInstancesData() {
        try {
            AudienceReportTestData testData = new AudienceNonVerticalReportTestData();
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
    public static Object[][] audienceReportVerticalInstancesData() {
        try {
            AudienceReportTestData testData = new AudienceVerticalReportTestData();
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
    public static Object[][] campaignsReportCommonData() {
        try {
            CampaignsReportTestData testData = new CampaignsReportTestData();
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
    public static Object[][] campaignsReportInstancesData() {
        try {
            CampaignsReportTestData testData = new CampaignsReportTestData(true);
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
    public static Object[][] campaignDetailsReportData() {
        try {
            CampaignDetailsTestData testData = new CampaignDetailsTestData(true);
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
    public static Object[][] pingPostTransactionsReportCommonData() {
        try {
            PingPostTransactionsTestData testData = new PingPostTransactionsTestData();
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
    public static Object[][] pingPostTransactionsReportInstancesData() {
        try {
            PingPostTransactionsTestData testData = new PingPostTransactionsTestData(true);
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
    public static Object[][] dailyMarginReportCommonData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(DailyMarginReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][2];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                DailyMarginReportTestData testData = new DailyMarginReportTestData();
                String reportType = reportTypesList.get(i);
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
    }

    @DataProvider
    public static Object[][] dailyMarginReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(DailyMarginReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][1];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new DailyMarginReportTestData(reportTypesList.get(i));
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] pingPostReportCommonData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PingPostReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][2];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                PingPostReportTestData testData = new PingPostReportTestData();
                String reportType = reportTypesList.get(i);
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
    }

    @DataProvider
    public static Object[][] pingPostReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> reportTypesList = new ArrayList<>(PingPostReportTestData.REPORT_TYPE_MAP.keySet());
        Object[][] objects = new Object[reportTypesList.size()][1];
        for (int i = 0; i < reportTypesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new PingPostReportTestData(reportTypesList.get(i));
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    @DataProvider
    public static Object[][] dispositionBreakdownReportCommonData() {
        try {
            DispositionBreakdownReportTestData testData = new DispositionBreakdownReportTestData();
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
    public static Object[][] dispositionBreakdownReportInstancesData() {
        try {
            DispositionBreakdownReportTestData testData = new DispositionBreakdownReportTestData(true);
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