package px.reports.dailyMargin;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import dto.ObjectIdentityData;
import dto.TestDataException;
import elements.menu.ListMenuElement;
import org.json.JSONArray;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportsPage;
import px.reports.Valued;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import utils.SoftAssertionHamcrest;
import utils.TestReporter;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static px.reports.ReportPageLocators.CALENDAR_PICKER;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.dailyMargin.DailyMarginReportFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.dailyMargin.DailyMarginReportTestData.*;

/**
 * Created by kgr on 5/24/2017.
 */
public class DailyMarginReportPage extends ReportsPage implements GraphicsPage {

    public DailyMarginReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.DAILY_MARGIN_REPORT_LINK);
        super.checkPage();
    }

    public void checkReportTypeFilter(DailyMarginReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Report type' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER);
        // check parent filter
        setReportTypeFilter(testData);
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByReportType(), testData.getAllTableBuyerCategories());
        // filter reset
        resetFilter(filterLocator, testData.getReportType(),
                testData.getItemsTotalCount(), DEFAULT_REPORT_TYPE.equals(testData.getReportType()));
        hamcrest.assertAll();
    }

    public void setReportTypeFilter(DailyMarginReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType());
    }

    public void checkPublisherFilter(DailyMarginReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publisher' filter");
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // check parent filter
        setFilter(publisherFilterLocator, publisherFilterValue);
        // apply condition
        FieldFormatObject.applyCondition(testData.getFields(), PUBLISHER_FILTER);
        // check items
        checkFilterItems(publisherInstanceFilterLocator, ObjectIdentityData.getAllIDs(testData.getSubIDs()));
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByPublisherGUID(), testData.getAllTableBuyerCategories());
        hamcrest.assertAll();
    }

    public void checkPublisherInstanceFilter(DailyMarginReportTestData testData) {
        if (!testData.hasPublisherInstances()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publisher Instance' filter");
        String filterValue = testData.getSubID().getId();
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // filter by instance if exists
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByPublisherInstanceGUID(), testData.getAllTableBuyerCategories());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsByPublisherGUID().length());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByPublisherGUID(), testData.getAllTableBuyerCategories());
        hamcrest.assertAll();
    }

    public void checkVerticalsFilter(DailyMarginReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Verticals' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getVerticalList());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByVertical(), testData.getAllTableBuyerCategories());
        // filter reset
        resetFilter(filterLocator, testData.getVerticalList().toString(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkBuyerCategoriesFilter(DailyMarginReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer categories' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORIES_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getBuyerCategoriesList());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByBuyerCategories(), testData.getTableBuyerCategories());
        // filter reset
        resetFilter(filterLocator, testData.getItemsByBuyerCategories().toString(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void resetPublishersFilter(DailyMarginReportTestData testData) {
        String filterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsByReportTypeCount());
        // reset fields condition
        FieldFormatObject.resetCondition(testData.getFields());
    }

    public void checkAllFilters(DailyMarginReportTestData testData) {
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
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVerticalList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORIES_FILTER), testData.getBuyerCategoriesList());
        // apply condition
        FieldFormatObject.applyCondition(testData.getFields(), PUBLISHER_FILTER);
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByAllFilters(), testData.getTableBuyerCategories());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(),
                    resetData.getFilterResetKey().equals(REPORT_TYPE_FILTER) && testData.getReportType().equals(DEFAULT_REPORT_TYPE));
            checkCellsDynamicTooltipsData(testData, resetData.getItemsByFiltersCombination(),
                    ((DailyMarginReportTestData.FiltersResetData) resetData).isAllBuyerCategories()
                            ? testData.getAllTableBuyerCategories() : testData.getTableBuyerCategories());
        }
        hamcrest.assertAll();
    }

    /**
     * Method to verify forecast feature
     *
     * @param testData - DailyMarginReportTestData, later on could be extended to ReportTestData
     */
    // add screens for each fail
    public void checkForecast(DailyMarginReportTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        for (int itemIndex = 0; itemIndex <= menuElement.getItemsCount(); itemIndex++) {
            helper.click(CALENDAR_PICKER);
            // custom range
            if (itemIndex == menuElement.getItemsCount()) {
                calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
            } else menuElement.setByIndex(itemIndex + 1);
            waitPageIsLoaded();
            // to set date range from URL
            isTheSameDateRange();
            // check table
            List<List<String>> tableCellsText = HTMLHelper.getTableCells(tableElement.getWrappedElement());
            boolean showForecast = testData.hasForecast(initialFromPeriod, initialToPeriod);
            try {
                assertThat(String.format("Forecast feature verification\tDate range[%s - %s], so forecast row is %s",
                        this.initialFromPeriod, this.initialToPeriod, showForecast ? "present" : "absent"),
                        !tableCellsText.isEmpty() && !tableCellsText.get(0).isEmpty() && tableCellsText.get(0).get(0).equals("Forecast"),
                        equalTo(showForecast));
            } catch (AssertionError e) {
                hamcrest.assertThat(e.getMessage(), false);
                TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Forecast row should be " + (showForecast ? "present " : "absent "));
            }

        }
        if (!hamcrest.toString().isEmpty()) throw new TestDataException(hamcrest.toString());
//        hamcrest.assertAll();
    }

    /*
        1) Publisher Manager, Vertical, Quality tier = {Bubble Chart, Column Chart}
        2) Hour, Day, Week = {Stacked Area, Bubble Chart}
     */
    public void checkGraphics(DailyMarginReportTestData testData, JSONArray dataList, Valued[] enums) {
        log.info("Check 'Graphics' filter");
        this.hamcrest = new SoftAssertionHamcrest();
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // stacked area chart
        if (testData.hasBubbleChart(testData.getReportType()))
            hamcrest.append(checkBubbleChart(testData, dataList, enums));
        else hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        //  column chart
        if (testData.hasBubbleChart(testData.getReportType()))
            hamcrest.append(checkColumnChart(testData, dataList, enums));
        else hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // after reset - default graphic
        log.info(String.format("Check graphic after reset - '%s' is expected",
                testData.hasBubbleChart(testData.getReportType()) ? "None" : "Stacked Area chart"));
        resetGraphics();
        if (testData.hasBubbleChart(testData.getReportType())) hamcrest.append(checkNoneGraphics(enums));
        else hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        hamcrest.assertAll();
    }
}
