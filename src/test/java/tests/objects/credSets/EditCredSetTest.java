package tests.objects.credSets;

import configuration.dataproviders.CredentialSettingsDataProvider;
import org.testng.annotations.Test;
import px.objects.credSets.CredSetTestData;
import px.objects.credSets.pages.CredSetPreviewPage;
import px.objects.credSets.pages.CredSetsOverviewPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/7/2017.
 */
public class EditCredSetTest extends LoginTest {

    @Test(dataProvider = "previewCredSetData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void checkCredSetPreview(CredSetTestData testData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // preview credSet
        credSetsPage.editCredSet(testData);
        // check preview
        new CredSetPreviewPage(pxDriver).checkCredSetDetails(testData);
    }

    @Test(dataProvider = "editCredSetWithPositiveData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void editCredSetWithPositiveData(CredSetTestData testData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.editCredSet(testData)
                .editInstance(testData)
                .saveInstance(testData);
        // like refresh page, time to update object
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.editCredSet(testData);
        new CredSetPreviewPage(pxDriver).checkCredSetDetails(testData.asMap());
    }

    @Test(dataProvider = "editCredSetWithNegativeData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void editCredSetWithNegativeData(CredSetTestData testData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.editCredSet(testData)
                .editInstance(testData)
                .saveInstance(testData);
    }
}
