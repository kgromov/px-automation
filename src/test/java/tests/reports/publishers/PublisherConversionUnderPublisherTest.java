package tests.reports.publishers;

import configuration.dataproviders.PublisherReportsDataProvider;
import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.ReportTestData;
import px.reports.publisherConversion.PublisherConversionPage;
import px.reports.publisherConversion.PublisherConversionTestData;
import tests.LoginTest;

import java.util.Arrays;
import java.util.List;

import static px.reports.publisherConversion.PublisherConversionTestData.*;

/**
 * Created by kgr on 11/17/2016.
 */
public class PublisherConversionUnderPublisherTest extends LoginTest {
//    private String url = super.url = "http://beta.px.com/";

    @Test
    public void checkFilterAccordance() {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.checkFiltersAccordance(new ReportTestData() {
            @Override
            public List<String> filters() {
                return Arrays.asList(ADJUSTMENT_FILTER, OFFER_FILTER, BROWSER_FILTER, CONVERSION_STATUS_FILTER);
            }
        });
    }

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

    @Test(dataProvider = "publisherConversionReportInstancesData", dataProviderClass = PublisherReportsDataProvider.class, invocationCount = 5)
    public void checkInstancesFiltersReport(PublisherConversionTestData testData) {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        // set proper data range from test data
        reportPage.setCustomRanges(testData);
        // check instance filters
        reportPage.checkAdjustmentFilter(testData);
        reportPage.checkOffersFilter(testData);
        reportPage.checkBrowsersFilter(testData);
        reportPage.checkConversionStatusesFilter(testData);
        // check combinations and reset
        reportPage.checkAllFilters(testData);
    }
}
