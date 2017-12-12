package tests.objects.publishers;

import configuration.dataproviders.PublisherDataProvider;
import org.testng.annotations.Test;
import px.objects.publishers.PublisherColumnsEnum;
import px.objects.publishers.PublisherTestData;
import px.objects.publishers.pages.CreatePublishersPage;
import px.objects.publishers.pages.PublishersPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/24/2016.
 */
public class CreatePublishersTest extends LoginTest {

    @Test(dataProvider = "positivePublisherData", dataProviderClass = PublisherDataProvider.class)
    public void createPublisherWithPositiveData(PublisherTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        publishersPage.navigateToObjects();
        publishersPage.fillInPage();
        new CreatePublishersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
//        publishersPage.checkCreatedInstance(testData);
//        CheckTestData.checkInstanceCreated(testData.getInstanceGroup(), "publisherName", testData);
        // check created in publishers table -> search by name
        publishersPage.checkCreatedInstanceByName(testData, PublisherColumnsEnum.NAME.getValue());
    }

    @Test(dataProvider = "negativePublisherData", dataProviderClass = PublisherDataProvider.class)
    public void createPublisherWithNegativeData(PublisherTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        publishersPage.navigateToObjects();
        publishersPage.fillInPage();
        new CreatePublishersPage(pxDriver)
                .checkDefaultValues()
                .createInstance(testData)
                .saveInstance(testData);
    }
}
