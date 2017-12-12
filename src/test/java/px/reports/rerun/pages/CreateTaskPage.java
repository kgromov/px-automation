package px.reports.rerun.pages;

import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import elements.RadioButtonElement;
import elements.SuperTypifiedElement;
import elements.dropdown.FilteredDropDown;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import pages.groups.Objectable;
import px.objects.InstancesTestData;
import px.reports.rerun.RerunTaskTestData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.DashboardPageLocators.ERROR_MESSAGE;
import static px.reports.rerun.RerunPageLocators.*;

/**
 * Created by konstantin on 05.10.2017.
 */
public class CreateTaskPage extends ObjectPage implements Creatable {
    @FindBy(xpath = FILTER_GUID)
    private SelectElement filterCondition;
    // task fields
    @FindBy(xpath = REPEAT_CYCLE)
    private SelectElement repeatCycle;
    @FindBy(xpath = ACTIVE_FROM_DATE)
    private InputElement activeFrom;
    @FindBy(xpath = ACTIVE_TO_FATE)
    private InputElement activeTo;
    @FindBy(xpath = START_TIME)
    private InputElement startTime;
    @FindBy(xpath = AGE_MIN)
    private InputElement ageMin;
    @FindBy(xpath = AGE_MAX)
    private InputElement ageMax;
    @FindBy(xpath = ALLOW_MULTIPLE_RERUN)
    private RadioButtonElement allowMultipleRerun;
    @FindBy(xpath = IGNORE_RERUN_STATUS)
    private RadioButtonElement ignoreRerunStatus;
    @FindBy(xpath = LAST_AGE_MIN)
    private InputElement lastAgeMin;
    @FindBy(xpath = LAST_AGE_MAX)
    private InputElement lastAgeMax;
    // filter fields
    @FindBy(xpath = FILTER_NAME)
    private InputElement filterName;
    @FindBy(xpath = VERTICALS)
    protected FilteredDropDown verticals;
    @FindBy(xpath = LEAD_SOLD_BEFORE)
    private RadioButtonElement soldBefore;
    @FindBy(xpath = CAMPAIGNS)
    protected FilteredDropDown campaigns;
    @FindBy(xpath = DO_PIXEL)
    private RadioButtonElement doPixel;
    @FindBy(xpath = ALLOW_BIDDING)
    private RadioButtonElement allowBiding;
    @FindBy(xpath = LEAD_SOLD_TO_CAMPAIGN)
    private RadioButtonElement soldToCampaigns;

    public CreateTaskPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public CreateTaskPage setExistedFilter(ObjectIdentityData filter) {
        log.info(String.format("Set filter condition '%s' to rerun task", filter));
        helper.waitUntilDisplayed(filterCondition);
        filterCondition.setByTitle(filter.getName(), true);
        waitPageIsLoaded();
        return this;
    }

    public CreateTaskPage saveInstance() {
        helper.click(SCHEDULE_TASK_BUTTON);
        helper.pause(1000);
        waitPageIsLoaded();
        return this;
    }

    public CreateTaskPage processFilters() {
        log.info("Process filtered leads");
        helper.click(PROCESS_FILTERS_BUTTON);
        pxDriver.waitForAjaxComplete();
        waitPageIsLoaded(150);
        return this;
    }

    public EditTaskPage cloneFilter() {
        log.info("Clone filter from existed");
        helper.click(CLONE_FILTER_BUTTON);
        pxDriver.waitForAjaxComplete();
        waitPageIsLoaded();
        return new EditTaskPage(pxDriver);
    }

    public EditTaskPage editFilter() {
        log.info("Edit existed filter");
        helper.click(EDIT_EXISTED_BUTTON);
        pxDriver.waitForAjaxComplete();
        waitPageIsLoaded();
        return new EditTaskPage(pxDriver);
    }

    public CreateTaskPage checkErrors(RerunTaskTestData testData) {
        if (testData.isPositive()) {
            checkErrorMessage();
//            helper.waitUntilToBeInvisible(LEAD_RERUN_TASK_CONTAINER);
        } else {
            // required cause of fields in form and columns in table match
            if (!helper.isElementPresent(ERROR_MESSAGE))
                throw new NoSuchElementException(String.format("No error messages after entering invalid data - %s",
                        testData.getNegativeData()));
            List<SuperTypifiedElement> errorElements = new ArrayList<>(Arrays.asList(ageMin, ageMax));
            if (testData.withNewFilter()) errorElements.add(filterName);
            if (testData.isAllowedMultipleRerun()) {
                errorElements.add(lastAgeMin);
                errorElements.add(lastAgeMax);
            }
            checkErrorMessage(errorElements);
        }
        return this;
    }

    public CreateTaskPage setTaskData(RerunTaskTestData testData) {
        repeatCycle.setByText(testData.getRepeatCycle());
        // could be by calendars/picker either
        activeFrom.setByText(testData.getActiveFrom());
        activeTo.setByText(testData.getActiveTo());
        startTime.setByText(testData.getStartTime());
        ageMin.setByText(testData.getAgeMin());
        ageMax.setByText(testData.getAgeMax());
        allowMultipleRerun.setByText(testData.getAllowMultiple());
        // dependency
        if (testData.isAllowedMultipleRerun()) {
            helper.waitUntilDisplayed(lastAgeMin);
            lastAgeMin.setByText(testData.getLastAgeMin());
            lastAgeMax.setByText(testData.getLastAgeMax());
        } else {
            helper.waitUntilDisplayed(ignoreRerunStatus);
            ignoreRerunStatus.setByText(testData.getIgnoreStatus());
        }
        return this;
    }

    public CreateTaskPage setFilterData(RerunTaskTestData testData) {
        filterName.setByText(testData.getName());
        log.info(String.format("Verticals = %s\nCampaigns = %s", testData.getVerticals(), testData.getCampaigns()));
        verticals.setByText(testData.getVerticals());
        soldBefore.setByText(testData.getSoldBefore());
        // not always there is any campaign by chosen verticals
        if (testData.isAnyCampaign()) {
            helper.pause(2000);
            campaigns.setByText(testData.getCampaigns().stream().map(campaign ->
                    campaign.getId() + " - " + campaign.getName()).collect(Collectors.toList()));
        }
        doPixel.setByText(testData.getDoPixel());
        allowBiding.setByText(testData.getAllowBiding());
        soldToCampaigns.setByText(testData.getSoldToCampaign());
        return this;
    }


    @Override
    public CreateTaskPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof RerunTaskTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        RerunTaskTestData testData = (RerunTaskTestData) pTestData;
        log.info(String.format("Create new rerun task by name '%s'", testData.getName()));
        helper.waitUntilDisplayed(LEAD_RERUN_TASK_CONTAINER);
        // task fields
        setTaskData(testData);
        // filter  fields
        setFilterData(testData);
        return this;
    }

    @Override
    public Objectable checkDefaultValues() {
        return null;
    }
}
