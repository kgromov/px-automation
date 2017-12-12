package tests.objects.customRights;

import configuration.dataproviders.CustomRightsDataProvider;
import org.testng.annotations.Test;
import px.objects.customRights.CustomRightsTestData;
import px.objects.customRights.pages.CustomRightsOverviewPage;
import tests.LoginTest;

/**
 * Created by kgr on 11/8/2017.
 */
public class CreateCustomRightsTest extends LoginTest {

    @Test(dataProvider = "createCustomRightsWithPositiveData", dataProviderClass = CustomRightsDataProvider.class)
    public void createCustomRightWithPositiveData(CustomRightsTestData testData) {
        CustomRightsOverviewPage customRightsPage = new CustomRightsOverviewPage(pxDriver);
        customRightsPage.navigateToObjects();
        // create credSet
        customRightsPage.createCreateCustomRight(testData)
                .createInstance(testData)
                .saveInstance(testData);
        // check presence in overview
        customRightsPage.checkCredSetPresence(testData);
    }

    @Test(dataProvider = "createCustomRightsWithNegativeData", dataProviderClass = CustomRightsDataProvider.class)
    public void createCustomRightWithNegativeData(CustomRightsTestData testData) {
        CustomRightsOverviewPage customRightsPage = new CustomRightsOverviewPage(pxDriver);
        customRightsPage.navigateToObjects();
        // create credSet
        customRightsPage.createCreateCustomRight(testData)
                .createInstance(testData)
                .saveInstance(testData);
    }
}
