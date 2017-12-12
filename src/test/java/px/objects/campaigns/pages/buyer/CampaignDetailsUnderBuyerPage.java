package px.objects.campaigns.pages.buyer;

import configuration.browser.PXDriver;
import px.objects.campaigns.pages.CampaignDetailsPage;
import px.reports.campaigns.CampaignDetailsTestData;
import px.reports.campaigns.CampaignDetailsUnderBuyerTestData;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.Map;

import static pages.locators.ElementLocators.DATA_FIELD_PARAMETERIZED_ELEMENT;
import static px.objects.campaigns.CampaignsPageLocators.*;
import static px.reports.campaigns.CampaignDetailsUnderBuyerTestData.ADMIN_FIELDS;

/**
 * Created by kgr on 5/24/2017.
 */
public class CampaignDetailsUnderBuyerPage extends CampaignDetailsPage {

    public CampaignDetailsUnderBuyerPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public void checkCampaignDetails(CampaignDetailsTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        log.info("Check absence of admin campaign fields\t" + ADMIN_FIELDS);
        // check that admin fields are empty
        ADMIN_FIELDS.forEach(field -> {
            hamcrest.assertThat(String.format("Field '%s' should be absent on campaign details page", field),
                    !helper.isElementPresent(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, field)));
        });
        hamcrest.assertAll();
        // check campaign fields values
        super.checkCampaignDetails(testData);
    }

    public void checkCampaignFieldsEditable(CampaignDetailsUnderBuyerTestData testData) {
        log.info("Check Buyer campaign details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, Boolean> campaignDetailsMap = testData.getEditableFieldsMap();
        this.editableFieldsMap = new HashMap<>(campaignDetailsMap);
        helper.waitUntilDisplayed(listMenuElement);
        // General
        log.info("Check 'General'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElementsEditable(helper.getElement(GENERAL_CONTAINER), campaignDetailsMap));
        // Commercial
        log.info("Check 'Commercial'");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(COMMERCIAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElementsEditable(helper.getElement(COMMERCIAL_CONTAINER), campaignDetailsMap));
        // Volume and Quality
        log.info("Check 'Volume and Quality'");
        listMenuElement.setByIndex(3);
        helper.waitUntilDisplayed(VOLUME_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElementsEditable(helper.getElement(VOLUME_CONTAINER), campaignDetailsMap));
        // Goals
        if (testData.hasGoals()) {
            log.info("Check 'Goals'");
            listMenuElement.setByIndex(4);
            helper.waitUntilDisplayed(GOALS_CONTAINER);
            // check preview elements with response data
            hamcrest.append(checkPreviewElementsEditable(helper.getElement(GOALS_CONTAINER), campaignDetailsMap));
        }
        hamcrest.assertThat("There are missed data fields\t" + editableFieldsMap.keySet(), editableFieldsMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
    }
}
