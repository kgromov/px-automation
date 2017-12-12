package tests.reports.publishers;

import configuration.dataproviders.ReportsDataProvider;
import org.testng.annotations.Test;
import px.reports.publisherConversion.PublisherConversionPage;
import px.reports.publisherConversionDetails.PublisherConversionDetailTestData;
import px.reports.publisherConversionDetails.PublisherConversionDetailsPage;
import tests.LoginTest;

/**
 * Created by kgr on 3/2/2017.
 */
public class PublisherConversionDetailsTest extends LoginTest {

    @Test(dataProvider = "publisherConversionDetailsData", dataProviderClass = ReportsDataProvider.class, invocationCount = 5)
    public void checkConversionDetails(PublisherConversionDetailTestData testData) {
        PublisherConversionPage reportPage = new PublisherConversionPage(pxDriver);
        reportPage.navigateToPage();
        // set proper items per page
        reportPage.setItemPerPage(100);
        reportPage.setCustomRanges(testData);
        // choose lead in leads report and preview to it
        PublisherConversionDetailsPage leadsPreviewPage = new PublisherConversionDetailsPage(pxDriver);
        leadsPreviewPage.navigateToPage(testData.getTransactionID());
        // check lead details with response
        leadsPreviewPage.checkConversionDetails(testData);
    }
}
