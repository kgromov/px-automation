package configuration.dataproviders;

import dto.TestDataError;
import org.testng.annotations.DataProvider;
import px.reports.audience.AudienceNonVerticalUnderBuyerReportTestData;
import px.reports.audience.AudienceReportTestData;
import px.reports.audience.AudienceVerticalUnderBuyerReportTestData;
import px.reports.buyers.BuyerUnderBuyerReportTestData;
import px.reports.leads.LeadsReportUnderBuyerTestData;
import px.reports.outbound.OutboundTransactionUnderBuyerTestData;
import px.reports.sourceQuality.SourceQualityScoreUnderBuyerTestData;
import utils.FailListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kgr on 7/28/2017.
 */
public class BuyerReportsDataProvider extends ReportsDataProvider {

    @DataProvider
    public static Object[][] audienceReportNonVerticalInstancesData() {
        try {
            AudienceReportTestData testData = new AudienceNonVerticalUnderBuyerReportTestData();
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
            AudienceReportTestData testData = new AudienceVerticalUnderBuyerReportTestData();
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
    public static Object[][] buyerReportInstancesData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        List<String> categoriesList = new ArrayList<>(dataProvider.getPossibleValueFromJSON("BuyerCategories").keySet());
        Object[][] objects = new Object[categoriesList.size()][1];
        for (int i = 0; i < categoriesList.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = new BuyerUnderBuyerReportTestData(categoriesList.get(i));
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
    public static Object[][] leadsReportInstancesData() {
        try {
            LeadsReportUnderBuyerTestData testData = new LeadsReportUnderBuyerTestData();
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
    public static Object[][] outboundTransactionsReportInstancesData() {
        try {
            OutboundTransactionUnderBuyerTestData testData = new OutboundTransactionUnderBuyerTestData();
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