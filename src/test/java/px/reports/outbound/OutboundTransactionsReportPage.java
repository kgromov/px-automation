package px.reports.outbound;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pages.groups.Actions;
import pages.groups.Transactionable;
import pages.locators.ReportPageLocators;
import px.objects.sourceManagement.SourceManagementTestData;
import px.reports.LookUpReportPage;
import px.reports.ReportTestData;
import px.reports.SearchByEnum;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.TableDataObject;
import utils.CustomMatcher;
import utils.SoftAssertionHamcrest;

import java.util.*;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.DataHelper.getRandomValueFromList;
import static configuration.helpers.PXDataHelper.getValueByType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static px.objects.sourceManagement.SourceManagementTestData.BLOCKED_SOURCE;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.outbound.OutboundTransactionTestData.*;
import static px.reports.outbound.OutboundTransactionsColumnsEnum.BUYER_CAMPAIGN;
import static px.reports.outbound.OutboundTransactionsColumnsEnum.POST_RESULT;
import static px.reports.pingPostTransactions.PingPostTransactionsTestData.POST_TYPE_FILTER;
import static utils.CustomMatcher.equalsToEscapeSpace;

/**
 * Created by kgr on 12/21/2016.
 */
public class OutboundTransactionsReportPage extends LookUpReportPage implements Transactionable, Actions {

    public OutboundTransactionsReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.TRANSACTIONS_OUTBOUND_REPORT_LINK);
        super.checkPage();
    }

    public void checkPublishersFilter(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publishers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getPublishers()
                .stream().map(option -> option.getId() + " - " + option.getName())
                .collect(Collectors.toList()));
        // check table
        checkCellsData(testData, testData.getItemsByPublishersID());
        // filter reset
        String filterValue = testData.getPublishers().stream().map(p -> p.getId() +
                "-" + p.getName()).collect(Collectors.joining(", "));
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkCampaignsFilter(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer Campaigns' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER);
        setFilter(filterLocator, testData.getCampaigns()
                .stream().map(option -> option.getId() + " - " + option.getName())
                .collect(Collectors.toList()));
        // check table
        checkCellsData(testData, testData.getItemsByBuyerCampaignsID());
        // filter reset
        String filterValue = testData.getCampaigns().stream().map(p -> p.getId() +
                "-" + p.getName()).collect(Collectors.joining(", "));
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkResultCodesFilter(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer Result Codes' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_RESULT_CODE_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getResultCodeLiteralsList());
        // check table
        checkCellsData(testData, testData.getItemsByResultCodes());
        // filter reset
        String filterValue = testData.getResultCodeLiteralsList().toString();
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkPostTypesFilter(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Post Types' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, POST_TYPE_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getPostTypeList());
        // check table
        checkCellsData(testData, testData.getItemsByPostTypes());
        // filter reset
        String filterValue = testData.getPostTypeList().toString();
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkVerticalsFilter(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Verticals' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getVertical());
        // check table
        checkCellsData(testData, testData.getItemsByVertical());
        // filter reset
        resetFilter(filterLocator, testData.getVertical(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkTransactionTypesFilter(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All types' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, TRANSACTION_TYPE_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getTransactionType());
        // check table
        checkCellsData(testData, testData.getItemsByTransactionType());
        // filter reset
        resetFilter(filterLocator, testData.getTransactionType(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    // change logic to set randomly and reset combination regardless to filters number?
    public void checkAllFilters(OutboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), testData.getCampaigns()
                .stream().map(option -> option.getId() + " - " + option.getName())
                .collect(Collectors.toList()));
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_RESULT_CODE_FILTER), testData.getResultCodeLiteralsList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, POST_TYPE_FILTER), testData.getPostTypeList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVertical());
        if (Config.isAdmin()) {
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), testData.getPublishers()
                    .stream().map(option -> option.getId() + " - " + option.getName())
                    .collect(Collectors.toList()));
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, TRANSACTION_TYPE_FILTER), testData.getTransactionType());
        }
        // check table
        checkCellsData(testData, testData.getItemsByAllFilters());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getItemsCurrentTotalCount());
            checkCellsData(testData, resetData.getItemsByFiltersCombination());
        }
        hamcrest.assertAll();
    }

    @Override
    protected void checkCellsData(ReportTestData pTestData, JSONArray dataList) {
        if (!(pTestData instanceof OutboundTransactionTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OutboundTransactionTestData testData = (OutboundTransactionTestData) pTestData;
        long start = System.currentTimeMillis();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        List<FieldFormatObject> fields = FieldFormatObject.withoutNonFields(testData.getFields());
        List<List<String>> tableCellsText = tableDataObject.getTableCellsText();
        JSONArray expectedData = tableDataObject.getDataList();
        // exclude 'apiResponse' field
        List<String> exceptionalFields = Arrays.asList(OutboundTransactionsColumnsEnum.POST_TYPE.getValue(), POST_RESULT.getValue());
        List<FieldFormatObject> commonFields = fields.stream()
                .filter(field -> !exceptionalFields.contains(field.getName())).collect(Collectors.toList());
        for (int i = 0; i < expectedData.length(); i++) {
            JSONObject jsonObject = expectedData.getJSONObject(i);
            for (FieldFormatObject field : commonFields) {
                try {
                    // check cell text
                    String expectedValue = String.valueOf(jsonObject.get(field.getName()));
                    // generic solution
                    expectedValue = field.hasMappedValues() ? field.getMappedValue(expectedValue) : expectedValue;
                    String actualValue = tableCellsText.get(i).get(field.getIndex());
                    hamcrest.assertThat(String.format("Data in '%s' column at row '%d' in table equals to original data in json", field.getName(), i + 1),
//                            tableCellsText.get(i).get(field.getIndex()), getMatherByMetricType(field, expectedValue));
                            actualValue, equalsToEscapeSpace(getValueByType(field, expectedValue)));
                } catch (JSONException e) {
                    hamcrest.assertThat(String.format("There is no data in response JSON array by index '%d'\tDetails:\n%s", i, e.getMessage()), false);
//                    break;
                } catch (IndexOutOfBoundsException e2) {
                    hamcrest.assertThat(String.format("There is no row in table array by index '%d'", i), false);
                    break;
                }
            }
            // exceptional case when post codes don't match
            String description = String.format(".\tTable row - %d", i + 1);
            FieldFormatObject postTypeField = FieldFormatObject.getFieldObjectFromListByName(fields, OutboundTransactionsColumnsEnum.POST_TYPE.getValue());
            FieldFormatObject resultCodeField = FieldFormatObject.getFieldObjectFromListByName(fields, POST_RESULT.getValue());
            String postType = String.valueOf(jsonObject.get(postTypeField.getName()));
            String resultCode = String.valueOf(jsonObject.get(resultCodeField.getName()));
            log.info(String.format("postType = '%s'\tresultCode = '%s'", postType, resultCode));
            hamcrest.assertThat("Post Type in table equals to original in json" + description,
                    tableCellsText.get(i).get(postTypeField.getIndex()),
                    OutboundTransactionTestData.isExclusionCode(postType)
                            ? equalToIgnoringCase(OutboundTransactionTestData.getExclusionCodeValueByKey(postType))
                            : CustomMatcher.containsStringToIgnoreCaseAndBlanketInAnyOrder(postType));
            try {
                hamcrest.assertThat("Buyer Post result code in table equals to original in json" + description,
                        tableCellsText.get(i).get(resultCodeField.getIndex()),
                        OutboundTransactionTestData.isExclusionCode(resultCode)
                                ? equalToIgnoringCase(OutboundTransactionTestData.getExclusionCodeValueByKey(resultCode))
                                : CustomMatcher.containsStringToIgnoreCaseAndBlanketInAnyOrder(resultCode));
            } catch (IllegalArgumentException e) {
                hamcrest.assertThat(String.format("Unknown Buyer Post result code\tExpected = '%s',\tactual = '%s'",
                        resultCode, tableCellsText.get(i).get(resultCodeField.getIndex())), false);
            }
            // check request/response data popup, skip if transactionID does not match - depends on postType
            if (testData.hasTransactionData(String.valueOf(jsonObject.get("postType"))) &&
                    !TRANSACTIONS_SET.contains(String.valueOf(jsonObject.get("leadResponseId"))))
                hamcrest.append(checkTransactionData(tableElement, testData.getTransactionData(), jsonObject, i));
        }
        log.info(String.format("Time to check %d rows = %d", expectedData.length(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        String[] exclusions = {
                OutboundTransactionsColumnsEnum.SUB_ID.getValue(), OutboundTransactionsColumnsEnum.EMAIL.getValue()
        };
        if (testData.isBigData()) checkCalendarDateRangesBigData(testData, exclusions);
        else checkCalendarDateRanges(testData, exclusions);
    }

    @Override
    public void checkSearchFilterWithFilters(ReportTestData pTestData) {
        if (!(pTestData instanceof OutboundTransactionTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        OutboundTransactionTestData testData = (OutboundTransactionTestData) pTestData;
        List<String> multipleFilers = Arrays.asList(PUBLISHER_FILTER, BUYER_CAMPAIGN_FILTER,
                BUYER_RESULT_CODE_FILTER, POST_TYPE_FILTER);
        ObjectIdentityData buyerCampaign = ObjectIdentityData.getAnyObjectFromList(testData.getCampaigns());
        List<String> filterLocators = new ArrayList<>(Arrays.asList(BUYER_CAMPAIGN_FILTER,
                BUYER_RESULT_CODE_FILTER, POST_TYPE_FILTER, VERTICAL_FILTER));
        List<String> filterValues = new ArrayList<>(Arrays.asList(
                buyerCampaign.getId() + " - " + buyerCampaign.getName(),
                getRandomValueFromList(testData.getResultCodeLiteralsList()),
                getRandomValueFromList(testData.getPostTypeList()),
                testData.getVertical()));
        if (Config.isAdmin()) {
            ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(testData.getPublishers());
            filterLocators.addAll(Arrays.asList(PUBLISHER_FILTER, TRANSACTION_TYPE_FILTER));
            filterValues.addAll(Arrays.asList(publisher.getId() +
                    " - " + publisher.getName(), testData.getTransactionType()));
        }
        // check each filter with lookUp filter in random order
        while (!filterLocators.isEmpty()) {
            int index = getRandomInt(filterLocators.size());
            String filterLocator = filterLocators.get(index);
            String filterValue = filterValues.get(index);
            checkFilterDoesNotChangeSearchFilter(testData.getFields(), filterLocator, filterValue, multipleFilers);
            // remove already verified filters
            filterLocators.remove(filterLocator);
            filterValues.remove(filterValue);
        }
    }

    // ==================================== Lead insert ====================================
    // or at least 1 success transaction (record)?
    public void checkTransactionByEmail(String email, boolean successExpected) {
        // set lookup
        filterTable(email, SearchByEnum.EMAIL.getValue());
        // check that transaction is success (what to do if more than 1 row correspond to transaction)?
        List<String> postResults = tableElement.getColumnCellsText(POST_RESULT.getValue());
        assertThat(String.format("Check that transaction by email '%s' has '%s' success result code\tAll post results = %s",
                email, successExpected ? "" : "not", postResults),
                successExpected == postResults.contains("Success"));
        /*assertThat(String.format("Check that transaction by email '%s' has '%s' success result code", email, successExpected ? "" : "not"),
                tableElement.getCellTextAt(1, getHeaderIndex(tableElement, POST_RESULT.getValue()) + 1),
                successExpected ? equalToIgnoringCase("Success") : not(equalToIgnoringCase("Success")));    */
    }

    public void checkTransaction(SourceManagementTestData testData, String email, boolean successExpected) {
        // set lookup
        filterTable(email, SearchByEnum.EMAIL.getValue());
        List<String> campaigns = tableElement.getColumnCellsText(BUYER_CAMPAIGN.getValue());
        List<String> postResults = tableElement.getColumnCellsText(POST_RESULT.getValue());
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < campaigns.size(); i++) {
            if (campaigns.get(i).equals(testData.getCampaign().getName())) {
                map.put(postResults.get(i), testData.getCampaign().getName());
            }
        }
        // bring into view
        int columnIndex = getHeaderIndex(tableElement, POST_RESULT.getValue());
        helper.moveToElement(tableElement.getCellAt(1, columnIndex + 1));
        assertThat(String.format("Check that transactions by email '%s' have proper result codes - %s '%s' according to buyer campaign sourceId = '%s'",
                email, successExpected ? "no" : "present", BLOCKED_SOURCE, testData.getSourceID()),
                successExpected != map.containsKey(BLOCKED_SOURCE));
    }

}