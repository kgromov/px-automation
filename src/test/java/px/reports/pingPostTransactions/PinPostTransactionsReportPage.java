package px.reports.pingPostTransactions;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.JSONWrapper;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import pages.groups.Transactionable;
import pages.locators.ReportPageLocators;
import px.reports.LookUpReportPage;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.TableDataObject;
import px.reports.inbound.InboundTransactionsColumnsEnum;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.DataHelper.getRandomValueFromList;
import static configuration.helpers.PXDataHelper.getValueByType;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.pingPostTransactions.PingPostTransactionsColumnsEnum.EMAIL;
import static px.reports.pingPostTransactions.PingPostTransactionsTestData.*;
import static utils.CustomMatcher.equalsToEscapeSpace;

/**
 * Created by kgr on 5/10/2017.
 */
public class PinPostTransactionsReportPage extends LookUpReportPage implements Transactionable {

    public PinPostTransactionsReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.TRANSACTIONS_PING_POST_REPORT_LINK);
        super.checkPage();
    }

    public void checkPublishersFilter(PingPostTransactionsTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publishers' filter");
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // check parent filter
        setFilter(publisherFilterLocator, publisherFilterValue);
        // check items
        checkFilterItems(publisherInstanceFilterLocator, ObjectIdentityData.getAllIDs(testData.getSubIDs()));
        // check table
        checkCellsData(testData, testData.getItemsByPublishersID());
        hamcrest.assertAll();
    }

    public void resetPublishersFilter(PingPostTransactionsTestData testData) {
        String filterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void checkPublisherInstanceFilter(PingPostTransactionsTestData testData) {
        if (!testData.hasPublisherInstances()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publisher Instance' filter");
        String filterValue = testData.getSubID().getId();
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // filter by instance if exists
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByPublisherInstanceGUID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsByPublishersID().length());
        // check table
        checkCellsData(testData, testData.getItemsByPublishersID());
        hamcrest.assertAll();
    }

    public void checkResultCodesFilter(PingPostTransactionsTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer Result Codes' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_RESULT_CODE_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getResultCodeLiteralsList());
        // check table
        checkCellsData(testData, testData.getItemsByResultCodes());
        // filter reset
        String filterValue = testData.getResultCodeLiteralsList().toString();
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkPostTypesFilter(PingPostTransactionsTestData testData) {
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

    public void checkVerticalsFilter(PingPostTransactionsTestData testData) {
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

    public void checkAllFilters(PingPostTransactionsTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // filter values
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherInstanceFilterValue = testData.hasPublisherInstances() ? testData.getSubID().getId() : null;
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // set all filters in chain
        setFilter(publisherFilterLocator, publisherFilterValue);
        if (testData.hasPublisherInstances())
            setFilter(publisherInstanceFilterLocator, publisherInstanceFilterValue);
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_RESULT_CODE_FILTER), testData.getResultCodeLiteralsList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, POST_TYPE_FILTER), testData.getPostTypeList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVertical());
        // check table
        checkCellsData(testData, testData.getItemsByAllFilters());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getItemsTotalCount());
            checkCellsData(testData, resetData.getItemsByFiltersCombination());
        }
        hamcrest.assertAll();
    }

    @Override
    public void checkCellsData(ReportTestData pTestData, JSONArray dataList) {
        if (!(pTestData instanceof PingPostTransactionsTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PingPostTransactionsTestData testData = (PingPostTransactionsTestData) pTestData;
        long start = System.currentTimeMillis();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        List<FieldFormatObject> fieldFormatObjects = FieldFormatObject.withoutNonFields(testData.getFields());
        List<List<String>> tableCellsText = tableDataObject.getTableCellsText();
        JSONArray expectedData = tableDataObject.getDataList();
        // exclude 'apiResponse' field
        List<FieldFormatObject> commonFields = fieldFormatObjects.stream()
                .filter(field -> !field.getName().equals(InboundTransactionsColumnsEnum.RESPONSE.getValue())).collect(Collectors.toList());
        for (int i = 0; i < expectedData.length(); i++) {
            JSONObject jsonObject = expectedData.getJSONObject(i);
            for (FieldFormatObject field : commonFields) {
                try {
                    // check cell text
                    String expectedValue = JSONWrapper.getString(jsonObject, field.getName());
                    // generic solution
                    expectedValue = field.hasMappedValues() ? field.getMappedValue(expectedValue) : expectedValue;
                    String actualValue = tableCellsText.get(i).get(field.getIndex());
                    hamcrest.assertThat(String.format("Data in '%s' column at row '%d' in table equals to original data in json", field.getName(), i + 1),
//                            actualValue, field.getMatherByMetricType(expectedValue));
                            actualValue, equalsToEscapeSpace(getValueByType(field, expectedValue)));
                    // check cell tooltip
                    if (field.hasTooltip()) {
                        if(field.hasFormula()) field.getFormula().calculateFormula(jsonObject);
                        hamcrest.append(checkCellsTooltipData(tableElement, field, expectedValue, i));
                    }
                    // check cell popup
                    if (field.hasPopup()) hamcrest.append(checkCellsPopup(tableElement, field, actualValue, i));
                } catch (JSONException e) {
                    hamcrest.assertThat(String.format("There is no data in response JSON array by index '%d'\tDetails:\n%s", i, e.getMessage()), false);
//                    break;
                } catch (IndexOutOfBoundsException e1) {
                    hamcrest.assertThat(String.format("There is no row in table array by index '%d'", i), false);
                    break;
                } catch (NoSuchElementException e2) {
                    hamcrest.assertThat(String.format("No data inside tooltip for field '%s' at row '%d'", field.getName(), i), false);
                }
            }
            // check inbound data popup, skip if transactionID does not match
            if (!TRANSACTIONS_SET.contains(String.valueOf(jsonObject.get(InboundTransactionsColumnsEnum.TRANSACTION_ID.getValue()))))
                hamcrest.append(checkInboundData(tableElement, testData.getInboundData(), jsonObject, i));
        }
        log.info(String.format("Time to check %d rows with tooltips = %d", expectedData.length(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    @Override
    public void checkSearchFilterWithFilters(ReportTestData pTestData) {
        if (!(pTestData instanceof PingPostTransactionsTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PingPostTransactionsTestData testData = (PingPostTransactionsTestData) pTestData;
        List<String> multipleFilers = Arrays.asList(PUBLISHER_RESULT_CODE_FILTER, POST_TYPE_FILTER);
        List<String> filterLocators = new ArrayList<>(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_RESULT_CODE_FILTER,
                POST_TYPE_FILTER, VERTICAL_FILTER));
        List<String> filterValues = new ArrayList<>(Arrays.asList(
                testData.getPublisher().getId() + " - " + testData.getPublisher().getName(),
                getRandomValueFromList(testData.getResultCodeLiteralsList()),
                getRandomValueFromList(testData.getPostTypeList()),
                testData.getVertical()));
        if (testData.hasPublisherInstances()) {
            filterLocators.add(PUBLISHER_INSTANCE_FILTER);
            filterValues.add(testData.getSubID().getId());
        }
        // check each filter with lookUp filter in random order
        while (!filterLocators.isEmpty()) {
            int index = getRandomInt(filterLocators.size());
            String filterLocator = filterLocators.get(index);
            String filterValue = filterValues.get(index);
            // dependent child filters
            if (filterLocator.equals(PUBLISHER_INSTANCE_FILTER)) {
                setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER),
                        testData.getPublisher().getId() + " - " + testData.getPublisher().getName());
            }
            checkFilterDoesNotChangeSearchFilter(testData.getFields(), filterLocator, filterValue, multipleFilers);
            // remove already verified filters
            filterLocators.remove(filterLocator);
            filterValues.remove(filterValue);
            // dependent filters
            if (filterLocator.equals(PUBLISHER_FILTER) && testData.hasPublisherInstances()) {
                filterLocators.remove(PUBLISHER_INSTANCE_FILTER);
                filterValues.remove(testData.getSubID().getId());
            }
        }
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        checkCalendarDateRanges(testData, EMAIL.getValue());
    }
}