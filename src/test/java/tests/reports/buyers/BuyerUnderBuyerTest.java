package tests.reports.buyers;

import configuration.dataproviders.BuyerReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.buyers.BuyerReportFiltersEnum;
import px.reports.buyers.BuyerReportPage;
import px.reports.buyers.BuyerReportTestData;
import px.reports.buyers.BuyerUnderBuyerReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 7/31/2017.
 */
public class BuyerUnderBuyerTest extends LoginTest {
    @Test
    public void checkFilterAccordance() {
        BuyerUnderBuyerReportTestData testData = new BuyerUnderBuyerReportTestData();
        BuyerReportPage reportPage = new BuyerReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.turnOffGraphics();
        reportPage.checkFiltersAccordance(testData);
    }

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
        reportPage.checkGraphics(testData, testData.getItemsByBuyerCategory(), BuyerReportFiltersEnum.filters());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "buyerReportInstancesData", dataProviderClass = BuyerReportsDataProvider.class, testName = "checkInstancesFiltersReport")
    public void checkInstancesFiltersReport(BuyerUnderBuyerReportTestData testData) {
        BuyerReportPage reportPage = new BuyerReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set category
        reportPage.setBuyerCategoriesFilter(testData);
        // all rows
        reportPage.checkBuyerCategoryFilter(testData);
    }
}
