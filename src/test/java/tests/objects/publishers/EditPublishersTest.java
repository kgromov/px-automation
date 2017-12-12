package tests.objects.publishers;

import configuration.dataproviders.PublisherDataProvider;
import dto.CheckTestData;
import org.testng.annotations.Test;
import px.objects.publishers.PublisherTestData;
import px.objects.publishers.pages.EditPublishersPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/9/2016.
 */
public class EditPublishersTest extends LoginTest {
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;

    @Test(dataProvider = "editPublisherData", dataProviderClass = PublisherDataProvider.class)
    public void editPublisherGeneralInfoWithPositiveData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editInstance(oldData, newData);
        // check edited fields
        CheckTestData.checkEditedPublisher(newData);
    }

    @Test(dataProvider = "editPublisherData", dataProviderClass = PublisherDataProvider.class, enabled = false)
    public void editPublisherTrackingDataWithPositiveData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editTrackingData(newData);
    }

    @Test(dataProvider = "editPublisherData", dataProviderClass = PublisherDataProvider.class, enabled = false)
    public void editPublisherContactInfoWithPositiveData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editContactInfo(newData);
    }

    @Test(dataProvider = "editPublisherData", dataProviderClass = PublisherDataProvider.class, enabled = false)
    public void editPublisherAddressInfoWithPositiveData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editAddressInfo(newData);
    }

    @Test(dataProvider = "editNegativePublisherData", dataProviderClass = PublisherDataProvider.class)
    public void editPublisherGeneralInfoWithNegativeData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editGeneralInfo(newData);
    }

    @Test(dataProvider = "editNegativePublisherData", dataProviderClass = PublisherDataProvider.class)
    public void editPublisherContactInfoWithNegativeData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editContactInfo(newData);
    }

    @Test(dataProvider = "editNegativePublisherData", dataProviderClass = PublisherDataProvider.class)
    public void editPublisherAddressInfoWithNegativeData(PublisherTestData oldData, PublisherTestData newData) {
        pxDriver.goToURL(url + "admin/publishers/edit/" + oldData.getGuid());
        login();
        new EditPublishersPage(pxDriver)
                .checkDefaultValues(oldData)
                .editAddressInfo(newData);
    }

}