package tests.reports.transactions;

import configuration.dataproviders.SearchDataProvider;
import org.testng.annotations.Test;
import px.reports.dto.SearchData;
import px.reports.outbound.OutboundTransactionTestData;
import px.reports.outbound.OutboundTransactionsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class OutboundTransactionsSearchReportTest extends LoginTest {

    @Test(dataProvider = "outboundTransactionsReportSearchData", dataProviderClass = SearchDataProvider.class, testName = "outboundTransactionsReportSearchData")
    public void checkSearchFilterReport(OutboundTransactionTestData testData, SearchData searchData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set maximum items per page
        reportPage.setItemPerPage(100);
        // check search filter
        reportPage.checkSearchFilter(searchData);
    }

    @Test(dataProvider = "outboundTransactionsWithFiltersSearchData", dataProviderClass = SearchDataProvider.class, testName = "outboundTransactionsWithFiltersSearchData")
    public void checkSearchFilterDependencyWithFiltersReport(OutboundTransactionTestData testData, SearchData searchData) {
        OutboundTransactionsReportPage reportPage = new OutboundTransactionsReportPage(pxDriver);
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