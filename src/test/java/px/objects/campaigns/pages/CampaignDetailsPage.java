package px.objects.campaigns.pages;

import configuration.browser.PXDriver;
import pages.DetailsPage;
import px.reports.campaigns.CampaignDetailsTestData;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.Map;

import static px.objects.campaigns.CampaignsPageLocators.*;

/**
 * Created by kgr on 5/24/2017.
 */
public class CampaignDetailsPage extends DetailsPage {

    public CampaignDetailsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void checkCampaignDetails(CampaignDetailsTestData testData) {
        log.info("Check Buyer campaign details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, String> campaignDetailsMap = testData.getCampaignDetailsMap();
        this.missedDataMap = new HashMap<>(campaignDetailsMap);
        helper.waitUntilDisplayed(listMenuElement);
        // General
        log.info("Check 'General'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_CONTAINER), campaignDetailsMap, testData.getFields()));
        // Commercial
        log.info("Check 'Commercial'");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(COMMERCIAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(COMMERCIAL_CONTAINER), campaignDetailsMap, testData.getFields()));
        // Volume and Quality
        log.info("Check 'Volume and Quality'");
        listMenuElement.setByIndex(3);
        helper.waitUntilDisplayed(VOLUME_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(VOLUME_CONTAINER), campaignDetailsMap, testData.getFields()));
        // Goals
        if (testData.hasGoals()) {
            log.info("Check 'Goals'");
            listMenuElement.setByIndex(4);
            helper.waitUntilDisplayed(GOALS_CONTAINER);
            // check preview elements with response data
            hamcrest.append(checkPreviewElements(helper.getElement(GOALS_CONTAINER), campaignDetailsMap, testData.getFields()));
        }
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
    }
}
