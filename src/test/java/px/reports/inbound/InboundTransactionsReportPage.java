package px.reports.inbound;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import pages.groups.Transactionable;
import pages.locators.ReportPageLocators;
import px.reports.LookUpReportPage;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.TableDataObject;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.PXDataHelper.getMatherByMetricType;
import static configuration.helpers.PXDataHelper.getValueByType;
import static pages.locators.ElementLocators.*;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.ReportTestData.TRANSACTIONS_SET;
import static px.reports.inbound.InboundTransactionTestData.*;
import static px.reports.inbound.InboundTransactionsColumnsEnum.RESPONSE;
import static px.reports.inbound.InboundTransactionsColumnsEnum.TRANSACTION_ID;
import static utils.CustomMatcher.equalsToEscapeSpace;

/**
 * Created by kgr on 12/21/2016.
 */
public class InboundTransactionsReportPage extends LookUpReportPage implements Transactionable {

    public InboundTransactionsReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.TRANSACTIONS_INBOUND_REPORT_LINK);
        waitPageIsLoaded(60);
        displayAllTableColumns();
//        super.checkPage();
    }

    public void checkPublisherFilter(InboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publishers' filter");
        ObjectIdentityData publisherData = testData.getPublisher();
        String filterValue = publisherData.getId() + " - " + publisherData.getName();
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByPublisherID());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER),
                filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkReasonCodesFilter(InboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Result Codes' filter");
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, RESULT_CODES_FILTER), testData.getResultCode());
        // check table
        checkCellsData(testData, testData.getItemsByResultCode());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, RESULT_CODES_FILTER),
                testData.getResultCode(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkVerticalsFilter(InboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Verticals' filter");
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVertical());
        // check table
        checkCellsData(testData, testData.getItemsByVertical());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER),
                testData.getVertical(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkOffersFilter(InboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Offers' filter");
        ObjectIdentityData offerData = testData.getOffer();
        String filterValue = offerData.getId() + " - " + offerData.getName();
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER), filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByOfferID());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER), filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    // change logic to set randomly combination regardless to filters number?
    public void checkAllFilters(InboundTransactionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        ObjectIdentityData offerData = testData.getOffer();
        String offerFilterValue = offerData.getId() + " - " + offerData.getName();
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, RESULT_CODES_FILTER), testData.getResultCode());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVertical());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER), offerFilterValue);
        // admin only
        if (Config.isAdmin()) {
            ObjectIdentityData publisherData = testData.getPublisher();
            String publisherFilterValue = publisherData.getId() + " - " + publisherData.getName();
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), publisherFilterValue);
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
        if (!(pTestData instanceof InboundTransactionTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        InboundTransactionTestData testData = (InboundTransactionTestData) pTestData;
        long start = System.currentTimeMillis();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        List<FieldFormatObject> fieldFormatObjects = FieldFormatObject.withoutNonFields(testData.getFields());
        List<List<String>> tableCellsText = tableDataObject.getTableCellsText();
        JSONArray expectedData = tableDataObject.getDataList();
        // exclude 'apiResponse' field
        List<FieldFormatObject> commonFields = fieldFormatObjects.stream()
                .filter(field -> !field.getName().equals(RESPONSE.getValue())).collect(Collectors.toList());
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
//                            actualValue, field.getMatherByMetricType(expectedValue));
                            actualValue, equalsToEscapeSpace(getValueByType(field, expectedValue)));
                } catch (JSONException e) {
                    hamcrest.assertThat(String.format("There is no data in response JSON array by index '%d'\tDetails:\n%s", i, e.getMessage()), false);
//                    break;
                } catch (IndexOutOfBoundsException e2) {
                    hamcrest.assertThat(String.format("There is no row in table array by index '%d'", i), false);
                    break;
                }
            }
            // apiResponse verification differs - include matcher and tooltip/popup verification
            FieldFormatObject apiResponseField = FieldFormatObject.getFieldObjectFromListByName(fieldFormatObjects, RESPONSE.getValue());
            String description = String.format(".\tTable row - %d", i + 1);
            String expectedValue = String.valueOf(jsonObject.get(apiResponseField.getName()));
            String responseInTable = tableCellsText.get(i).get(apiResponseField.getIndex());
            // common cell value verification
            hamcrest.assertThat(String.format("Data in '%s' column at row '%d' in table equals to original data in json", apiResponseField.getName(), i + 1),
                    responseInTable, getMatherByMetricType(apiResponseField, expectedValue.equalsIgnoreCase("BaeOK") ? expectedValue : "BaeNOK"));
            // to prevent selenium exceptions if 'BaeOK' in table
            if (!responseInTable.equalsIgnoreCase("BaeOK")) {
                int y = apiResponseField.getIndex() + 1;
                // popup verification
                try {
                    log.info("Check 'BaeNOK' reason popup");
                    // could produce errors cause cell should be clickable
//                    tableElement.clickOnCellAt(i + 1, y);
                    tableElement.clickOnClickableCellAt(i + 1, y, 2);
                    helper.waitUntilDisplayed(By.xpath(DELETE_POPUP), 2);
                    waitPopupTextLoaded();
                    hamcrest.assertThat("API response in popup equals to original in json" + description,
//                            helper.getElement(POPUP_TEXT).getText(), getMatherByMetricType(apiResponseField, expectedValue));
                            helper.getElement(POPUP_TEXT).getText(), equalsToEscapeSpace(getValueByType(apiResponseField, expectedValue)));
                    /*helper.click(POPUP_CLOSE);
                    helper.waitUntilToBeInvisible(DELETE_POPUP);*/
                    closePopupWithAttempts();
                } catch (NoSuchElementException | TimeoutException e) {
                    log.info(String.format("DEBUG\tFailed BaeNOK popup verification, row =%d\tCause = %s", i + 1, e.getMessage()));
                    hamcrest.assertThat("No popup with 'BaeNOK' response details" + description, false);
                } catch (WebDriverException e) {
                    log.info("DEBUG\tPopup verification failed due to\t" + e.getMessage());
                    throw new RuntimeException(String.format("%s cell (%d, %d), field - '%s'\tDetails = %s",
                            e.getMessage(), i + 1, y, apiResponseField.getName(), e.getMessage()), e);
                 /*   hamcrest.assertThat(String.format("%s cell (%d, %d), field - '%s'\tDetails = %s",
                            e.getMessage(), i + 1, y, apiResponseField.getName(), e.getMessage()), false);*/
                }
                // tooltip verification
                try {
                    log.info("Check 'BaeNOK' reason tooltip");
                    helper.moveToElement(tableElement.getCellAt(i + 1, y));
                    helper.waitUntilDisplayed(TABLE_TOOLTIP, 2);
                    log.info("Check 'BaeNOK' reason in tooltip");
                    hamcrest.assertThat("API response in tooltip equals to original in json" + description,
//                            helper.getElement(TABLE_TOOLTIP).getText(), getMatherByMetricType(apiResponseField, expectedValue));
                            helper.getElement(TABLE_TOOLTIP).getText(), equalsToEscapeSpace(getValueByType(apiResponseField, expectedValue)));
                } catch (NoSuchElementException | TimeoutException e) {
                    log.info(String.format("DEBUG\tFailed BaeNOK tooltip verification, row =%d\tCause = %s", i + 1, e.getMessage()));
                    hamcrest.assertThat("No tooltip with 'BaeNOK' response details" + description, false);
                }
            }
            // check inbound data popup, skip if transactionID does not match
            if (!TRANSACTIONS_SET.contains(String.valueOf(jsonObject.get(TRANSACTION_ID.getValue()))))
                hamcrest.append(checkInboundData(tableElement, testData.getInboundData(), jsonObject, i));
        }
        log.info(String.format("Time to check %d rows with tooltips = %d", expectedData.length(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        String[] exclusions = {
                InboundTransactionsColumnsEnum.DATE.getValue(),
                InboundTransactionsColumnsEnum.VERTICAL.getValue(),
                TRANSACTION_ID.getValue(),
                InboundTransactionsColumnsEnum.PUBLISHER_ID.getValue(),
                InboundTransactionsColumnsEnum.OFFER_ID.getValue(),
                InboundTransactionsColumnsEnum.EMAIL.getValue(),
                InboundTransactionsColumnsEnum.PHONE.getValue()
        };
        if (testData.isBigData()) checkCalendarDateRangesBigData(testData, exclusions);
        else checkCalendarDateRanges(testData, exclusions);
    }

    @Override
    public void checkSearchFilterWithFilters(ReportTestData pTestData) {
        if (!(pTestData instanceof InboundTransactionTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        InboundTransactionTestData testData = (InboundTransactionTestData) pTestData;
        List<String> filterLocators = new ArrayList<>(Arrays.asList(RESULT_CODES_FILTER, VERTICAL_FILTER, OFFER_FILTER));
        List<String> filterValues = new ArrayList<>(Arrays.asList(testData.getResultCode(), testData.getVertical(),
                testData.getOffer().getId() + " - " + testData.getOffer().getName()));
        if (Config.isAdmin()) {
            filterLocators.add(PUBLISHER_FILTER);
            filterValues.add(testData.getPublisher().getId() + " - " + testData.getPublisher().getName());
        }
        // check each filter with lookUp filter in random order
        while (!filterLocators.isEmpty()) {
            int index = getRandomInt(filterLocators.size());
            String filterLocator = filterLocators.get(index);
            String filterValue = filterValues.get(index);
            checkFilterDoesNotChangeSearchFilter(testData.getFields(), filterLocator, filterValue);
            // remove already verified filters
            filterLocators.remove(filterLocator);
            filterValues.remove(filterValue);
        }
    }
}