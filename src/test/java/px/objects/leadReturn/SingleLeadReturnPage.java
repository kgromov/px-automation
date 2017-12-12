package px.objects.leadReturn;

import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import elements.ElementManager;
import elements.dropdown.SelectElement;
import elements.input.TextAreaElement;
import elements.input.TextElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Actions;
import pages.groups.Objectable;
import px.reports.leadReturns.LeadReturnStatusEnum;
import utils.SoftAssertionHamcrest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static config.Config.isAdmin;
import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.ElementLocators.DELETE_POPUP;
import static pages.locators.ElementLocators.POPUP_CONFIRM;
import static px.objects.leads.LeadsPreviewPageLocators.*;
import static px.reports.leadReturns.LeadReturnStatusEnum.PENDING;

/**
 * Created by kgr on 9/4/2017.
 */
public class SingleLeadReturnPage extends ObjectPage implements Actions {
    @FindBy(xpath = DELETE_POPUP + BUYER_ID_TEXT)
    private TextElement buyerCampaign;
    @FindBy(xpath = DELETE_POPUP + EMAIL_INPUT)
    private TextElement email;
    @FindBy(xpath = DELETE_POPUP + RETURN_REASON_SELECT)
    private SelectElement reason;
    @FindBy(xpath = DELETE_POPUP + EXPLANATION_TEXTAREA)
    private TextAreaElement explanation;
    @FindBy(xpath = DELETE_POPUP + DECLINE_REASON_TEXTAREA)
    private TextAreaElement declineExplanation;

    public SingleLeadReturnPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public SingleLeadReturnPage createLeadReturn(SingleLeadReturnsTestData testData) {
        log.info("Create lead return with new data\t" + testData);
        helper.waitUntilDisplayed(RETURN_REASON_SELECT);
//        email.setByText(testData.getEmail());
        reason.setByText(testData.getReason());
        explanation.setByText(testData.getExplanation());
        return this;
    }

    public SingleLeadReturnPage declineLeadReturn(SingleLeadReturnsTestData testData) {
        log.info("Decline lead return with data\t" + testData);
        helper.waitUntilDisplayed(EMAIL_INPUT);
        declineExplanation.setByText(testData.getDeclineExplanation());
        return this;
    }

    public SingleLeadReturnPage checkCampaign(SingleLeadReturnsTestData testData) {
        log.info("Check default values of lead return form");
        helper.waitUntilDisplayed(BUYER_ID_TEXT);
        ObjectIdentityData campaign = testData.getCampaign();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Check lead campaign", buyerCampaign.getValue(),
                equalTo(campaign.getId() + (isAdmin() ? " - " + campaign.getName() : "")));
        hamcrest.assertThat("Check email", email.getValue(), equalTo(testData.getEmail()));
        hamcrest.assertAll();
        return this;
    }

    public SingleLeadReturnPage checkDecliningLead(SingleLeadReturnsTestData testData) {
        log.info("Check default values of decline lead return form");
        helper.waitUntilDisplayed(EMAIL_INPUT);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Check return reason", ElementManager.getElement(reason).getValue(), equalTo(testData.getReason()));
        hamcrest.assertThat("Check explanation", ElementManager.getElement(explanation).getValue(), equalTo(testData.getExplanation()));
        hamcrest.assertThat("Check email", ElementManager.getElement(email).getValue(), equalTo(testData.getEmail()));
        hamcrest.assertAll();
        return this;
    }

    // or just contains in one loop
    public SingleLeadReturnPage checkDecliningLead(List<SingleLeadReturnsTestData> testData) {
        log.info("Check default values of decline lead return form");
        helper.waitUntilDisplayed(EMAIL_INPUT);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<String> reasons = testData.stream().map(SingleLeadReturnsTestData::getReason).collect(Collectors.toList());
        List<String> explanations = testData.stream().map(SingleLeadReturnsTestData::getExplanation).collect(Collectors.toList());
        List<String> emails = testData.stream().map(SingleLeadReturnsTestData::getEmail).collect(Collectors.toList());
        hamcrest.assertThat("Check return reason", ElementManager.getElement(reason).getValue(), equalTo(StringUtils.join(reasons, ", ")));
        hamcrest.assertThat("Check explanation", ElementManager.getElement(explanation).getValue(), equalTo(StringUtils.join(explanations, ", ")));
        hamcrest.assertThat("Check email", ElementManager.getElement(email).getValue(), equalTo(StringUtils.join(emails, ", ")));
        hamcrest.assertAll();
        return this;
    }

    public Objectable saveInstance(SingleLeadReturnsTestData testData, LeadReturnStatusEnum statusEnum) {
        helper.click(POPUP_CONFIRM);
        if (testData.isPositive()) {
            checkErrorMessage();
            helper.waitUntilToBeInvisible(DELETE_POPUP);
        } else {
            boolean isCreated = statusEnum.equals(PENDING);
            helper.pause(2000);
            if (!helper.isElementAccessible(DELETE_POPUP))
                throw new RuntimeException(String.format("Lead return %s successfully with negative data: explanation '%s'",
                        isCreated ? "created" : "declined",
                        isCreated ? testData.getExplanation() : testData.getDeclineExplanation()));
            checkErrorMessage(Collections.singletonList(isCreated ? explanation : declineExplanation));
        }
        return this;
    }
}
