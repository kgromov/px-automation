package px.objects.leads.buyer;

import configuration.browser.PXDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import px.objects.leads.LeadsPreviewPage;
import px.objects.leads.LeadsTestData;
import px.reports.leads.LeadsReportTestData;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.ElementLocators.ACTIONS_CELL;
import static pages.locators.ElementLocators.DATA_FIELD_PARAMETERIZED_ELEMENT;
import static px.objects.leads.LeadsPreviewPageLocators.*;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsPreviewUnderBuyerPage extends LeadsPreviewPage {

    public LeadsPreviewUnderBuyerPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public LeadsPreviewUnderBuyerPage navigateToPage(LeadsReportTestData.ResponseObject response) {
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, "leadGuid")));
        int rowIndex = tableElement.getRowIndexByCellText(response.getLeadGuid(), columnIndex);
        log.info(String.format("Lead index in table = '%d' (as 1-based should be leadIndex++)", rowIndex));
        WebElement cell = tableElement.getCellAt(rowIndex, tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL)) + 1);
        cell.click();
        if (helper.isElementAccessible(cell, By.xpath(PREVIEW_LINK)) && response.isSoldLead())
            helper.getElement(cell, PREVIEW_LINK).click();
        // fail test if no leads preview
        try {
            helper.waitUntilDisplayed(listMenuElement);
        } catch (TimeoutException e) {
            throw new RuntimeException(String.format("Unable to proceed to lead preview of lead by guid = '%s' ", response.getLeadGuid()));
        }
        return this;
    }

    @Override
    public void checkLeadDetails(LeadsTestData testData) {
        log.info("Check lead details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, String> leadDetailsMap = testData.getLeadDetailsMap();
        // map to get missed data elements
        Map<String, String> missedDataMap = new HashMap<>(leadDetailsMap);
        helper.waitUntilDisplayed(listMenuElement);
        // check admin menu items absence
        List<String> items = listMenuElement.items().stream().map(WebElement::getText).collect(Collectors.toList());
        LeadsUnderBuyerTestData.ADMIN_MENU_ITEMS.forEach(item -> {
            hamcrest.assertThat(String.format("Admin menu item '%s' is present under buyer user", item),
                    items.contains(item), equalTo(false));
        });
        // Lead Overview
        log.info("Check 'Lead Overview'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(LEAD_OVERVIEW_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_OVERVIEW_CONTAINER), leadDetailsMap, missedDataMap));
        // Contact Information
        log.info("Check 'Contact Information'");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(LEAD_CONTACT_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_CONTACT_CONTAINER), leadDetailsMap, missedDataMap));
        // Lead Details
        log.info("Check 'Lead Details'");
        listMenuElement.setByIndex(3);
        helper.waitUntilDisplayed(LEAD_DETAILS_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_DETAILS_CONTAINER), leadDetailsMap, missedDataMap));
        // Consent Language
        log.info("Check 'Consent Language'");
        listMenuElement.setByIndex(4);
        helper.waitUntilDisplayed(LEAD_TCPA_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_TCPA_CONTAINER), leadDetailsMap, missedDataMap));
        // Lead Attributes
        log.info("Check 'Lead Attributes'");
        listMenuElement.setByIndex(5);
        helper.waitUntilDisplayed(LEAD_ATTRIBUTES_CONTAINER);
        // click to show all lead attributes
        if (helper.isElementAccessible(SHOW_ALL_BUTTON)) helper.getElement(SHOW_ALL_BUTTON).click();
        hamcrest.append(checkPreviewElements(helper.getElement(LEAD_ATTRIBUTES_CONTAINER), leadDetailsMap, missedDataMap));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.size() == 0);
        hamcrest.assertAll();
    }
}
