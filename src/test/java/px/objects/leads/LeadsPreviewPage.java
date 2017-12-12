package px.objects.leads;

import configuration.browser.PXDriver;
import configuration.helpers.DataHelper;
import configuration.helpers.HTMLHelper;
import elements.input.InputElement;
import elements.menu.ListMenuElement;
import elements.table.TableElement;
import org.hamcrest.Matcher;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.OverviewPage;
import pages.groups.Overviewable;
import pages.groups.Transactionable;
import pages.locators.ElementLocators;
import px.reports.outbound.OutboundTransactionTestData;
import utils.CustomMatcher;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static configuration.helpers.DataHelper.getRoundedFloatToPatten;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static pages.locators.ElementLocators.DATA_FIELD_ELEMENT;
import static pages.locators.ElementLocators.ITEMS_TABLE;
import static px.objects.leads.LeadsPreviewPageLocators.*;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsPreviewPage extends OverviewPage implements Overviewable, Transactionable {
    @FindBy(xpath = ElementLocators.SETTING_MENU)
    protected ListMenuElement listMenuElement;

    public LeadsPreviewPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void checkLeadDetails(LeadsTestData testData) {
        log.info("Check lead details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, String> leadDetailsMap = testData.getLeadDetailsMap();
        // map to get missed data elements
        Map<String, String> missedDataMap = new HashMap<>(leadDetailsMap);
        helper.waitUntilDisplayed(listMenuElement);
//        hamcrest.assertThat("Number of fields with data to verify with json is the same", dataFieldsList.size(), equalTo(leadDetailsMap.size()));
        // Lead Overview
        log.info("Check 'Lead Overview'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(LEAD_OVERVIEW_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_OVERVIEW_CONTAINER), leadDetailsMap, missedDataMap));
        // Contact Information
        log.info("Check 'Contact Information'");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(LEAD_CONTACT_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_CONTACT_CONTAINER), leadDetailsMap, missedDataMap));
        // Lead Details
        log.info("Check 'Lead Details'");
        listMenuElement.setByIndex(3);
        helper.waitUntilDisplayed(LEAD_DETAILS_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_DETAILS_CONTAINER), leadDetailsMap, missedDataMap));
        // Buyer Details
        log.info("Check 'Buyer Details'");
        listMenuElement.setByIndex(4);
        helper.waitUntilDisplayed(BUYER_DETAILS_CONTAINER);
        if (testData.isLeadSoldToMoreThan1Buyer()) {
            log.info("DEBUG\tMore than 2 buyers");
            tableElement = new TableElement(helper.getElement(BUYER_DETAILS_CONTAINER, ITEMS_TABLE));
            checkCellsData(testData.getBuyerDetailsArray(), HTMLHelper.getTableCells(tableElement), testData.getBuyerFields());
        } else
            hamcrest.append(checkPreviewElements(helper.getElement(BUYER_DETAILS_CONTAINER), leadDetailsMap, missedDataMap));
        // BLOCK_BUTTON
        // Disposition History
        log.info("Check 'Disposition History'");
        if (testData.getLead().isCanChangeDisposition())
            checkDispositionHistory(testData);
        // Consent Language
        log.info("Check 'Consent Language'");
        listMenuElement.setByIndex(6);
        helper.waitUntilDisplayed(LEAD_TCPA_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_TCPA_CONTAINER), leadDetailsMap, missedDataMap));
        // Inbound Request
        log.info("Check 'Inbound Request'");
        listMenuElement.setByIndex(7);
        helper.waitUntilDisplayed(LEAD_REQUEST_CONTAINER);
        boolean isPassword = leadDetailsMap.containsKey("Password") || leadDetailsMap.containsKey("password") || DataHelper.hasTagValue("Password", leadDetailsMap.get("xmlBody"));
        hamcrest.assertThat("Password is absent in Request", !isPassword);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_REQUEST_CONTAINER), leadDetailsMap, missedDataMap));
        // Lead Attributes
        log.info("Check 'Lead Attributes'");
        listMenuElement.setByIndex(8);
        helper.waitUntilDisplayed(LEAD_ATTRIBUTES_CONTAINER);
        // click to show all lead attributes
        if (helper.isElementAccessible(SHOW_ALL_BUTTON)) helper.getElement(SHOW_ALL_BUTTON).click();
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_ATTRIBUTES_CONTAINER), leadDetailsMap, missedDataMap));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.size() == 0);
        hamcrest.assertAll();
    }

    /* 1) override with request/response popups
     * 2) PostType, ResultCode logic from OutboundTransactions report
    */
    public void checkLeadTransactions2(LeadsTestData testData) {
        log.info("Check lead transactions");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        listMenuElement.setByText(LEAD_TRANSACTIONS_ITEM);
        helper.waitUntilDisplayed(LEAD_TRANSACTIONS_CONTAINER);
        tableElement = new TableElement(helper.getElement(LEAD_TRANSACTIONS_CONTAINER, ITEMS_TABLE));
        if (testData.getLeadTransactions().isEmpty()) {
            checkEmptyTable();
            return;
        }
        // or even check pagination
        if (tableElement.getTotalRowsCount() > 10)
            checkPagination(testData, testData.getLeadTransactionsCount());
        log.info("Table 'Transactions' verification");
        if (tableElement.getTotalRowsCount() > 10) setItemPerPage(100);
        checkCellsData(testData.getLeadTransactionsArray(), HTMLHelper.getTableCells(tableElement), testData.getTransactionFields());
        hamcrest.assertAll();
    }

    public void checkDispositionHistory(LeadsTestData testData) {
        log.info("Check disposition history");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        listMenuElement.setByIndex(5);
        helper.waitUntilDisplayed(DISPOSITION_HISTORY_CONTAINER);
        tableElement = new TableElement(helper.getElement(DISPOSITION_HISTORY_CONTAINER, ITEMS_TABLE));
        // sold dependent
        if (testData.isSoldLead())
            hamcrest.assertThat("There are more than 1 rows in disposition history cause Lead was sold", tableElement.getTotalRowsCount(), greaterThan(1));
        hamcrest.assertThat("All buyerIds are consistent to lead ones", testData.getDispositionHistoryBuyers(), equalTo(testData.getLeadBuyers()));
        checkCellsData(testData.getDispositionHistory(), HTMLHelper.getTableCells(tableElement), testData.getDispositionFields());
        hamcrest.assertAll();
    }

    public void checkLeadTransactions(LeadsTestData testData) {
        log.info("Check lead transactions");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        listMenuElement.setByText(LEAD_TRANSACTIONS_ITEM);
        helper.waitUntilDisplayed(LEAD_TRANSACTIONS_CONTAINER);
        WebElement container = helper.getElement(LEAD_TRANSACTIONS_CONTAINER);
        waitPageIsLoaded();
        tableElement = new TableElement(helper.getElement(LEAD_TRANSACTIONS_CONTAINER, ITEMS_TABLE));
        log.info("Table 'Transactions' verification");
        if (testData.getLeadTransactions().isEmpty()) {
            checkEmptyTable();
            return;
        }
        // or even check pagination
        if (tableElement.getTotalRowsCount() > 10)
            checkPagination(testData, testData.getLeadTransactionsCount());
        List<LeadsTestData.LeadTransaction> transactionList = testData.getLeadTransactions();
        // get know which items per page is active
        int rowsPerPage = transactionList.size() > 0 ? getActiveItemsPerPage() : 0;
        List<LeadsTestData.LeadTransaction> transactionsOnPage = transactionList.size() > rowsPerPage
                ? new ArrayList<>(transactionList.subList(0, rowsPerPage)) : transactionList;
        log.info(String.format("Check table by rows according to json array, expected row numbers - %d", transactionsOnPage.size()));
        assertThat("Check that rows number in table equals to received in response", tableElement.getTotalRowsCount(), equalTo(testData.getLeadTransactionsCount()));
        // get data from table cells
//        List<List<String>> tableCellsText = helper.getTextFromElementsListParallel(tableElement.getTableCellsByRows());
        List<List<String>> tableCellsText = HTMLHelper.getTableCells(tableElement.getWrappedElement());
        for (int i = 0; i < transactionsOnPage.size(); i++) {
            try {
                List<String> rowCellsText = tableCellsText.get(i);
                LeadsTestData.LeadTransaction transaction = transactionsOnPage.get(i);
                String description = String.format(".\tTable row - %d", i + 1);
                hamcrest.assertThat("Lead id in table equals to original in json" + description,
                        rowCellsText.get(LeadTransactionsColumnsEnum.LEAD_ID.ordinal()),
                        equalToIgnoringCase(transaction.getLeadId()));
                hamcrest.assertThat("Date in table equals to original in json" + description,
                        rowCellsText.get(LeadTransactionsColumnsEnum.DATE.ordinal()),
                        equalToIgnoringCase(transaction.getPostDate()));
                hamcrest.assertThat("Payout in table equals to original in json" + description,
                        rowCellsText.get(LeadTransactionsColumnsEnum.PAYOUT.ordinal()),
//                        containsString(transaction.getPayout()));
                        CustomMatcher.matchesPattern(transaction.getPayout(), getRoundedFloatToPatten(transaction.getPayout())));
                hamcrest.assertThat("Buyer name in table equals to original in json" + description,
                        rowCellsText.get(LeadTransactionsColumnsEnum.BUYER_NAME.ordinal()),
                        equalToIgnoringCase(transaction.getBuyerName()));
                // exceptional case when post codes don't match
                String postType = transaction.getPostType();
                String resultCode = transaction.getBuyerPostResultCode();
                log.info(String.format("postType = '%s'\tresultCode = '%s'", postType, resultCode));
                hamcrest.assertThat("Post Type in table equals to original in json" + description,
                        rowCellsText.get(LeadTransactionsColumnsEnum.POST_TYPE.ordinal()),
                        OutboundTransactionTestData.isExclusionCode(postType)
                                ? equalToIgnoringCase(OutboundTransactionTestData.getExclusionCodeValueByKey(postType))
                                : CustomMatcher.containsStringToIgnoreCaseAndBlanketInAnyOrder(postType));
                hamcrest.assertThat("Buyer Post result code in table equals to original in json" + description,
                        rowCellsText.get(LeadTransactionsColumnsEnum.RESULT_CODE.ordinal()),
                        OutboundTransactionTestData.isExclusionCode(resultCode)
                                ? equalToIgnoringCase(OutboundTransactionTestData.getExclusionCodeValueByKey(resultCode))
                                : CustomMatcher.containsStringToIgnoreCaseAndBlanketInAnyOrder(resultCode));
                // request text in popup verification
                if (transaction.hasRequest()) {
                    WebElement cell = tableElement.getCellAt(i + 1, LeadTransactionsColumnsEnum.ACTIONS.ordinal() + 1);
                    hamcrest.append(checkTransactionData(cell, "Request", transaction.getRequestData(), description));
                }
                // response text in popup verification
                if (transaction.hasResponse()) {
                    WebElement cell = tableElement.getCellAt(i + 1, LeadTransactionsColumnsEnum.ACTIONS.ordinal() + 1);
                    hamcrest.append(checkTransactionData(cell, "Response", transaction.getResponseData(), description));
                }
            } catch (IndexOutOfBoundsException e) {
                hamcrest.assertThat(String.format("There is no row in table array by index '%d'", i), false);
                break;
            }
        }
        hamcrest.assertAll();
    }

    protected String checkPreviewElements(WebElement container, Map<String, String> leadDetailsMap, Map<String, String> missedDataMap) {
        log.info("Check data-field elements");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // fields with data
        List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_ELEMENT);
        dataFieldsList.forEach(element -> {
            InputElement inputElement = new InputElement(element);
            String key = element.getAttribute("data-field-name");
            String expectedValue = leadDetailsMap.get(key);
            String actualValue = LeadsTestData.compareDigitsField(key)
                    ? DataHelper.remainDigits(inputElement.getText()) : inputElement.getText().replaceAll("\\s+", " ");
            if (key.equals("XmlBody")) actualValue = actualValue.replaceAll("\t|\n|\r", "").replaceAll("> <", "><");
            Matcher<String> matcher = LeadsTestData.isRoundedFormatField(key)
                    ? CustomMatcher.matchesPattern(expectedValue, DataHelper.getRoundedFloatToPatten(expectedValue)) : equalTo(expectedValue);
            hamcrest.assertThat(String.format("Preview field '%s' with label '%s' value equals to json",
                    key, inputElement.getLabel()), actualValue, matcher);
            missedDataMap.remove(key);
        });
        return hamcrest.toString();
    }
}
