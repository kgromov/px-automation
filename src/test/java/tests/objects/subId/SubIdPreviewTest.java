package tests.objects.subId;

import configuration.dataproviders.SubIdDataProvider;
import org.testng.annotations.Test;
import px.objects.publishers.pages.PublishersPage;
import px.objects.subIds.SubIdTestData;
import px.objects.subIds.SubIdsPreviewTestData;
import px.objects.subIds.pages.SubIdDetailsPage;
import px.objects.subIds.pages.SubIdsPage;
import tests.LoginTest;

/**
 * Created by kgr on 7/24/2017.
 */
public class SubIdPreviewTest extends LoginTest {
 
    @Test(dataProvider = "subIdDetailsData", dataProviderClass = SubIdDataProvider.class)
    public void checkSubIdDetails(SubIdsPreviewTestData previewTestData, SubIdTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(previewTestData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        subIdsPage.editSubId(previewTestData.getSubId());
        // check each field accordance with data from response
        SubIdDetailsPage detailsPage = new SubIdDetailsPage(pxDriver);
        detailsPage.checkDetails(testData);
    }

    @Test(dataProvider = "subIdOverviewData", dataProviderClass = SubIdDataProvider.class)
    public void checkSubIdsTable(SubIdsPreviewTestData testData) {
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        // publisher management
        publishersPage.navigateToObjects();
        // subIds management
        publishersPage.navigateToSubIdsPage(testData.getPublisher());
        SubIdsPage subIdsPage = new SubIdsPage(pxDriver);
        // set to 100 items per page
        subIdsPage.setItemPerPage(100);
        // check all rows
        subIdsPage.checkAllCells(testData);
    }
}