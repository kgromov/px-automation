package tests.objects.subId;

import configuration.dataproviders.SubIdDataProvider;
import org.testng.annotations.Test;
import px.objects.publishers.pages.PublishersPage;
import px.objects.subIds.SubIdTestData;
import px.objects.subIds.SubIdsPreviewTestData;
import px.objects.subIds.pages.EditSubIdPage;
import px.objects.subIds.pages.SubIdsPage;
import tests.LoginTest;

/**
 * Created by kgr on 7/14/2017.
 */
public class EditSubIdTest extends LoginTest {
    // TODO: when full screen will be available positive test could be united into 1

    @Test(dataProvider = "editSubIdWithPositiveData", dataProviderClass = SubIdDataProvider.class)
    public void editSubIdGeneralInfoWithPositiveData(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        subIdsPage.editSubId(previewTestData.getSubId())
                .editGeneralInfo(testData)
                // check edit => check details with updated data
                .checkUpdatedSubIdGeneralInfo(testData);
    }

    @Test(dataProvider = "editSubIdWithPositiveData", dataProviderClass = SubIdDataProvider.class)
    public void editSubIdContactInfoWithPositiveData(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        EditSubIdPage editSubIdPage = subIdsPage.editSubId(previewTestData.getSubId());
        editSubIdPage.editContactInfo(testData);
        // check edit => check details with updated data
        editSubIdPage.checkUpdatedSubIdContactInfo(testData);
    }

    @Test(dataProvider = "editSubIdWithPositiveData", dataProviderClass = SubIdDataProvider.class)
    public void editSubIdAddressInfoWithPositiveData(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        EditSubIdPage editSubIdPage = subIdsPage.editSubId(previewTestData.getSubId());
        editSubIdPage.editAddressInfo(testData);
        // check edit => check details with updated data
        editSubIdPage.checkUpdatedSubIdAddressInfo(testData);
    }

    @Test(dataProvider = "editSubIdWithNegativeData", dataProviderClass = SubIdDataProvider.class)
    public void editSubIdGeneralInfoWithNegativeData(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        // edit subId page
        subIdsPage.editSubId(previewTestData.getSubId())
                .editGeneralInfo(testData);
    }

    @Test(dataProvider = "editSubIdWithNegativeData", dataProviderClass = SubIdDataProvider.class)
    public void editSubIdContactInfoWithNegativeData(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        // edit subId page
        subIdsPage.editSubId(previewTestData.getSubId())
                .editContactInfo(testData);
    }

    @Test(dataProvider = "editSubIdWithNegativeData", dataProviderClass = SubIdDataProvider.class)
    public void editSubIdAddressInfoWithNegativeData(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        // edit subId page
        subIdsPage.editSubId(previewTestData.getSubId())
                .editAddressInfo(testData);
    }
}
