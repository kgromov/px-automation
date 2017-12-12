package px.reports.leadReturns;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import elements.dropdown.TableDropDown;
import org.json.JSONArray;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import pages.groups.Searchable;
import px.objects.leadReturn.SingleLeadReturnPage;
import px.objects.leadReturn.SingleLeadReturnsTestData;
import px.reports.ReportsPage;
import utils.SoftAssertionHamcrest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.AdminPageLocators.SINGLE_LEAD_RETURNS_LINK;
import static pages.locators.ElementLocators.*;
import static px.objects.leads.LeadsPreviewPageLocators.ACCEPT_LINK;
import static px.objects.leads.LeadsPreviewPageLocators.DECLINE_LINK;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.ReportPageLocators.SELECT_ALL_COLUMN;
import static px.reports.leadReturns.LeadReturnStatusEnum.PENDING;
import static px.reports.leadReturns.LeadReturnsReportTestData.*;

/**
 * Created by kgr on 9/4/2017.
 */
public class LeadReturnReportPage extends ReportsPage implements Actions, Searchable {

    public LeadReturnReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(SINGLE_LEAD_RETURNS_LINK);
        super.checkPage();
    }

    public void setReportTypeFilter(LeadReturnsReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType());
    }

    public void setPeriodMonthFilter(SingleLeadReturnsTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), PENDING.getValue());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PERIOD_MONTH_FILTER), testData.getPeriodMonth());
    }

    public void checkReportTypeFilter(LeadReturnsReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Report types (review statuses)' filter");
        // check parent filter
        setReportTypeFilter(testData);
        // check cells data
        checkCellsData(testData, testData.getItemsByReportType());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType(),
                testData.getItemsTotalCount(), DEFAULT_REPORT_TYPE.equals(testData.getReportType()));
        hamcrest.assertAll();
    }

    public void checkPeriodMonthFilter(LeadReturnsReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Period months' filter");
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PERIOD_MONTH_FILTER), testData.getPeriodMonthLabel());
        // check cells data
        checkCellsData(testData, testData.getItemsByPeriodMonth());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PERIOD_MONTH_FILTER), testData.getPeriodMonthLabel(),
                testData.getItemsByReportTypeCount(), testData.DEFAULT_PERIOD_MONTH.equals(testData.getPeriodMonth()));
        hamcrest.assertAll();
    }

    // ============================== Lead returns functional ==============================
    public SingleLeadReturnPage invokeAcceptPopup(SingleLeadReturnsTestData testData) {
        log.info("Accept lead return via actions option");
        WebElement cell = findCellByValue(tableElement, testData.getEmail());
        cell.click();
        setActionByItemText("Accept");
        confirm(true);
//        helper.waitUntilDisplayed(DELETE_POPUP);
        return new SingleLeadReturnPage(pxDriver);
    }

    public SingleLeadReturnPage invokeAcceptPopup(List<SingleLeadReturnsTestData> testData) {
        log.info("Accept multiple lead return via button");
        // select all lead returns by email
        selectRowByCellValue(tableElement, testData.stream()
                .map(SingleLeadReturnsTestData::getEmail).collect(Collectors.toList()), "emailAddress");
        // accept by button
        helper.click(ACCEPT_LINK);
        confirm(true);
        return new SingleLeadReturnPage(pxDriver);
    }

    public SingleLeadReturnPage invokeDeclinePopup(SingleLeadReturnsTestData testData) {
        log.info("Invoke decline lead return popup via actions option");
        WebElement cell = findCellByValue(tableElement, testData.getEmail());
        cell.click();
        setActionByItemText("Decline");
        helper.waitUntilDisplayed(DELETE_POPUP);
        return new SingleLeadReturnPage(pxDriver);
    }

    public SingleLeadReturnPage invokeDeclinePopup(List<SingleLeadReturnsTestData> testData) {
        log.info("Invoke decline lead return popup via button");
        // select all lead returns by email
        selectRowByCellValue(tableElement, testData.stream()
                .map(SingleLeadReturnsTestData::getEmail).collect(Collectors.toList()), "emailAddress");
        // decline by button
        helper.click(DECLINE_LINK);
        helper.waitUntilDisplayed(DELETE_POPUP);
        return new SingleLeadReturnPage(pxDriver);
    }

    // full row (lead) verification
    public LeadReturnReportPage checkSingleLeadReturn(SingleLeadReturnsTestData testData, LeadReturnsReportTestData reportData) {
        log.info("Check that lead %s status at leads return report page");
        // set filter by status and check that lead return data
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), reportData.getReportType());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PERIOD_MONTH_FILTER), reportData.getPeriodMonthLabel());
        int rowIndex = tableElement.getRowIndexByCellText(testData.getEmail());
        // check data only for 1 row -> new method in table or HTMLHelper
        List<List<String>> tableData = HTMLHelper.getTableCells(tableElement);
        tableData = tableData.subList(rowIndex - 1, rowIndex);
        JSONArray expectedData = new JSONArray();
        expectedData.put(testData.toJSONWithStatus(reportData.getReportType()));
        checkCellsData(expectedData, tableData, reportData.getFields());
        return this;
    }

    // light version - just presence
    public LeadReturnReportPage checkSingleLeadReturn(SingleLeadReturnsTestData testData) {
        return checkSingleLeadReturn(Collections.singletonList(testData));
    }

    public LeadReturnReportPage checkSingleLeadReturn(List<SingleLeadReturnsTestData> testData) {
        log.info("Check that lead %s status at leads return report page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        SingleLeadReturnsTestData initialData = testData.get(0);
        // set filter by status and check that lead return data
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), initialData.getStatus());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PERIOD_MONTH_FILTER), initialData.getPeriodMonth());
        testData.forEach(item ->
                hamcrest.assertThat(String.format("Check that lead with email '%s' is present in leads return report with status '%s'",
                        item.getEmail(), item.getStatus()), tableElement.isCellPresentByText(item.getEmail())));
        // absence with others statuses
        REPORT_TYPES.stream().filter(status -> !status.equals(initialData.getStatus())).forEach(status -> {
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), status);
            // absence with others statuses
            testData.forEach(item ->
                    hamcrest.assertThat(String.format("Check that lead with email '%s' is absent in leads return report with status '%s'",
                            item.getEmail(), status), !tableElement.isCellPresentByText(item.getEmail())));
        });
        hamcrest.assertAll();
        return this;
    }

    // if not pending
    public LeadReturnReportPage checkNoActions() {
        log.info("Check that there are no ability to Accept/Decline lead return");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        int rows = tableElement.getRowsCount();
        int actionsColumn = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL)) + 1;
        for (int i = 0; i < rows; i++) {
            WebElement cell = tableElement.getCellAt(i + 1, actionsColumn);
            TableDropDown tableDropDown = new TableDropDown(cell);
            hamcrest.assertThat("There are no actions items", tableDropDown.getItems().isEmpty());
        }
        hamcrest.assertAll();
        return this;
    }

    public LeadReturnReportPage checkNoActionButtons() {
        log.info("Check that there Accept/Decline buttons are not present of selected lead return");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        WebElement selectAllCell = helper.getElement(SELECT_ALL_COLUMN);
        helper.getElement(selectAllCell, CHECKBOX_INPUT).click();
        hamcrest.assertThat("'Accept' button is absent for non 'Pending' lead return(s)", !helper.isElementPresent(ACCEPT_LINK));
        hamcrest.assertThat("'Decline' button is absent for non 'Pending' lead return(s)", !helper.isElementPresent(DECLINE_LINK));
        hamcrest.assertAll();
        return this;
    }

}