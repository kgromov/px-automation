package px.reports.disposition;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import dto.ObjectIdentityData;
import elements.menu.ListMenuElement;
import org.json.JSONArray;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import px.reports.Valued;
import utils.SoftAssertionHamcrest;

import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.LoginPageLocators.LOGGED_USER_NAME;
import static px.reports.ReportPageLocators.CALENDAR_PICKER;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.disposition.DispositionBreakdownReportTestData.*;

/**
 * Created by kgr on 6/13/2017.
 */
public class DispositionBreakdownReportPage extends ReportsPage implements GraphicsPage {

    public DispositionBreakdownReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.DISPOSITION_BREAKDOWN_REPORT_LINK);
        try {
            super.checkPage();
        } catch (RuntimeException ignored) {
        }
    }

    public void checkPublisherFilter(DispositionBreakdownReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publisher' filter");
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

    public void checkBuyerFilter(DispositionBreakdownReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer' filter");
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
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

    public void checkVerticalsFilter(DispositionBreakdownReportTestData testData) {
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

    public void resetBuyersFilter(DispositionBreakdownReportTestData testData) {
        String filterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // workaround for overlapped tooltip
        helper.getElement(LOGGED_USER_NAME).click();
        resetFilter(buyerFilterLocator, filterValue,
                buyerInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void resetPublishersFilter(DispositionBreakdownReportTestData testData) {
        String filterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // workaround for overlapped tooltip
        helper.getElement(LOGGED_USER_NAME).click();
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void checkBuyerInstanceFilter(DispositionBreakdownReportTestData testData) {
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

    public void checkPublisherInstanceFilter(DispositionBreakdownReportTestData testData) {
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

    public void checkAllFilters(DispositionBreakdownReportTestData testData) {
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
    public void checkGraphics(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        log.info("Check 'Graphics' filter");
        this.hamcrest = new SoftAssertionHamcrest();
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // column chart
        hamcrest.append(checkColumnChart(testData, dataList, enums));
        //  bubble chart
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // after reset - default graphic
        log.info("Check graphic after reset - 'Column chart' is expected");
        resetGraphics();
        hamcrest.append(checkColumnChart(testData, dataList, enums));
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        // set initial ranges - should be the same as in test data
        this.initialFromPeriod = testData.getFromPeriod();
        this.initialToPeriod = testData.getToPeriod();
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        // exclude custom range - not fully clear how to
        int originalRowsCount = tableElement.getTotalRowsCount();
        List<List<String>> originalCellsText = HTMLHelper.getTableCells(tableElement.getWrappedElement());
        for (int itemIndex = 0; itemIndex <= menuElement.getItemsCount(); itemIndex++) {
            // custom range
            if (itemIndex == menuElement.getItemsCount()) {
                helper.click(CALENDAR_PICKER);
                calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
            } else {
                String item = menuElement.getItemTextByIndex(itemIndex + 1);
                if (!item.contains("month") && !item.contains("days")) {
                    helper.click(CALENDAR_PICKER);
                    menuElement.setByIndex(itemIndex + 1);
                }
                else continue;
            }
            waitPageIsLoaded();
            // check table
            int newRowsCount = tableElement.getTotalRowsCount();
            List<List<String>> newCellsTextList = HTMLHelper.getTableCells(tableElement.getWrappedElement());
            if (isSuitableRange(0) && !skipEmptyData(originalRowsCount, newRowsCount, testData.hasTotalRow()))
                checkCellsData(originalCellsText, newCellsTextList, isTheSameDateRange(), true, getDateRanges());
            originalCellsText = newCellsTextList;
            originalRowsCount = newRowsCount;
        }
    }
}
