package px.objects.buyers.pages;

import configuration.browser.PXDriver;
import org.openqa.selenium.NoSuchElementException;
import pages.groups.Actions;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.buyers.BuyerTestData;
import utils.SoftAssertionHamcrest;

import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.DashboardPageLocators.SAVE_BUTTON;
import static pages.locators.ElementLocators.DELETE_POPUP;
import static px.objects.buyers.BuyersPageLocators.CONTACT_INFO_CONTAINER;
import static px.objects.buyers.BuyersPageLocators.MANAGERS_CONTAINER;

/**
 * Created by kgr on 10/19/2016.
 */
public class EditBuyersPage extends CreateBuyersPage implements Editable, Actions {

    public EditBuyersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public EditBuyersPage editInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof BuyerTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        BuyerTestData testData = (BuyerTestData) pTestData;
        log.info(String.format("Edit buyer '%s'", testData.getPrevName()));
        editInstance(CONTACT_INFO_CONTAINER);
        createInstance(testData);
        if (testData.showPopup()) {
            helper.click(MANAGERS_CONTAINER + SAVE_BUTTON);
            pxDriver.waitForAjaxComplete();
            if (!helper.isElementPresent(DELETE_POPUP, 2)) {
                throw new NoSuchElementException(String.format("No confirmation popup after changing payment terms from '%s' to '%s'",
                        testData.getPreviousPaymentTerms(), testData.getPaymentTerms()));
            }
            confirm(true);
        }
        return this;
    }

    @Override
    public EditBuyersPage checkDefaultValues(InstancesTestData testData) {
        log.info("Check fields with created buyer values");
        helper.waitUntilDisplayed(CONTACT_INFO_CONTAINER);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Name verification", nameInput.getText(), equalTo(testData.getPrevName()));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("Not supported method");
    }
}
