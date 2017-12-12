package px.reports.publisherPerformance;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import px.reports.dto.AbstractFiltersResetData;
import utils.SoftAssertionHamcrest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.publisherPerformance.PublisherPerformanceTestData.*;

/**
 * Created by kgr on 12/21/2016.
 */
public class PublisherPerformanceReportPage extends ReportsPage implements GraphicsPage {

    public PublisherPerformanceReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.PUBLISHER_PERFORMANCE_REPORT_LINK);
        super.checkPage();
    }

    public void checkPublisherManagersFilter(PublisherPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", PublisherPerformanceReportFiltersEnum.PUBLISHER_MANAGER.getValue()));
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_MANAGER_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getPublisherManager().getName());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByPublisherManager(), testData.getAllTableBuyerCategories());
        // filter reset
        resetFilter(filterLocator, testData.getPublisherManager().getName(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkVerticalsFilter(PublisherPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", PublisherPerformanceReportFiltersEnum.VERTICAL.getValue()));
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getVerticalList());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByVertical(), testData.getAllTableBuyerCategories());
        // filter reset
        resetFilter(filterLocator, testData.getVerticalList().toString(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkBuyerCategoriesFilter(PublisherPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", PublisherPerformanceReportFiltersEnum.BUYER_CATEGORY.getValue()));
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getBuyerCategoriesList());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByBuyerCategories(), testData.getTableBuyerCategories());
        // filter reset
        String filterValue = testData.getBuyerCategoriesList().toString();
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkAllFilters(PublisherPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        String filterValue = testData.getBuyerCategoriesList().toString();
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_MANAGER_FILTER), testData.getPublisherManager().getName());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVerticalList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER), testData.getBuyerCategoriesList());
        // check table
        checkCellsDynamicTooltipsData(testData, testData.getItemsByAllFilters(), testData.getTableBuyerCategories());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getItemsCurrentTotalCount());
            checkCellsDynamicTooltipsData(testData, resetData.getItemsByFiltersCombination(),
                    testData.hasBuyerCategories() ? testData.getTableBuyerCategories() : testData.getAllTableBuyerCategories());
        }
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        checkCalendarDateRanges(testData, PublisherPerformanceReportColumnsEnum.PUBLISHER_ID.getValue(),
                PublisherPerformanceReportColumnsEnum.PUBLISHER_NAME.getValue(),
                PublisherPerformanceReportColumnsEnum.PUBLISHER_TYPE.getValue()
        );
    }

}