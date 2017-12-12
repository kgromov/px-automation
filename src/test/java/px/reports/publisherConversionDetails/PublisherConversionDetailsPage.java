package px.reports.publisherConversionDetails;

import configuration.browser.PXDriver;
import elements.table.TableElement;
import pages.DetailsPage;
import px.reports.publisherConversion.PublisherConversionReportColumnsEnum;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.Map;

import static pages.locators.ElementLocators.ITEMS_TABLE;
import static px.reports.publisherConversionDetails.PublisherConversionDetailsPageLocators.*;

/**
 * Created by kgr on 3/2/2017.
 */
public class PublisherConversionDetailsPage extends DetailsPage {

    public PublisherConversionDetailsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage(String transactionID) {
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        int rowIndex = tableElement.getRowIndexByCellText(transactionID, PublisherConversionReportColumnsEnum.TRANSACTION_ID.ordinal());
        log.info(String.format("Conversion index in table = '%d' (as 1-based should be conversionIndex++)", rowIndex));
        tableElement.clickOnCellAt(rowIndex, PublisherConversionReportColumnsEnum.ADJUSTMENT.ordinal() + 2);
        waitPageIsLoaded();
    }

    public void checkConversionDetails(PublisherConversionDetailTestData testData) {
        log.info("Check Publisher conversion details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, String> conversionDetailsMap = testData.getConversionDetailsMap();
        // map to get missed data elements
        this.missedDataMap = new HashMap<>(conversionDetailsMap);
        helper.waitUntilDisplayed(listMenuElement);
//        hamcrest.assertThat("Number of fields with data to verify with json is the same", dataFieldsList.size(), equalTo(leadDetailsMap.size()));
        // Lead Overview
        log.info("Check 'General'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_CONTAINER), conversionDetailsMap, testData.getFields()));
        // Contact Information
        log.info("Check 'Affiliate'");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(AFFILIATE_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(AFFILIATE_CONTAINER), conversionDetailsMap, testData.getFields()));
        // Lead Details
        log.info("Check 'Payout'");
        listMenuElement.setByIndex(3);
        helper.waitUntilDisplayed(PAYOUT_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(PAYOUT_CONTAINER), conversionDetailsMap, testData.getFields()));
        // Buyer Details
        log.info("Check 'Tracking'");
        listMenuElement.setByIndex(4);
        helper.waitUntilDisplayed(TRACKING_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(TRACKING_CONTAINER), conversionDetailsMap, testData.getFields()));
        // Consent Language
        log.info("Check 'Notes'");
        listMenuElement.setByIndex(5);
        helper.waitUntilDisplayed(NOTES_CONTAINER);
        hamcrest.append(checkPreviewElements(helper.getElement(NOTES_CONTAINER), conversionDetailsMap, testData.getFields()));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
    }
}