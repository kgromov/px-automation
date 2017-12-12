package px.reports.campaigns;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import elements.menu.ListMenuElement;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import pages.groups.GraphicsPage;
import pages.groups.Searchable;
import px.objects.campaigns.pages.CampaignDetailsPage;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import px.reports.dto.AbstractFiltersResetData;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static px.objects.campaigns.CampaignsPageLocators.CAMPAIGN_SETTINGS_LINK;
import static px.reports.ReportPageLocators.CALENDAR_PICKER;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.campaigns.CampaignsReportTestData.BUYER_FILTER;
import static px.reports.campaigns.CampaignsReportTestData.VERTICAL_FILTER;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignsReportPage extends ReportsPage implements GraphicsPage, Actions, Searchable {
    public CampaignsReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.CAMPAIGNS);
        super.checkPage();
    }

    public CampaignDetailsPage navigateToObject(String campaignName) {
        log.info(String.format("Navigate to campaign '%s' settings through campaigns overview table", campaignName));
        filterTable(campaignName, "Campaign Name");
        WebElement cell = findCellByValue(tableElement, campaignName);
        cell.click();
        setActionByItemLink(cell, CAMPAIGN_SETTINGS_LINK);
        return new CampaignDetailsPage(pxDriver);
    }

    public void checkCalendarDateRangesTableRecordsOrder(ReportTestData testData) {
        // set initial ranges - should be the same as in test data
        this.initialFromPeriod = testData.getFromPeriod();
        this.initialToPeriod = testData.getToPeriod();
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        // exclude custom range - not fully clear how to
        int originalRowsCount = tableElement.getTotalRowsCount();
        List<List<String>> originalCellsText = HTMLHelper.getTableCells(tableElement.getWrappedElement());
        for (int itemIndex = 0; itemIndex <= menuElement.getItemsCount(); itemIndex++) {
            helper.click(CALENDAR_PICKER);
            // custom range
            if (itemIndex == menuElement.getItemsCount()) {
                calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
            } else menuElement.setByIndex(itemIndex + 1);
            waitPageIsLoaded();
            // check table
            int newRowsCount = tableElement.getTotalRowsCount();
            List<List<String>> newCellsTextList = HTMLHelper.getTableCells(tableElement.getWrappedElement());
            if (isSuitableRange(7) && !skipEmptyData(originalRowsCount, newRowsCount, testData.hasTotalRow()))
                new CampaignsReportPage(pxDriver).checkCellsOrder(originalCellsText, newCellsTextList, isTheSameDateRange(), getDateRanges());
            originalCellsText = newCellsTextList;
            originalRowsCount = newRowsCount;
        }
    }

    public void checkVerticalsFilter(CampaignsReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Verticals' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getVerticalList());
        // check table
        checkCellsData(testData, testData.getItemsByVertical());
        // filter reset
        resetFilter(filterLocator, testData.getVerticalList().toString(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkBuyersFilter(CampaignsReportTestData testData) {
        if (!Config.isAdmin()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getBuyer().getName());
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        // filter reset
        resetFilter(filterLocator, testData.getBuyer().getName(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkAllFilters(CampaignsReportTestData testData) {
        if (!Config.isAdmin()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVerticalList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), testData.getBuyer().getName());
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

    private void checkCellsOrder(List<List<String>> rowsList, List<List<String>> newRowList, boolean isEqual, String details) {
        int differentRowsOrder = 0;
        int rowsCount = Math.min(rowsList.size(), newRowList.size());
        for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
            differentRowsOrder += rowsList.get(rowIndex).toString().equals(newRowList.get(rowIndex).toString()) ? 0 : 1;
        }
        log.info(String.format("Different cells = '%d'", differentRowsOrder));
        // if there are different rows table would be different
        if ((!rowsList.isEmpty() || !newRowList.isEmpty()) && rowsList.size() != newRowList.size()) {
            List<List<String>> remainList = rowsList.size() > newRowList.size()
                    ? rowsList.subList(rowsCount, rowsList.size()) : newRowList.subList(rowsCount, newRowList.size());
            try {
                differentRowsOrder += remainList.size() * remainList.get(0).size();
            } catch (IndexOutOfBoundsException e) {
                log.info(String.format("HOW IT COULD BE?\trowsList = '%s',\tnewRowList = '%s',\tremainList = '%s'",
                        rowsList, newRowList, remainList));
            }
            log.info(String.format("Different cells by diff rows = '%d'", differentRowsOrder));
        }
        if (isEqual)
            assertThat(String.format("Table cells are sorted in the same order\nDetails:\t%s", details), differentRowsOrder, equalTo(0));
        else
            assertThat(String.format("Table cells are not in the same order\nDetails:\t%s", details), differentRowsOrder, greaterThanOrEqualTo(1));
    }

    public void checkAllCells(CampaignsReportTestData testData) {
        checkCellsData(testData, testData.getAllRowsArray());
    }
}
