package px.reports.rerun.pages;

import configuration.browser.PXDriver;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.reports.ReportsPage;
import px.reports.rerun.ProcessedLeadTestData;
import px.reports.rerun.RerunedLeadsReportTestData;
import utils.SoftAssertionHamcrest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.remainDigits;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.ElementLocators.*;
import static px.reports.rerun.RerunPageLocators.REMOVE_FROM_BATCH_BUTTON;

/**
 * Created by konstantin on 05.10.2017.
 */
public class RerunedLeadsPage extends ReportsPage implements Actions {

    public RerunedLeadsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public RerunedLeadsPage removeFromBatch(ProcessedLeadTestData.ResponseObject lead) {
        log.info(String.format("Remove lead with email '%s' from precessed leads", lead.getEmail()));
        helper.waitUntilDisplayed(tableElement);
        WebElement cell = findCellByValue(tableElement, lead.getEmail());
        cell.click();
        // check that number of leads to be remove is proper
        helper.waitUntilDisplayed(DELETE_POPUP);
        assertThat("Number of leads to be remove is proper", remainDigits(helper.getElement(POPUP_TEXT).getText()), equalTo("1"));
        confirm(true);
        waitPageIsLoaded(150);
        assertThat("Lead with email '%s' is longer present in processed leads table",
                !tableElement.isCellPresentByText(lead.getEmail(), getHeaderIndex(tableElement, "email")));
        return this;
    }

    public RerunedLeadsPage removeFromBatch(Set<ProcessedLeadTestData.ResponseObject> leads) {
        List<String> emails = leads.stream().map(ProcessedLeadTestData.ResponseObject::getEmail).collect(Collectors.toList());
        log.info(String.format("Remove lead with email '%s' from precessed leads", emails));
        selectRowByCellValue(tableElement, emails, "email");
        // remove by button
        helper.click(REMOVE_FROM_BATCH_BUTTON);
        // check that number of leads to be remove is proper
        helper.waitUntilDisplayed(DELETE_POPUP);
        assertThat("Number of leads to be remove is proper",
                remainDigits(helper.getElement(POPUP_TEXT).getText()), equalTo(String.valueOf(leads.size())));
        confirm(true);
        waitPageIsLoaded(150);
        // check absence
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        emails.forEach(email ->
                hamcrest.assertThat("Lead with email '%s' is longer present in processed leads table",
                        !tableElement.isCellPresentByText(email, getHeaderIndex(tableElement, "email"))));
        hamcrest.assertAll();
        return this;
    }

    public RerunedLeadsPage checkAllCells(RerunedLeadsReportTestData testData) {
        if (getActiveItemsPerPage() > 0) helper.scrollToElement(helper.getElement(ITEM_PER_PAGE));
        checkCellsData(testData, testData.getAllRowsArray());
        return this;
    }
}
