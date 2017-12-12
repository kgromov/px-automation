package tests.objects.credSets;

import configuration.dataproviders.CredentialSettingsDataProvider;
import org.testng.annotations.Test;
import px.objects.credSets.CredSetTestData;
import px.objects.credSets.pages.CredSetsOverviewPage;
import tests.LoginTest;

/**
 * Created by kgr on 10/30/2017.
 */
public class CreateCredSetTest extends LoginTest {

    @Test(dataProvider = "createCredSetWithPositiveData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void createCredSetWithPositiveData(CredSetTestData testData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.createCredSet()
                .createInstance(testData)
                .saveInstance(testData);
        // check presence in overview
        credSetsPage.checkCredSetPresence(testData);
    }

    @Test(dataProvider = "createCredSetWithNegativeData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void createCredSetWithNegativeData(CredSetTestData testData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.createCredSet()
                .createInstance(testData)
                .saveInstance(testData);
    }

    @Test(dataProvider = "cloneCredSetWithPositiveData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void cloneCredSetWithPositiveData(CredSetTestData oldData, CredSetTestData newData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.cloneCredSet(oldData)
                .checkDefaultValues(oldData)
                .createInstance(newData)
                .saveInstance(newData);
        // check presence in overview
        credSetsPage.checkCredSetPresence(newData);
    }

    @Test(dataProvider = "cloneCredSetWithNegativeData", dataProviderClass = CredentialSettingsDataProvider.class)
    public void cloneCredSetWithNegativeData(CredSetTestData oldData, CredSetTestData newData) {
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        credSetsPage.navigateToObjects();
        // create credSet
        credSetsPage.cloneCredSet(oldData)
                .createInstance(newData)
                .saveInstance(newData);
    }
}
