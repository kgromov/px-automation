package configuration.dataproviders;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.SecurityToken;
import configuration.helpers.SessionToken;
import dto.TestDataError;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.disposition.DispositionTestData;
import px.objects.leadReturn.SingleLeadReturnsTestData;
import px.objects.leadReturn.SingleLeadReturnsUnderBuyerTestData;
import px.objects.leads.LeadsTestData;
import px.objects.leads.buyer.LeadsUnderBuyerTestData;
import px.reports.dto.DateRange;
import px.reports.leadReturns.LeadReturnsReportTestData;
import px.reports.leads.LeadsReportTestData;
import utils.FailListener;

import java.util.*;
import java.util.stream.Collectors;

import static config.Config.setAdminUser;
import static config.Config.setTestURL;
import static configuration.helpers.DataHelper.getRandomInt;
import static px.reports.leadReturns.LeadReturnStatusEnum.*;
import static px.reports.leadReturns.LeadReturnsReportTestData.REPORT_TYPES;
import static px.reports.leads.LeadsReportTestData.VERTICAL_FILTER;

/**
 * Created by konstantin on 21.10.2016.
 */
public class LeadDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] leadPreviewData() {
        try {
            return new Object[][]{
                    {new LeadsTestData()}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] leadPreviewUnderBuyerData() {
        try {
            return new Object[][]{
                    {new LeadsUnderBuyerTestData()}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] positiveDispositionHistoryData() {
        try {
            DispositionTestData testData = new DispositionTestData()
                    .setPositiveData();
            testData.setLead();                 // for navigation; lead details
            testData.setDispositionHistory();   // previous disposition history records
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
    public static Object[][] updateDispositionForMultipleLeadsWithPositiveData() {
        try {
            DispositionTestData testData = new DispositionTestData()
                    .setPositiveData();
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

    @DataProvider
    public static Object[][] negativeDispositionHistoryData() {
        try {
            DispositionTestData testData = new DispositionTestData();
            testData.setLead();                 // for navigation; lead details
            testData.setDispositionHistory();   // previous disposition history records
            testData.setNegativeData();         // cause lead creationDate is used for negative date
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
    public static Object[][] leadsReturnReportData() {
        FailListener.OWN_INVOCATION_COUNT = 0;
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        Object[][] objects = new Object[REPORT_TYPES.size()][2];
        for (int i = 0; i < REPORT_TYPES.size(); i++) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                String reportType = REPORT_TYPES.get(i);
                objects[i][0] = new LeadReturnsReportTestData(reportType);
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
    public static Object[][] returnLeadWithPositiveData() {
        try {
            SingleLeadReturnsTestData testData = new SingleLeadReturnsTestData();
            testData.setPositiveData();
            int monthOffset = DataHelper.getMonthOffset(new Date(), testData.getLead().getDate());
            return new Object[][]{
                    {testData, new LeadReturnsReportTestData()
                            .withReportType(PENDING.getValue())
                            .withMonth(monthOffset)
                    }
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] returnLeadWithNegativeData() {
        try {
            SingleLeadReturnsTestData testData = new SingleLeadReturnsTestData();
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
    public static Object[][] acceptLeadReturnData() {
        try {
            SingleLeadReturnsTestData testData = new SingleLeadReturnsTestData(ACCEPTED);
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
    public static Object[][] declineLeadReturnWithPositiveData() {
        try {
            SingleLeadReturnsTestData testData = new SingleLeadReturnsTestData(DECLINED)
                    .withDeclineExplanation(true);
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
    public static Object[][] declineLeadReturnWithNegativeData() {
        try {
            SingleLeadReturnsTestData testData = new SingleLeadReturnsTestData(DECLINED)
                    .withDeclineExplanation(false);
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
    public static Object[][] acceptMultipleLeadReturnData() {
        try {
            LeadReturnsReportTestData reportData = new LeadReturnsReportTestData(PENDING.getValue())
                    .withAttempts();
            Set<JSONObject> leadReturns = reportData.forMultipleReturns();
            List<SingleLeadReturnsTestData> testData = leadReturns.stream().map(leadReturn ->
                    new SingleLeadReturnsTestData(reportData, leadReturn).withStatus(ACCEPTED))
                    .collect(Collectors.toList());
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
    public static Object[][] declineMultipleLeadReturnWithPositiveData() {
        try {
            LeadReturnsReportTestData reportData = new LeadReturnsReportTestData(PENDING.getValue())
                    .withAttempts();
            Set<JSONObject> leadReturns = reportData.forMultipleReturns();
            List<SingleLeadReturnsTestData> testData = leadReturns.stream().map(leadReturn ->
                    new SingleLeadReturnsTestData(reportData, leadReturn)
                            .withStatus(DECLINED)
                            .withDeclineExplanation(true))
                    .collect(Collectors.toList());
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
    public static Object[][] declineMultipleLeadReturnWithNegativeData() {
        try {
            LeadReturnsReportTestData reportData = new LeadReturnsReportTestData(PENDING.getValue())
                    .withAttempts();
            Set<JSONObject> leadReturns = reportData.forMultipleReturns();
            List<SingleLeadReturnsTestData> testData = leadReturns.stream().map(leadReturn ->
                    new SingleLeadReturnsTestData(reportData, leadReturn)
                            .withStatus(DECLINED)
                            .withDeclineExplanation(false))
                    .collect(Collectors.toList());
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

    // lead return under buyer
    @DataProvider
    public static Object[][] returnLeadWithPositiveDataUnderBuyer() {
        try {
            // buyer session
            SingleLeadReturnsUnderBuyerTestData testData = new SingleLeadReturnsUnderBuyerTestData();
            testData.setPositiveData();
            int monthOffset = DataHelper.getMonthOffset(new Date(), testData.getLead().getDate());
            // admin session
            setAdminUser();
            SessionToken adminToken = new SessionToken();
            SecurityToken.changeSession(adminToken);
            LeadReturnsReportTestData reportData = new LeadReturnsReportTestData()
                    .withReportType(PENDING.getValue())
                    .withMonth(monthOffset);
            // revert session to buyer
            setTestURL(null);
            SecurityToken.revertSession();
            return new Object[][]{
                    {testData, reportData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] returnLeadWithNegativeDataUnderBuyer() {
        try {
            SingleLeadReturnsUnderBuyerTestData testData = new SingleLeadReturnsUnderBuyerTestData();
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
    public static Object[][] acceptLeadReturnDataUnderBuyer() {
        try {
            // buyer session
            SecurityToken.getToken();
            SingleLeadReturnsTestData testData;
            // admin session
            setAdminUser();
            SessionToken adminToken = new SessionToken();
            SecurityToken.changeSession(adminToken);
            LeadReturnsReportTestData reportData = new LeadReturnsReportTestData(PENDING.getValue());
            if (reportData.isAnyLeadReturnedByBuyer(new SingleLeadReturnsUnderBuyerTestData(false).getCampaignIDs())) {
                testData = new SingleLeadReturnsUnderBuyerTestData(reportData, ACCEPTED);
            } else {
                // revert session to buyer
                setTestURL(null);
                SecurityToken.revertSession();
                testData = new SingleLeadReturnsUnderBuyerTestData();
                testData.setPositiveData();
            }
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
    public static Object[][] declineLeadReturnDataUnderBuyer() {
        try {
            // buyer session
            SecurityToken.getToken();
            SingleLeadReturnsTestData testData;
            // admin session
            setAdminUser();
            SessionToken adminToken = new SessionToken();
            SecurityToken.changeSession(adminToken);
            LeadReturnsReportTestData reportData = new LeadReturnsReportTestData(PENDING.getValue());
            if (reportData.isAnyLeadReturnedByBuyer(new SingleLeadReturnsUnderBuyerTestData(false).getCampaignIDs())) {
                testData = new SingleLeadReturnsUnderBuyerTestData(reportData, DECLINED)
                        .withDeclineExplanation(true);
            } else {
                // revert session to buyer
                setTestURL(null);
                SecurityToken.revertSession();
                testData = new SingleLeadReturnsUnderBuyerTestData();
                testData.setPositiveData();
            }
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
    public static Object[][] checkLeadXmlData() {
        Map<String, String> verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals");
        Map<String, LeadsReportTestData.ResponseObject> verticals = new HashMap<>();
        LeadsReportTestData testData = nonEmptyLeadOverviewWithAttempts(new LeadsReportTestData());
        verticalsMap.keySet().forEach(vertical -> {
            // items in table by vertical
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/leaddetailsfastreport/data")
                    .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(vertical, testData.getFromPeriod(), testData.getToPeriod()))
                    .sort("dateTime", "desc")
                    .build().getRequestedURL();
            JSONArray itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            if (itemsByVertical.length() > 0) {
                JSONObject object = itemsByVertical.getJSONObject(getRandomInt(itemsByVertical.length()));
                verticals.put(vertical, LeadsReportTestData.getResponseObject(object));
            }
        });
        Object[][] objects = new Object[verticals.size()][2];
        int i = 0;
        for (Map.Entry<String, LeadsReportTestData.ResponseObject> entry : verticals.entrySet()) {
            try {
                FailListener.OWN_INVOCATION_COUNT = i;
                objects[i][0] = entry.getKey();
                objects[i][1] = entry.getValue();
                i++;
            } catch (Exception e) {
                TestDataError.collect(e);
                objects[i][0] = null;
                objects[i][1] = null;
            }
        }
        FailListener.OWN_INVOCATION_COUNT = 0;
        return objects;
    }

    private static LeadsReportTestData nonEmptyLeadOverviewWithAttempts(LeadsReportTestData testData) {
        Set<DateRange> ranges = new HashSet<>();
        do {
            if (testData.getItemsCurrentTotalCount() > 0) return testData;
            ranges.add(new DateRange(testData.getFromPeriod(), testData.getToPeriod()));
            testData = new LeadsReportTestData();
        } while (ranges.size() < 5);
        throw new TestDataException(String.format("There is no data after" +
                " 5 attempts in the following ranges %s", ranges));
    }
}