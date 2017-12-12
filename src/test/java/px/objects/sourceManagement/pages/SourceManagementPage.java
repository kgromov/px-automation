package px.objects.sourceManagement.pages;

import configuration.browser.PXDriver;
import dto.TestData;
import elements.RadioButtonElement;
import elements.SectionTitleElement;
import elements.checkbox.CheckboxElement;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Actions;
import pages.groups.Creatable;
import px.objects.InstancesTestData;
import px.objects.sourceManagement.SourceManagementTestData;
import utils.SoftAssertionHamcrest;

import java.util.Collections;
import java.util.function.Predicate;

import static org.hamcrest.core.IsEqual.equalTo;
import static pages.locators.ElementLocators.DELETE_POPUP;
import static px.objects.sourceManagement.SourceManagementPageLocators.*;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementPage extends ObjectPage implements Creatable, Actions {
    @FindBy(xpath = CAMPAIGN_STATUS_SECTION)
    SectionTitleElement campaignStatusSection;
    @FindBy(xpath = CAMPAIGN_STATUS)
    RadioButtonElement campaignStatus;
    @FindBy(xpath = CHERRY_PICK_CHECKBOX)
    CheckboxElement cherryPick;
    @FindBy(xpath = ADD_EXCEPTION_SECTION)
    SectionTitleElement addExceptionSection;
    @FindBy(xpath = SOURCE_ID)
    InputElement sourceId;
    @FindBy(xpath = STATUS)
    RadioButtonElement status;

    public SourceManagementPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public SourceManagementPage blockSource() {
        String prevValue = status.getValue();
        setStatus("Block");
        if (!prevValue.equals("Block")) confirm(true);
        return this;
    }

    public SourceManagementPage prioritizeSource() {
        return setStatus("Prioritize");
    }

    private SourceManagementPage setStatus(String value) {
        status.setByText(value);
        return this;
    }

    public SourceManagementPage cherryPickSource(boolean select) {
        if (campaignStatus.getValue().equals("Off")) {
            campaignStatus.setByText("On");
            confirm(true);
            helper.waitUntilDisplayed(cherryPick);
        }
        if (cherryPick.isChecked()) {
            if (!select) {
                cherryPick.setByText();
                confirm(true);
            }
        } else {
            if (select) {
                cherryPick.setByText();
                confirm(true);
            }
        }
        return this;
    }

    public SourceManagementPage setSource(String sourceIdValue) {
        sourceId.setByText(sourceIdValue);
        return this;
    }

    @Override
    public SourceManagementPage createInstance(TestData pTestData) {
        if (!(pTestData instanceof SourceManagementTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        SourceManagementTestData testData = (SourceManagementTestData) pTestData;
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check if expanded
        if (campaignStatusSection.isSectionCollapsed()) campaignStatusSection.expandSection();
        String prevValue = campaignStatus.getValue();
        campaignStatus.setByText(testData.getCampaignStatus());
        if (!prevValue.equals(testData.getCampaignStatus()) && testData.isTurnOnCherryPick()) confirm(true);
        // cherry pick
        if (testData.isTurnOnCherryPick()) cherryPickSource(testData.isCherryPicked());
        // check if expanded
        if (addExceptionSection.isSectionCollapsed()) addExceptionSection.expandSection();
        sourceId.setByText(testData.getSourceID());
        // status: popup when changed to 'Block'
        prevValue = status.getValue();
        setStatus(testData.getStatus());
        if (!prevValue.equals(testData.getStatus()) && testData.isBlocked()) confirm(true);
        return this;
    }

    @Override
    public SourceManagementPage createInstance(InstancesTestData pTestData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }

    @Override
    public SourceManagementPage checkDefaultValues() {
        log.info("Check default source values");
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Check that default 'campaign status' is 'Off'", campaignStatus.getValue(), equalTo("Off"));
        hamcrest.assertThat("Check that default 'status' is 'Block'", status.getValue(), equalTo("Block"));
        hamcrest.assertAll();
        return this;
    }

    public SourceManagementPage saveInstance(TestData testData) {
        helper.click(PROCEED_BUTTON);
        // wait till popup if no match
        if (helper.isElementPresent(DELETE_POPUP, 5)) {
            confirm(true);
        }
        waitPageIsLoaded();
        if (testData.isPositive()) checkErrorMessage();
        else checkErrorMessage(Collections.singletonList(sourceId));
        return this;
    }

    private void confirmByCondition(Predicate<Boolean> condition, boolean change) {
        if (condition.test(change)) confirm(true);
    }
}
