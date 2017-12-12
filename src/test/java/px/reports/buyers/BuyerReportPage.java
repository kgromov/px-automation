package px.reports.buyers;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.DataHelper;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import utils.SoftAssertionHamcrest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.buyers.BuyerReportFiltersEnum.DEFAULT_BAYER_CATEGORY;
import static px.reports.buyers.BuyerReportTestData.*;

/**
 * Created by kgr on 11/16/2016.
 */
public class BuyerReportPage extends ReportsPage implements GraphicsPage {

    public BuyerReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.BUYER_REPORT_LINK);
        super.checkPage();
    }

    public void checkBuyerCategoryFilter(BuyerReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerReportFiltersEnum.BUYER_CATEGORY.getValue()));
        // check table
        checkCellsData(testData, testData.getItemsByBuyerCategory());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER),
                testData.getBuyerCategory(), testData.getItemsTotalCount(),
                DEFAULT_BAYER_CATEGORY.equals(testData.getBuyerCategory()));
        hamcrest.assertAll();
    }

    public void setBuyerCategoriesFilter(BuyerReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getBuyerCategory());
    }

    public void checkBuyerFilter(BuyerReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerReportFiltersEnum.BUYER.getValue()));
        String filterValue = testData.getBuyer().getName();
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), filterValue,
                testData.getItemsByCategoryCount());
        hamcrest.assertAll();
    }

    public void checkSalesManagerFilter(BuyerReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerReportFiltersEnum.SALES_MANAGER.getValue()));
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, SALES_MANAGERS_FILTER), testData.getSalesManagerName());
        // check table
        checkCellsData(testData, testData.getItemsBySalesManager());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, SALES_MANAGERS_FILTER),
                testData.getSalesManagerName(), testData.getItemsByCategoryCount());
        hamcrest.assertAll();
    }

    public void checkAllFilters(BuyerReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' and '%s' filters", BuyerReportFiltersEnum.BUYER.getValue(), BuyerReportFiltersEnum.SALES_MANAGER.getValue()));
        String filterValue = testData.getBuyer().getName();
        // check parent filter
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), filterValue);
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, SALES_MANAGERS_FILTER), testData.getSalesManagerName());
        // check table
//        checkCellsData(testData.getItemsByBuyerAndSalesManagerGUID(), testData.getHeaders());
        checkCellsData(testData, testData.getItemsByBuyerAndSalesManagerGUID());
        // reset filters
        if (DataHelper.getRandBoolean()) {
            resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), filterValue, testData.getItemsBySalesManager().length());
            // check table
            checkCellsData(testData, testData.getItemsBySalesManager());
            resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, SALES_MANAGERS_FILTER),
                    testData.getSalesManagerName(), testData.getItemsTotalCount());
        } else {
            resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, SALES_MANAGERS_FILTER),
                    testData.getSalesManagerName(), testData.getItemsTotalCount());
            // check table
            checkCellsData(testData, testData.getItemsByBuyerGUID());
            resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER), filterValue,
                    testData.getItemsTotalCount());
        }
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        checkCalendarDateRanges(testData, BuyerReportColumnsEnum.BUYER_CAMPAIGN.getValue(),
                BuyerReportColumnsEnum.VERTICAL.getValue(),
                BuyerReportColumnsEnum.VIEWS.getValue(),
                BuyerReportColumnsEnum.SALES_MANAGERS.getValue()
        );
    }
}