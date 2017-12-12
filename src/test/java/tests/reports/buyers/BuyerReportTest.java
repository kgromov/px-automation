package tests.reports.buyers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.buyers.BuyerReportFiltersEnum;
import px.reports.buyers.BuyerReportPage;
import px.reports.buyers.BuyerReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class BuyerReportTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test(dataProvider = "buyerReportCommonData", dataProviderClass = ReportsDataProvider.class, testName = "buyerReportCommonData")
    public void checkCommonReport(BuyerReportTestData testData, String reportType) {
        BuyerReportPage reportPage = new BuyerReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
        // common verifications for each buyer category
        reportPage.checkPagination(testData, testData.getItemsByCategoryCount());
        reportPage.checkGraphics(testData, testData.getItemsByBuyerCategory(), BuyerReportFiltersEnum.values());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "buyerReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(BuyerReportTestData testData) {
        BuyerReportPage reportPage = new BuyerReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
        // check by instances
        reportPage.checkBuyerFilter(testData);
        reportPage.checkSalesManagerFilter(testData);
        reportPage.checkAllFilters(testData);
        // all rows
        reportPage.checkBuyerCategoryFilter(testData);
    }
}
