package px.objects.disposition;

import configuration.browser.PXDriver;
import elements.SuperTypifiedElement;
import elements.date.CalendarElement;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import elements.input.TextAreaElement;
import elements.menu.ListMenuElement;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.OverviewPage;
import pages.groups.Actions;
import pages.groups.Objectable;
import px.objects.InstancesTestData;
import px.reports.leads.LeadsReportTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.EXPIRATION_DATE_PATTERN;
import static configuration.helpers.DataHelper.getDateByFormatSimple;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static pages.locators.ElementLocators.*;
import static px.objects.disposition.DispositionHistoryColumnsEnum.*;
import static px.objects.leads.LeadsPreviewPageLocators.*;
import static px.reports.leads.LeadsReportColumnsEnum.EMAIL;

/**
 * Created by kgr on 5/16/2017.
 */
public class DispositionPage extends OverviewPage implements Objectable, Actions {
    @FindBy(xpath = DELETE_POPUP)
    private CalendarElement calendarElement;

    public DispositionPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public DispositionPage invokeUpdateDispositionPopup(Set<LeadsReportTestData.ResponseObject> leads) {
        List<String> emails = leads.stream().map(LeadsReportTestData.ResponseObject::getEmail).collect(Collectors.toList());
        log.info(String.format("Remove lead with email '%s' from precessed leads", emails));
        // select row by checkbox for leads email
        selectRowByCellValue(tableElement, emails, EMAIL.getValue());
        // invoke disposition dialogue by button
        helper.click(UPDATE_DISPOSITION_BUTTON);
        // wait till popup
        helper.waitUntilDisplayed(DELETE_POPUP);
        return this;
    }

    public DispositionPage invokeUpdateDispositionPopup(LeadsReportTestData.ResponseObject lead) {
        log.info(String.format("Remove lead with email '%s' from precessed leads", lead.getEmail()));
        // find row in table and click on it
        int rowIndex = tableElement.getRowIndexByCellText(lead.getEmail(), getHeaderIndex(tableElement, EMAIL.getValue()));
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL)) + 1;
        WebElement cell = tableElement.getCellAt(rowIndex, columnIndex);
        cell.click();
        setActionByItemLink(cell, UPDATE_DISPOSITION_LINK);
        // wait till popup
        helper.waitUntilDisplayed(DELETE_POPUP);
        return this;
    }

    public DispositionPage checkSelectedLeadsTable(LeadsReportTestData.ResponseObject... leads) {
        log.info("Check leads email/campaign info in update disposition dialogue");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // here is possibly should be table verification -> put to LeadsPreviewPage
        tableElement = new TableElement(helper.getElement(DELETE_POPUP, ITEMS_TABLE));
        int emailColumnIndex = getHeaderIndex(tableElement, "email") + 1;
        int campaignColumnIndex = getHeaderIndex(tableElement, "buyerId") + 1;
        int rowIndex = 1;
        while (rowIndex <= leads.length) {
            LeadsReportTestData.ResponseObject lead = leads[rowIndex - 1];
            hamcrest.assertThat("Buyer email verification", tableElement.getCellTextAt(rowIndex,
                    emailColumnIndex), equalTo(lead.getEmail()));
            // could be more than 1 buyer -> rewrite to select
            List<String> leadCampaigns = lead.getCampaigns().stream().map(campaign ->
                    campaign.getId() + " - " + campaign.getName()).collect(Collectors.toList());
            Collections.sort(leadCampaigns);
            WebElement campaignCell = tableElement.getCellAt(rowIndex, campaignColumnIndex);
            List<String> actualCampaigns = lead.isSoldMore1Buyer()
                    ? new SelectElement(campaignCell).getCollapsedItems()
                    : Collections.singletonList(campaignCell.getText());
            Collections.sort(actualCampaigns);
            hamcrest.assertThat("Buyer campaigns verification", actualCampaigns, equalTo(leadCampaigns));
            // try to set in dropdown
          /*  if (lead.isSoldMore1Buyer()) {
                ObjectIdentityData campaign = ObjectIdentityData.getAnyObjectFromList(lead.getCampaigns());
                FilteredDropDown campaigns = new FilteredDropDown(campaignCell);
//                campaigns.setLabel("Lead buyer instances select");
                campaigns.setByTitle(campaign.getId() + " - " + campaign.getName());
                campaigns.setByTitle(lead.getCampaignsToSet().stream().map(campaign ->
                        campaign.getId() + " - " + campaign.getName()).collect(Collectors.toList()));
            }*/
            rowIndex++;
        }
        hamcrest.assertAll();
        // check table
        helper.click(POPUP_CONFIRM);
        helper.waitForAjaxComplete();
        return this;
    }

    public DispositionPage updateDisposition(DispositionTestData testData) {
        log.info("Update disposition with new data\t" + testData);
        helper.waitUntilDisplayed(DISPOSITION_STATUS_SELECT);
        if (testData.isPositive())
            new SelectElement(helper.getElement(DELETE_POPUP, DISPOSITION_STATUS_SELECT)).setByTitle(testData.getDispositionStatus());
        // new calendar method is required - like mix of setDate/setRange
        new InputElement(helper.getElement(DELETE_POPUP, DISPOSITION_DATE_CALENDAR)).getInput().click();
        calendarElement.setDate(testData.getDispositionDate());
        calendarElement.setTime(testData.getDispositionDate());
        new TextAreaElement(helper.getElement(DELETE_POPUP, DISPOSITION_EXPLANATION_TEXTAREA)).setByText(testData.getDispositionExplanation());
        helper.click(POPUP_CONFIRM);
        if (testData.isPositive()) {
            checkErrorMessage();
            helper.waitUntilToBeInvisible(DELETE_POPUP);
        }
        return this;
    }

    public Objectable checkErrorMessage(DispositionTestData testData) {
        if (!helper.isElementPresent(DELETE_POPUP, 2))
            throw new RuntimeException(String.format("Disposition successfully updated with negative date '%s' and explanation '%s'",
                    testData.getDispositionDate(), testData.getDispositionExplanation()));
        return checkErrorMessage(Arrays.asList(
                new InputElement(helper.getElement(DELETE_POPUP, DISPOSITION_DATE_CALENDAR)),
                new TextAreaElement(helper.getElement(DELETE_POPUP, DISPOSITION_EXPLANATION_TEXTAREA)),
                new SelectElement(helper.getElement(DELETE_POPUP, DISPOSITION_STATUS_SELECT))
        ));
    }

    public DispositionPage checkDispositionHistoryTable(DispositionTestData testData) {
        log.info("Check disposition history");
        helper.waitUntilDisplayed(SETTING_MENU);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        new ListMenuElement(helper.getElement(SETTING_MENU)).setByIndex(5);
        helper.waitUntilDisplayed(DISPOSITION_HISTORY_CONTAINER);
        tableElement = new TableElement(helper.getElement(DISPOSITION_HISTORY_CONTAINER, ITEMS_TABLE));
        hamcrest.assertThat("Disposition status is updated", tableElement.getCellTextAt(1,
                getHeaderIndex(tableElement, STATUS.getValue()) + 1),
                equalTo(testData.getDispositionStatus()));
        hamcrest.assertThat("Disposition explanation is updated", tableElement.getCellTextAt(1,
                getHeaderIndex(tableElement, EXPLANATION.getValue()) + 1),
                equalTo(testData.getDispositionExplanation()));
        // date - no more than few minutes up to now
      /*  DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+13"));*/
        String dateInTable = tableElement.getCellTextAt(1, getHeaderIndex(tableElement,
                DATE.getValue()) + 1).replace("AM", "am").replace("PM", "pm");
        hamcrest.assertThat("Disposition date is updated", dateInTable,
                equalToIgnoringCase(getDateByFormatSimple(EXPIRATION_DATE_PATTERN, testData.getDispositionDate())));
         /* Date updatedDate = DataHelper.getDateByFormatSimple(EXPIRATION_DATE_PATTERN, dateInTable);
        hamcrest.assertThat("There are a few seconds difference between current date and updated date",
                TimeUnit.MINUTES.convert(new Date().getTime() - updatedDate.getTime(), TimeUnit.MILLISECONDS) < 2);*/
        hamcrest.assertAll();
        return this;
    }

    @Override
    public Objectable checkErrorMessage() {
        return new ObjectPage(pxDriver) {
            @Override
            public ObjectPage checkErrorMessage() {
                return super.checkErrorMessage();
            }
        };
    }

    @Override
    public Objectable checkErrorMessage(List<SuperTypifiedElement> elementsWithError) {
        return new ObjectPage(pxDriver) {
            @Override
            public ObjectPage checkErrorMessage() {
                return super.checkErrorMessage(elementsWithError);
            }
        };
    }

    @Override
    public Objectable saveInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}