package tests.reports.pingPost;

import configuration.dataproviders.SearchDataProvider;
import org.testng.annotations.Test;
import px.reports.dto.SearchData;
import px.reports.pingPostTransactions.PinPostTransactionsReportPage;
import px.reports.pingPostTransactions.PingPostTransactionsTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class PingPostTransactionsSearchReportTest extends LoginTest {
    private String url = super.url = "http://stage03-ui.stagingpx.com/"; // "http://rvmd-11606-ui.stagingpx.com/";

    @Test(dataProvider = "pingPostTransactionsReportSearchData", dataProviderClass = SearchDataProvider.class, testName = "pingPostTransactionsReportSearchData")
    public void checkSearchFilterReport(PingPostTransactionsTestData testData, SearchData searchData) {
        PinPostTransactionsReportPage reportPage = new PinPostTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set maximum items per page
        reportPage.setItemPerPage(100);
        // check search filter
        reportPage.checkSearchFilter(searchData);
    }

    @Test(dataProvider = "pingPostTransactionsWithFiltersSearchData", dataProviderClass = SearchDataProvider.class, testName = "pingPostTransactionsWithFiltersSearchData")
    public void checkSearchFilterDependencyWithFiltersReport(PingPostTransactionsTestData testData, SearchData searchData) {
        PinPostTransactionsReportPage reportPage = new PinPostTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set maximum items per page
        reportPage.setItemPerPage(100);
        // set search filter with searchValue
        reportPage.filterTable(searchData.getSearchValue(), searchData.getSearchByEnum().getValue());
        // check that filters don't change search filter
        reportPage.checkSearchFilterWithFilters(testData);
    }
}