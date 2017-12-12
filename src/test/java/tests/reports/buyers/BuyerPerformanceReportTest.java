package tests.reports.buyers;

import configuration.dataproviders.ExportReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.buyerPerformance.BuyerPerformanceReportPage;
import px.reports.buyerPerformance.BuyerPerformanceTestData;
import px.reports.buyers.BuyerReportFiltersEnum;
import tests.LoginTest;

import static px.reports.ReportPageLocators.BUYER_CATEGORIES_FILTER;

/**
 * Created by kgr on 11/17/2016.
 */
public class BuyerPerformanceReportTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test(dataProvider = "buyerPerformanceReportCommonDataByBuyerCategories", dataProviderClass = ReportsDataProvider.class, testName = "checkByBuyerCategoriesCommonReport")
    public void checkByBuyerCategoriesCommonReport(BuyerPerformanceTestData testData, String buyerCategory) {
        BuyerPerformanceReportPage reportPage = new BuyerPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
        // common for each buyer category
        reportPage.checkPagination(testData, testData.getBuyerCategoryItemsCount());
        reportPage.checkGraphics(testData, testData.getItemsByBuyerCategory(), BuyerReportFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "buyerPerformanceReportExportData", dataProviderClass = ExportReportsDataProvider.class, testName = "checkExportReport")
    public void checkExportReport(BuyerPerformanceTestData testData, String buyerCategory) {
        BuyerPerformanceReportPage reportPage = new BuyerPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setFilter(BUYER_CATEGORIES_FILTER, buyerCategory);
        // display all columns in table
        reportPage.displayAllTableColumns();
        reportPage.checkExport();
    }

    @Test(dataProvider = "buyerPerformanceReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(BuyerPerformanceTestData testData) {
        BuyerPerformanceReportPage reportPage = new BuyerPerformanceReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
        // display all columns in table
        reportPage.displayAllTableColumns();
        // check by instances
        reportPage.checkPublishersFilter(testData);
        reportPage.checkVerticalsFilter(testData);
        reportPage.checkAllFilters(testData);
        // check buyer categories filter
        reportPage.checkBuyerCategoriesFilter(testData);
    }
}
