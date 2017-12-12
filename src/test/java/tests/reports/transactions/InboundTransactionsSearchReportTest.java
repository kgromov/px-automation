package tests.reports.transactions;

import configuration.dataproviders.SearchDataProvider;
import org.testng.annotations.Test;
import px.reports.dto.SearchData;
import px.reports.inbound.InboundTransactionTestData;
import px.reports.inbound.InboundTransactionsReportPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class InboundTransactionsSearchReportTest extends LoginTest {

    @Test(dataProvider = "inboundTransactionsReportSearchData", dataProviderClass = SearchDataProvider.class, testName = "inboundTransactionsReportSearchData")
    public void checkSearchFilterReport(InboundTransactionTestData testData, SearchData searchData) {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set maximum items per page
        reportPage.setItemPerPage(100);
        // check search filter
        reportPage.checkSearchFilter(searchData);
    }

    @Test(dataProvider = "inboundTransactionsWithFiltersSearchData", dataProviderClass = SearchDataProvider.class, testName = "inboundTransactionsWithFiltersSearchData")
    public void checkSearchFilterDependencyWithFiltersReport(InboundTransactionTestData testData, SearchData searchData) {
        InboundTransactionsReportPage reportPage = new InboundTransactionsReportPage(pxDriver);
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