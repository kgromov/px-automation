package px.reports.leads;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.DataHelper;
import dto.ObjectIdentityData;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.RadioButtonElement;
import elements.dropdown.SelectElement;
import elements.dropdown.TableDropDown;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.objects.leadReturn.SingleLeadReturnPage;
import px.objects.leadReturn.SingleLeadReturnsTestData;
import px.objects.leads.LeadsPreviewPage;
import px.reports.LookUpReportPage;
import px.reports.ReportTestData;
import px.reports.SearchByEnum;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.TableDataObject;
import px.reports.export.LeadsExportTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static config.Config.isAdmin;
import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.PXDataHelper.getValueByType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static pages.locators.ElementLocators.*;
import static px.objects.leads.LeadsPreviewPageLocators.PREVIEW_LINK;
import static px.objects.leads.LeadsPreviewPageLocators.RETURN_LEAD_LINK;
import static px.reports.ExportLocators.*;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.leads.LeadsReportTestData.*;
import static utils.CustomMatcher.equalsToEscapeSpace;

/**
 * Created by kgr on 2/28/2017.
 */
public class LeadsReportPage extends LookUpReportPage implements Actions {

    public LeadsReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    // =================================== leads actions ===================================
    public LeadsPreviewPage navigateToPage(LeadsReportTestData.ResponseObject response) {
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement,
                String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, LeadsReportColumnsEnum.TRANSACTION_ID.getValue())));
        int rowIndex = tableElement.getRowIndexByCellText(response.getTransactionId(), columnIndex);
        log.info(String.format("Lead index in table = '%d' (as 1-based should be leadIndex++)", rowIndex));
        WebElement cell = tableElement.getCellAt(rowIndex, tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL)) + 1);
        cell.click();
        setActionByItemLink(cell, PREVIEW_LINK);
        // or at least save actions items
        /*if (helper.isElementAccessible(cell, By.xpath(PREVIEW_LINK)) && response.isSoldLead())
            helper.getElement(cell, PREVIEW_LINK).click();*/
        // fail test if no leads preview
        try {
            helper.waitUntilDisplayed(SETTING_MENU);
        } catch (TimeoutException e) {
            throw new RuntimeException(String.format("Unable to proceed to lead preview of lead by guid = '%s' " +
                    "and transactionID = '%s'", response.getLeadGuid(), response.getTransactionId()), e);
        }
        return new LeadsPreviewPage(pxDriver);
    }

    public SingleLeadReturnPage invokeLeadReturnPopup(LeadsReportTestData.ResponseObject response) {
        log.info(String.format("Invoke lead return form by email = %s", response.getEmail()));
        // set search filter with searchValue
        filterTable(response.getEmail(), SearchByEnum.EMAIL.getValue()); // currently cause of broker lookups
        assertThat(String.format(String.format("Email '%s' was not found through email lookup", response.getEmail()),
                response.getEmail()), !tableElement.isTableEmpty());
        WebElement cell = findCellByValue(tableElement, response.getEmail());
        cell.click();
        setActionByItemLink(cell, RETURN_LEAD_LINK);
        helper.waitUntilDisplayed(DELETE_POPUP);
        return new SingleLeadReturnPage(pxDriver);
    }

    public SingleLeadReturnPage checkReturnItem(LeadsReportTestData.ResponseObject response, boolean shouldPresent) {
        log.info(String.format("Check 'Return lead' item %s in lead action\tLead:\t%s", shouldPresent ? "present" : "absent", response));
        filterTable(response.getEmail(), SearchByEnum.EMAIL.getValue());
        assertThat(String.format(String.format("Email '%s' was not found through email lookup", response.getEmail()),
                response.getEmail()), !tableElement.isTableEmpty());
        TableDropDown dropDown = new TableDropDown(findCellByValue(tableElement, response.getEmail()));
        dropDown.click();
        assertThat(String.format("Item 'Return lead' is %s in Actions [%s]", (shouldPresent ? "present after decline"
                        : "absent after request return"), dropDown.getItems()),
                shouldPresent, equalTo(helper.isElementPresent(RETURN_LEAD_LINK)));
        return new SingleLeadReturnPage(pxDriver);
    }

    public SingleLeadReturnPage checkReturnItem(SingleLeadReturnsTestData testData, boolean shouldPresent) {
        log.info(String.format("Check 'Return lead' item %s in lead action\tLead:\t%s", shouldPresent ? "present" : "absent", testData));
        filterTable(testData.getEmail(), SearchByEnum.EMAIL.getValue()); // currently cause of broker lookups
        assertThat(String.format(String.format("Email '%s' was not found through email lookup", testData.getEmail()),
                testData.getEmail()), !tableElement.isTableEmpty());
        TableDropDown dropDown = new TableDropDown(findCellByValue(tableElement, testData.getEmail()));
        dropDown.click();
        assertThat(String.format("Item 'Return lead' is %s in Actions [%s]", (shouldPresent ? "present after decline"
                        : "absent after request return"), dropDown.getItems()),
                shouldPresent, equalTo(helper.isElementPresent(RETURN_LEAD_LINK)));
        return new SingleLeadReturnPage(pxDriver);
    }

    public SingleLeadReturnPage checkAcceptedReturnItem(SingleLeadReturnsTestData testData) {
        log.info("Check that accepted lead return is absent in buyer leads overview, email = " + testData.getEmail());
        filterTable(testData.getEmail(), SearchByEnum.EMAIL.getValue());
             assertThat(String.format("Buyer lead with email '%s' is present in leads overview after accepted in lead returns",
                testData.getEmail()), tableElement.isTableEmpty());
     /*   int emailColumnIndex = tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, EMAIL.getValue())));
        assertThat(String.format("Buyer lead with email '%s' is absent in leads overview after accepted in lead returns",
                testData.getEmail()), !tableElement.isCellPresentByText(testData.getEmail(), emailColumnIndex));*/
        return new SingleLeadReturnPage(pxDriver);
    }

    // =================================== leads report ===================================
    public void navigateToPage() {
        setMenu(DashboardMenuEnum.LEADS);
        super.checkPage();
    }

    public void checkPublisherFilter(LeadsReportTestData testData) {
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
        checkCellsData(testData, testData.getItemsByPublisherGUID());
        hamcrest.assertAll();
    }

    public void checkBuyerFilter(LeadsReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyers' filter");
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // check that there is only 1 buyer
        if (Config.isBuyer()) {
            checkFilterItems(buyerFilterLocator, Collections.singleton(buyerFilterValue));
        }
        // check parent filter
        setFilter(buyerFilterLocator, buyerFilterValue);
        // check items
        checkFilterItems(buyerInstanceFilterLocator, testData.getBuyerCampaigns().stream()
                .map(campaign -> campaign.getId() + " - " + campaign.getName())
                .collect(Collectors.toList()));
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        hamcrest.assertAll();
    }

    public void checkOffersFilter(LeadsReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Offers' filter");
        String filterValue = testData.getOffer().getId() + " - " + testData.getOffer().getName();
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER);
        // check parent filter
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByOfferID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkVerticalsFilter(LeadsReportTestData testData) {
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

    public void resetBuyersFilter(LeadsReportTestData testData) {
        String filterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        resetFilter(buyerFilterLocator, filterValue,
                buyerInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void resetPublishersFilter(LeadsReportTestData testData) {
        String filterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void checkBuyerInstanceFilter(LeadsReportTestData testData) {
        if (!testData.hasBuyerInstances()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer Instance' filter");
        String filterValue = testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName();
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // filter by instance if exists
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByBuyerInstanceGUID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsByBuyerGUID().length());
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        hamcrest.assertAll();
    }

    public void checkPublisherInstanceFilter(LeadsReportTestData testData) {
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
        resetFilter(filterLocator, filterValue, testData.getItemsByPublisherGUID().length());
        // check table
        checkCellsData(testData, testData.getItemsByPublisherGUID());
        hamcrest.assertAll();
    }

    public void checkAllFilters(LeadsReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // filter values
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerInstanceFilterValue = testData.hasBuyerInstances() ?
                testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName() : null;
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherInstanceFilterValue = testData.hasPublisherInstances() ? testData.getSubID().getId() : null;
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // set all filters in chain
        setFilter(buyerFilterLocator, buyerFilterValue);
        if (testData.hasBuyerInstances())
            setFilter(buyerInstanceFilterLocator, buyerInstanceFilterValue);
        setFilter(publisherFilterLocator, publisherFilterValue);
        if (testData.hasPublisherInstances())
            setFilter(publisherInstanceFilterLocator, publisherInstanceFilterValue);
        // 'offers' filter only for admin
        if (isAdmin()) {
            String offerFilterValue = testData.getOffer().getId() + " - " + testData.getOffer().getName();
            String offerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER);
            setFilter(offerFilterLocator, offerFilterValue);
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVertical());
        }
        // check table
        checkCellsData(testData, testData.getItemsByAllFilters());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getBuyerItemsCount());
            checkCellsData(testData, resetData.getItemsByFiltersCombination());
        }
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        checkCalendarDateRanges(testData,
                LeadsReportColumnsEnum.CAMPAIGN_ID.getValue(),
                LeadsReportColumnsEnum.SLOTS.getValue(),
                LeadsReportColumnsEnum.STATE.getValue(),
                LeadsReportColumnsEnum.DISPOSITION.getValue()
        );
    }

    @Override
    protected void checkCellsData(ReportTestData testData, JSONArray dataList) {
        long start = System.currentTimeMillis();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        List<FieldFormatObject> fields = FieldFormatObject.withoutNonFields(testData.getFields());
        List<List<String>> tableCellsText = tableDataObject.getTableCellsText();
        JSONArray expectedData = tableDataObject.getDataList();
        for (int i = 0; i < expectedData.length(); i++) {
            JSONObject jsonObject = expectedData.getJSONObject(i);
            for (FieldFormatObject field : fields) {
                try {
                    // check cell text
                    String expectedValue = String.valueOf(jsonObject.get(field.getName()));
                    String actualValue = tableCellsText.get(i).get(field.getIndex());
                    // value matcher + specific
                    if (field.getName().equals(LeadsReportColumnsEnum.CAMPAIGN_ID.getValue())) {
                        expectedValue = expectedValue.equals("43") ? "Not sold" : expectedValue;
                        hamcrest.assertThat(String.format("There are no more than '%d' Buyer IDs\tTable row - %d", MAX_BUYER_IDS, i + 2),
                                expectedValue.split("\\s*-\\s*").length, lessThanOrEqualTo(MAX_BUYER_IDS));
                    }
                    hamcrest.assertThat(String.format("Data in '%s' column at row '%d' in table equals to original data in json", field.getName(), i + 2),
//                            actualValue, getMatherByMetricType(field, expectedValue));
                            actualValue, equalsToEscapeSpace(getValueByType(field, expectedValue)));
                    if (field.hasTooltip())
                        hamcrest.append(checkCellsTooltipData(tableElement, field, expectedValue, i));
                    // check cell popup
                    if (field.hasPopup()) hamcrest.append(checkCellsPopup(tableElement, field, actualValue, i));
                } catch (JSONException e) {
                    hamcrest.assertThat(String.format("There is no data in response JSON array by index '%d'\tDetails:\n%s", i, e.getMessage()), false);
//                    break;
                } catch (IndexOutOfBoundsException e2) {
                    hamcrest.assertThat(String.format("There is no row in table array by index '%d'", i), false);
                    break;
                }
            }
            // canReturn
            int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
            TableDropDown actions = new TableDropDown(tableElement.getCellAt(i + 1, columnIndex + 1));
            List<String> items = actions.getItems();
            LeadsReportTestData.ResponseObject lead = LeadsReportTestData.getResponseObject(jsonObject);
            boolean canReturn = lead.isSoldLead() && lead.isCanReturn();
            hamcrest.assertThat(String.format("CanReturn verification - 'Return lead' %s" +
                            "contains in 'Actions' [%s] dropdown.\tTable row - %d", canReturn ? "" : "not ", items, i + 1),
//                    canReturn, equalTo(items.contains(RETURN_LEAD_LINK_ITEM)));
                    canReturn, equalTo(helper.isElementPresent(actions, RETURN_LEAD_LINK)));
        }
        log.info(String.format("Time to check %d rows with tooltips = %d", expectedData.length(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    public void checkExport(LeadsExportTestData testData) {
        log.info("Start 'Export report' feature verification");
        ElementsHelper helper = HelperSingleton.getHelper();
        // click on 'Export' button
        helper.waitUntilDisplayed(EXPORT_BUTTON);
//        helper.click(POPUP_CONFIRM);
        helper.getElement(EXPORT_BUTTON).click();
        // wait till popup
        helper.waitUntilDisplayed(EXPORT_DIALOGUE);
        // expand if required
        if (!helper.isElementAccessible(SEPARATOR_SELECT)) {
            helper.getElement(CREATE_TOGGLE).click();
            helper.waitUntilDisplayed(SEPARATOR_SELECT);
        }
        // select random item separator
        SelectElement separatorSelect = new SelectElement(helper.getElement(SEPARATOR_SELECT));
        separatorSelect.expand();
        List<String> separators = separatorSelect.getItems();
        separatorSelect.expand();
        String separator = DataHelper.getRandomValueFromList(separators);
        log.info(String.format("Select '%s' csv separator", separator));
        separatorSelect.setByText(separator);
        // others lead specific fields
        log.info(String.format("Set 'Leads attributes' radio button with value '%s'", testData.getExportAttributes()));
        RadioButtonElement exportAttributesRadio = new RadioButtonElement(helper.getElement(EXPORT_ATTRIBUTES_RADIO));
        exportAttributesRadio.setByText(testData.getExportAttributes());
        log.info(String.format("Set 'Vertical' select with value '%s'", testData.getVertical()));
        SelectElement verticalSelect = new SelectElement(helper.getElement(VERTICAL_SELECT));
        verticalSelect.setByTitle(testData.getVertical(), true);
        log.info(String.format("Set 'Country' select with value '%s'", testData.getCountry()));
        SelectElement countrySelect = new SelectElement(helper.getElement(COUNTRY_SELECT));
        countrySelect.setByTitle(testData.getCountry(), true);
        // proceed
        helper.click(POPUP_CONFIRM);
        helper.waitUntilToBeInvisible(EXPORT_DIALOGUE);
        // wait till confirmation tooltip
        helper.waitUntilDisplayed(QUEUED_TOOLTIP);
        log.info(String.format("Confirmation tooltip message\n%s",
                helper.getElement(QUEUED_TOOLTIP_MESSAGE).getText()));
        log.info("Finished 'Export report' feature verification");
    }

    @Override
    public void checkSearchFilterWithFilters(ReportTestData pTestData) {
        if (!(pTestData instanceof LeadsReportTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        LeadsReportTestData testData = (LeadsReportTestData) pTestData;
        List<String> filterLocators = new ArrayList<>(Arrays.asList(BUYER_FILTER, PUBLISHER_FILTER));
        List<String> filterValues = new ArrayList<>(Arrays.asList(testData.getBuyer().getName(),
                testData.getPublisher().getId() + " - " + testData.getPublisher().getName()));
        if (testData.hasBuyerInstances()) {
            filterLocators.add(BUYER_INSTANCE_FILTER);
            filterValues.add(testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName());
        }
        if (testData.hasPublisherInstances()) {
            filterLocators.add(PUBLISHER_INSTANCE_FILTER);
            filterValues.add(testData.getSubID().getId());
        }
        if (isAdmin()) {
            filterLocators.addAll(Arrays.asList(OFFER_FILTER, VERTICAL_FILTER));
            filterValues.addAll(Arrays.asList(testData.getOffer().getId() +
                    " - " + testData.getOffer().getName(), testData.getVertical()));
        }
        // check each filter with lookUp filter in random order
        while (!filterLocators.isEmpty()) {
            int index = getRandomInt(filterLocators.size());
            String filterLocator = filterLocators.get(index);
            String filterValue = filterValues.get(index);
            // dependent child filters
            if (filterLocator.equals(BUYER_INSTANCE_FILTER)) {
                setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), testData.getBuyer().getName());
            }
            if (filterLocator.equals(PUBLISHER_INSTANCE_FILTER)) {
                setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER),
                        testData.getPublisher().getId() + " - " + testData.getPublisher().getName());
            }
            checkFilterDoesNotChangeSearchFilter(testData.getFields(), filterLocator, filterValue);
            // remove already verified filters
            filterLocators.remove(filterLocator);
            filterValues.remove(filterValue);
            if (filterLocator.equals(BUYER_FILTER) && testData.hasBuyerInstances()) {
                filterLocators.remove(BUYER_INSTANCE_FILTER);
                filterValues.remove(testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName());
            }
            if (filterLocator.equals(PUBLISHER_FILTER) && testData.hasPublisherInstances()) {
                filterLocators.remove(PUBLISHER_INSTANCE_FILTER);
                filterValues.remove(testData.getSubID().getId());
            }
        }
    }
}