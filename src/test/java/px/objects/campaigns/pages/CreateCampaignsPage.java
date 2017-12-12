package px.objects.campaigns.pages;

import configuration.browser.PXDriver;
import elements.SuperTypifiedElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.campaigns.CampaignTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static px.objects.campaigns.CampaignsPageLocators.*;

/**
 * Created by kgr on 10/19/2016.
 */
public class CreateCampaignsPage extends ObjectPage implements Creatable {
    // general
    @FindBy(xpath = CAMPAIGN_NAME_INPUT)
    InputElement campaignNameInput;
    @FindBy(xpath = DAYS_FOR_DUPLICATE_INPUT)
    InputElement dayDuplicateInput;
    @FindBy(xpath = LEG_NAME_INPUT)
    InputElement legNameInput;
    @FindBy(xpath = HASH_LEG_NAME_INPUT)
    InputElement hashLegNameInput;
    @FindBy(xpath = BUYER_SELECT)
    SelectElement buyerSelect;
    @FindBy(xpath = COUNTRY_SELECT)
    SelectElement countrySelect;
    @FindBy(xpath = CURRENCY_SELECT)
    SelectElement currencySelect;
    @FindBy(xpath = BUYER_CATEGORY_SELECT)
    SelectElement buyerCategorySelect;
    @FindBy(xpath = BUYER_TYPE_SELECT)
    SelectElement buyerTypeSelect;
    @FindBy(xpath = VERTICAL_SELECT)
    SelectElement verticalSelect;
    @FindBy(xpath = DELIVERY_TYPE_SELECT)
    SelectElement deliveryTypeSelect;
    // commercial
    @FindBy(xpath = DIRECT_BID_INPUT)
    InputElement directBIDInput;
    @FindBy(xpath = PAYOUT_QUALITY_INPUT)
    InputElement payoutQualityInput;
    @FindBy(xpath = FLOOR_PAYOUT_INPUT)
    InputElement floorPayoutInput;
    @FindBy(xpath = MAX_LR_INPUT)
    InputElement maxLRInput;
    @FindBy(xpath = LR_PAYOUT_QUALITY_INPUT)
    InputElement lrPayoutInput;
    @FindBy(xpath = BUYER_TIER_SELECT)
    SelectElement buyerTierSelect;
    // volume
    @FindBy(xpath = MONTHLY_CAP_INPUT)
    InputElement monthlyCapInput;
    @FindBy(xpath = DAILY_CAP_INPUT)
    InputElement dailyCapInput;
    @FindBy(xpath = FLOOR_SCORE_INPUT)
    InputElement floorScoreInput;
    @FindBy(xpath = SOURCE_QUALITY_SELECT)
    SelectElement sourceQualitySelect;
    @FindBy(xpath = PUBLISHER_SCORE_SELECT)
    SelectElement publisherScoreSelect;
    // goals
    @FindBy(xpath = BUYER_GOAL_SELECT)
    SelectElement buyerGoalSelect;
    @FindBy(xpath = GOAL_PROFIT_INPUT)
    InputElement goalProfitInput;

    public CreateCampaignsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateCampaignsPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof CampaignTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        CampaignTestData testData = (CampaignTestData) pTestData;
        // contact info
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        if (testData.isCreateMode())
            buyerSelect.setByText(testData.getBuyerName());
        countrySelect.setByText(testData.getCountry());
        currencySelect.setByText(testData.getCurrency());
//        if (testData.isCreateMode())
            campaignNameInput.setByText(testData.getName());
        buyerCategorySelect.setByText(testData.getBuyerCategory());
        buyerTypeSelect.setByText(testData.getBuyerType());
        verticalSelect.setByText(testData.getVertical());
        deliveryTypeSelect.setByText(testData.getDeliveryType());
        dayDuplicateInput.setByText(testData.getDaysForDuplicate());
        legNameInput.setByText(testData.getLegName());
        hashLegNameInput.setByText(testData.getHashLegName());
        // commercial
        listMenuElement.setByText(COMMERCIAL_ITEM);
        buyerTierSelect.setByText(testData.getBuyerTier());
        directBIDInput.setByText(testData.getDirectBID());
        payoutQualityInput.setByText(testData.getPayoutQuality());
        floorPayoutInput.setByText(testData.getFlorPayout());
        maxLRInput.setByText(testData.getMaxLR());
        lrPayoutInput.setByText(testData.getIncLRInPayoutQuality());
        // volume
        listMenuElement.setByText(VOLUME_ITEM);
        monthlyCapInput.setByText(testData.getMonthlyPayoutCap());
        dailyCapInput.setByText(testData.getDailyLeadsCap());
        floorScoreInput.setByText(testData.getFloorScore());
        sourceQualitySelect.setByText(testData.getSourceQualityFloor());
        publisherScoreSelect.setByText(testData.getPublisherScoreCelling());
        // goals
        if (testData.hasGoals()) {
            listMenuElement.setByText(GOALS_ITEM);
            buyerGoalSelect.setByText(testData.getBuyerGoal());
            goalProfitInput.setByText(testData.getGoalProfit());
        }
        return this;
    }

    @Override
    public CreateCampaignsPage checkDefaultValues() {
        log.info("Check fields with default values");
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Day duplicate default value", dayDuplicateInput.getValue(), equalTo("30"));
//        hamcrest.assertThat("Buyer tier default value", buyerTierSelect.getValue(), equalTo("1"));
//        hamcrest.assertThat("Direct BID default value", directBIDInput.getValue(), equalTo("0,00 $"));
        hamcrest.assertThat("Direct BID default value", directBIDInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Payout quality default value", payoutQualityInput.getValue(), equalTo("100 %"));
//        hamcrest.assertThat("Floor payout default value", floorPayoutInput.getValue(), equalTo("0,50 $"));
        hamcrest.assertThat("Floor payout default value", floorPayoutInput.getValue(), containsString("0,50"));
        hamcrest.assertThat("Max LR default value", maxLRInput.getValue(), equalTo("0 %"));
        hamcrest.assertThat("LR payout default value", lrPayoutInput.getValue(), equalTo("100 %"));
//        hamcrest.assertThat("Monthly Payout capacity default value", monthlyCapInput.getValue(), equalTo("0,00 $"));
        hamcrest.assertThat("Monthly Payout capacity default value", monthlyCapInput.getValue(), containsString("0,00"));
        hamcrest.assertThat("Daily leads capacity default value", dailyCapInput.getValue(), equalTo("No Cap"));// equalTo("-1"));
        hamcrest.assertThat("Flor score default value", floorScoreInput.getValue(), equalTo("50"));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public CreateCampaignsPage saveInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof CampaignTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        CampaignTestData testData = (CampaignTestData) pTestData;
        // general info
        saveInstance(VOLUME_CONTAINER);
        waitPageIsLoaded();
        if (testData.isPositive()) {
            checkErrorMessage();
            if (testData.isCreateMode())
                helper.waitUntilToBeInvisible(VOLUME_CONTAINER);
        } else {
            List<SuperTypifiedElement> errorElements = new ArrayList<>(Arrays.asList(
                    campaignNameInput, dayDuplicateInput, legNameInput, hashLegNameInput, directBIDInput, payoutQualityInput,
                    floorPayoutInput, lrPayoutInput, monthlyCapInput, dailyCapInput, floorScoreInput));
            if (testData.hasGoals())
                errorElements.add(goalProfitInput);
            checkErrorMessage(errorElements);
        }
        return this;
    }

}