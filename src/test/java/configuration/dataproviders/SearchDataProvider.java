package configuration.dataproviders;

import dto.TestDataError;
import dto.TestDataException;
import org.testng.annotations.DataProvider;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.SearchData;
import px.reports.inbound.InboundTransactionTestData;
import px.reports.leads.LeadsReportTestData;
import px.reports.outbound.OutboundTransactionTestData;
import px.reports.pingPostTransactions.PingPostTransactionsTestData;
import utils.FailListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kgr on 8/10/2017.
 */
public class SearchDataProvider extends ReportsDataProvider {

    @DataProvider
    public static Object[][] inboundTransactionsReportSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            InboundTransactionTestData testData = new InboundTransactionTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData;
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] inboundTransactionsWithFiltersSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            InboundTransactionTestData testData = new InboundTransactionTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData.setFiltersData();
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] leadsReportSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            LeadsReportTestData testData = new LeadsReportTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData;
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] leadsReportWithFiltersSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            LeadsReportTestData testData = new LeadsReportTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData.setFiltersData();
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] outboundTransactionsReportSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            OutboundTransactionTestData testData = new OutboundTransactionTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData;
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] outboundTransactionsWithFiltersSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            OutboundTransactionTestData testData = new OutboundTransactionTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData.setFiltersData();
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] pingPostTransactionsReportSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            PingPostTransactionsTestData testData = new PingPostTransactionsTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData;
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }

    @DataProvider
    public static Object[][] pingPostTransactionsWithFiltersSearchData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        try {
            PingPostTransactionsTestData testData = new PingPostTransactionsTestData();
            List<FieldFormatObject> fields = FieldFormatObject.searchableFields(testData.getFields());
            if (fields.isEmpty()) throw new TestDataException("No lookup fields");
            Object[][] objects = new Object[fields.size()][2];
            for (int i = 0; i < fields.size(); i++) {
                try {
                    FailListener.OWN_INVOCATION_COUNT = i;
                    SearchData searchData = testData.getSearchData(fields.get(i));
                    objects[i][0] = testData.setFiltersData();
                    objects[i][1] = searchData;
                } catch (Exception e) {
                    TestDataError.collect(e);
                    objects[i][0] = null;
                    objects[i][1] = null;
                }
            }
            return objects;
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        } finally {
            FailListener.OWN_INVOCATION_COUNT = 0;
        }
    }
}
