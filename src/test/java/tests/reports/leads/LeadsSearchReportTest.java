package tests.reports.leads;

import configuration.dataproviders.SearchDataProvider;
import org.testng.annotations.Test;
import px.reports.dto.SearchData;
import px.reports.leads.LeadsReportPage;
import px.reports.leads.LeadsReportTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class LeadsSearchReportTest extends LoginTest {

    @Test(dataProvider = "leadsReportSearchData", dataProviderClass = SearchDataProvider.class, testName = "checkSearchFilterReport")
    public void checkSearchFilterReport(LeadsReportTestData testData, SearchData searchData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // set maximum items per page
        reportPage.setItemPerPage(100);
        // check search filter
        reportPage.checkSearchFilter(searchData);
    }

    @Test(dataProvider = "leadsReportWithFiltersSearchData", dataProviderClass = SearchDataProvider.class, testName = "checkSearchFilterDependencyWithFiltersReport")
    public void checkSearchFilterDependencyWithFiltersReport(LeadsReportTestData testData, SearchData searchData) {
        LeadsReportPage reportPage = new LeadsReportPage(pxDriver);
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
