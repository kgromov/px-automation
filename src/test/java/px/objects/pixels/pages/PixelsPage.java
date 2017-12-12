package px.objects.pixels.pages;

import configuration.browser.PXDriver;
import elements.dropdown.TableDropDown;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;
import pages.OverviewPage;
import pages.groups.Actions;
import px.objects.InstancesTestData;
import px.objects.offers.OfferColumnsEnum;
import px.objects.pixels.PixelTestData;
import utils.SoftAssertionHamcrest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pages.locators.ElementLocators.*;
import static px.objects.pixels.PixelPageLocators.DELETE_LINK;
import static px.objects.publishers.PublishersPageLocators.OFFER_PIXELS_CONTAINER;

/**
 * Created by kgr on 1/25/2017.
 */
public class PixelsPage extends OverviewPage implements Actions {

    public PixelsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    // possibly extend with description and move to OverviewPage
    public PixelsPage checkPixelPreview(PixelTestData pixelTestData) {
        log.info("Check Pixel code in preview popup");
        int rowIndex = tableElement.getRowIndexByCellText(pixelTestData.getOfferName(), tableElement.getColumnIndexByHeaderText(OfferColumnsEnum.NAME.getValue()));
        tableElement.clickOnCellAt(rowIndex, tableElement.getColumnIndexByHeaderText("Code") + 1);
        helper.waitUntilDisplayed(DELETE_POPUP);
        assertThat("Check Pixel code in preview popup",
                helper.getElement(POPUP_TEXT).getText(),
                equalToIgnoringCase(pixelTestData.getPixelCode()));
        helper.click(POPUP_CLOSE);
        helper.waitUntilToBeInvisible(DELETE_POPUP);
        return this;
    }

    public PixelsPage deletePixel(PixelTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        tableElement = new TableElement(helper.getElement(OFFER_PIXELS_CONTAINER, ITEMS_TABLE));
        // click on 'Actions' and select 'Delete' option
        WebElement cell = findCellByValue(tableElement, testData.getId());
        cell.click();
        setActionByItemLink(cell, DELETE_LINK);
        confirm(true);
        cell = findCellByValue(tableElement, testData.getId(), "offerPixelStatus");
        hamcrest.assertThat("Status of deleted offer pixel with id '%s' verification", cell.getText(), equalTo("deleted"));
        TableDropDown dropDown = new TableDropDown(findCellByValue(tableElement, testData.getId()));
        hamcrest.assertThat("Item 'Delete' is absent after pixel was deleted", dropDown.getItems(), not(hasItem("Delete")));
        hamcrest.assertAll();
        return this;
    }

    public PixelsPage checkDeletedInstanceByName(PixelTestData testData) {
        checkPage();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        tableElement = new TableElement(helper.getElement(OFFER_PIXELS_CONTAINER, ITEMS_TABLE));
        hamcrest.assertThat("Deleted payout removed from payouts table",
                tableElement.getTotalRowsCount(), equalTo(testData.getExistedPixelsCount() - 1));
        hamcrest.assertThat(String.format("'%s' is present in %ss filtered table", testData.getId(), testData.getInstanceGroup()),
                !tableElement.isCellPresentByText(testData.getId(), 1));
        hamcrest.assertAll();
        return this;
    }

    public PixelsPage setTableElement() {
        tableElement = new TableElement(helper.getElement(OFFER_PIXELS_CONTAINER, ITEMS_TABLE));
        return this;
    }

    public PixelsPage setPixelID(InstancesTestData testData) {
        testData.setId(tableElement.getCellTextAt(1, 1));
        return this;
    }
}