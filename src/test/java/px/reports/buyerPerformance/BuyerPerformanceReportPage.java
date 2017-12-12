package px.reports.buyerPerformance;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.ReportsPage;
import utils.SoftAssertionHamcrest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.buyerPerformance.BuyerPerformanceTestData.BUYER_CATEGORY_FILTER;
import static px.reports.buyerPerformance.BuyerPerformanceTestData.PUBLISHER_FILTER;
import static px.reports.buyerPerformance.BuyerPerformanceTestData.VERTICAL_FILTER;

/**
 * Created by kgr on 3/30/2017.
 */
public class BuyerPerformanceReportPage extends ReportsPage implements GraphicsPage {

    public BuyerPerformanceReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.BUYER_PERFORMANCE_REPORT_LINK);
        super.checkPage();
    }

    public void setBuyerCategoriesFilter(BuyerPerformanceTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER), testData.getBuyerCategory());        
    }

    public void checkBuyerCategoriesFilter(BuyerPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerPerformanceReportFiltersEnum.BUYER_CATEGORY.getValue()));
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER), testData.getBuyerCategory());
        // check table
        checkCellsData(testData, testData.getItemsByBuyerCategory(), testData.getBuyerCategoriesFields());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER),
                testData.getBuyerCategory(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkPublishersFilter(BuyerPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerPerformanceReportFiltersEnum.PUBLISHER.getValue()));
        ObjectIdentityData publisher = testData.getPublisher();
        String filterValue = publisher.getId() + " - " + publisher.getName();
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByPublisherID());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), 
                filterValue, testData.getBuyerCategoryItemsCount());
        hamcrest.assertAll();
    }

    public void checkVerticalsFilter(BuyerPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerPerformanceReportFiltersEnum.VERTICAL.getValue()));
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVerticalList());
        // check table
        checkCellsData(testData, testData.getItemsByVertical());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER),
                testData.getVerticalList().toString(), testData.getBuyerCategoryItemsCount());
        hamcrest.assertAll();
    }

    // maybe change to dynamic with 3 ones
    public void checkAllFilters(BuyerPerformanceTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        ObjectIdentityData publisher = testData.getPublisher();
        String filterValue = publisher.getId() + " - " + publisher.getName();
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVerticalList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), filterValue);
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
    public void checkCalendarDateRanges(ReportTestData testData) {
        checkCalendarDateRanges(testData, BuyerPerformanceReportColumnsEnum.BUYER_NAME.getValue(),
                BuyerPerformanceReportColumnsEnum.BUYER_CATEGORY.getValue()
        );
    }
}
