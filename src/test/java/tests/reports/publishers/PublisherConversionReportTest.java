package tests.reports.publishers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.publisherConversion.PublisherConversionPage;
import px.reports.publisherConversion.PublisherConversionTestData;
import tests.LoginTest;

/**
 * Created by kgr on 11/17/2016.
 */
public class PublisherConversionReportTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test(dataProvider = "publisherConversionReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkCommonReport(PublisherConversionTestData testData) {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkPagination(testData, testData.getItemsTotalCount());
        reportPage.checkCalendarDateRanges(testData);
    }

    @Test(dataProvider = "publisherConversionReportCommonData", dataProviderClass = ReportsDataProvider.class)
    public void checkExportReport(PublisherConversionTestData testData) {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        reportPage.checkExport();
    }

    @Test(dataProvider = "publisherConversionReportInstancesData", dataProviderClass = ReportsDataProvider.class, priority = 1, invocationCount = 5)
    public void checkInstancesFiltersReport(PublisherConversionTestData testData) {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters
        reportPage.checkPublishersFilter(testData);
        reportPage.checkAdjustmentFilter(testData);
        reportPage.checkOffersFilter(testData);
        reportPage.checkBrowsersFilter(testData);
        reportPage.checkConversionStatusesFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
