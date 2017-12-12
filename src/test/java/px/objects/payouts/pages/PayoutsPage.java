package px.objects.payouts.pages;

import configuration.browser.PXDriver;
import elements.SectionTitleElement;
import elements.menu.ListMenuElement;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.OverviewPage;
import pages.groups.Actions;
import pages.locators.ElementLocators;
import px.objects.payouts.PayoutTestData;
import px.objects.publishers.PublishersPageLocators;
import utils.SoftAssertionHamcrest;

import static org.hamcrest.Matchers.equalTo;
import static pages.locators.ElementLocators.*;
import static px.objects.offers.OffersPageLocators.OFFER_PAYOUTS_ITEM;
import static px.objects.payouts.PayoutsPageLocators.*;
import static px.objects.pixels.PixelPageLocators.DELETE_LINK;
import static px.objects.publishers.PublishersPageLocators.OFFER_PAYOUTS_CONTAINER;

/**
 * Created by kgr on 10/19/2016.
 */
public class PayoutsPage extends OverviewPage implements Actions{
    @FindBy(xpath = ElementLocators.SETTING_MENU)
    private ListMenuElement listMenuElement;
    @FindBy(xpath = BASIC_INFO_CONTAINER)
    private SectionTitleElement titleElement;

    public PayoutsPage(PXDriver pxDriver) {
        super(pxDriver);
        instanceGroup = "offerPayouts";
    }

    public void expandPublisherPayoutCreateForm() {
        log.info("Click on toggle to expand create form");
        helper.click(OFFER_PAYOUTS_CONTAINER + CREATE_TOGGLE);
        helper.waitUntilDisplayed(PAYOUT_INPUT);
    }

    public void collapsePublisherPayoutCreateForm() {
        log.info("Click on toggle to collapse create form");
        helper.click(OFFER_PAYOUTS_CONTAINER + CREATE_TOGGLE);
        helper.waitUntilToBeInvisible(PAYOUT_INPUT);
    }

    public void deletePayout(PayoutTestData testData) {
        // click on 'Actions' and select 'Delete' option
        WebElement cell = findCellByValue(tableElement, testData.getTargetID());
        cell.click();
        setActionByItemLink(cell, DELETE_LINK);
        confirm(true);
    }

    // replace with HeaderObjects ?
    public void clickAction(PayoutTestData testData, String key) {
        // workaround cause of missed filter by target ID
        if (testData.getExistedPayoutIDs().size() > 10) setItemPerPage(100);
        WebElement cell = findCellByValue(tableElement, testData.getTargetID());
        cell.click();
        setActionByItemLink(cell,  key.equals("Edit") ? EDIT_LINK : DELETE_LINK);
    }

    public void navigateToOfferPayout() {
        waitMenuItems();
        helper.waitUntilDisplayed(BASIC_INFO_CONTAINER + "//div[@class='px-sub-content']");
        closeWelcomePopup();
        listMenuElement.setByText(OFFER_PAYOUTS_ITEM);
        checkPage();
        pxDriver.waitForAjaxComplete();
//        testData.setCreatedPayoutsCount(tableElement.getFilledRowsCount());
    }

    public void navigateToPublisherPayout() {
        waitMenuItems();
        helper.waitUntilDisplayed(OFFER_PAYOUTS_CONTAINER);
//        closeWelcomePopup();
        listMenuElement.setByText(PublishersPageLocators.OFFER_PAYOUTS_ITEM);
        pxDriver.waitForAjaxComplete();
        checkPage();
        tableElement = new TableElement(helper.getElement(OFFER_PAYOUTS_CONTAINER, ITEMS_TABLE));
    }

    // rewrite this shit to normal verification by data {offer_id, publisher_id}
    public void checkCreatedInstance(PayoutTestData testData) {
        checkPage();
//        String columnName = testData.isOfferPayout() ? "publisherId" : "offerId";
//        int columnIndex = tableElement.getColumnIndexByAttribute("data-field-name", columnName);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat(String.format("Created payout added to %s table", testData.getInstanceGroup()),
                tableElement.getTotalRowsCount(), equalTo(testData.getExistedPayoutIDs().size() + 1));//
        hamcrest.assertThat(String.format("'%s' is present in payouts table", testData.getTargetID()),
                tableElement.isCellPresentByText(testData.getTargetID(), 1));
        hamcrest.assertAll();
    }

    public void checkDeletedInstanceByName(PayoutTestData testData) {
        checkPage();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Deleted payout removed from payouts table",
                tableElement.getTotalRowsCount(), equalTo(testData.getExistedPayoutIDs().size() - 1));
        hamcrest.assertThat(String.format("'%s' is present in %ss filtered table", testData.getTargetID(), testData.getInstanceGroup()),
                !tableElement.isCellPresentByText(testData.getTargetID(), 1));
        hamcrest.assertAll();
    }
}