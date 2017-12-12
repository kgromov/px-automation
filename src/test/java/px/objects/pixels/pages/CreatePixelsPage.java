package px.objects.pixels.pages;

import configuration.browser.PXDriver;
import elements.dropdown.SelectElement;
import elements.input.InputElement;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;
import pages.ObjectPage;
import pages.groups.Creatable;
import pages.groups.Objectable;
import px.objects.InstancesTestData;
import px.objects.brokers.BrokersPageLocators;
import px.objects.offers.OfferColumnsEnum;
import px.objects.pixels.PixelTestData;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static pages.locators.DashboardPageLocators.ERROR_MESSAGE;
import static pages.locators.ElementLocators.*;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by kgr on 1/25/2017.
 */
public class CreatePixelsPage extends ObjectPage implements Creatable {
    protected TableElement tableElement;

    public CreatePixelsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreatePixelsPage createInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof PixelTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PixelTestData testData = (PixelTestData) pTestData;
        log.info(String.format("Create offer pixel, offer id ='%s'", testData.getOfferID()));
        helper.click(OFFER_PIXELS_CONTAINER + CREATE_TOGGLE);
        helper.waitUntilDisplayed(CODE_INPUT);
        new SelectElement(helper.getElement(OFFER_PIXELS_CONTAINER, OFFER_ID_SELECT))
                .setByText(testData.getOfferID() + " - " + testData.getOfferName());
        new SelectElement(helper.getElement(OFFER_PIXELS_CONTAINER, TYPE_SELECT)).setByText(testData.getPixelType());
        new InputElement(helper.getElement(CODE_INPUT)).setByText(testData.getPixelCode());
        tableElement = new TableElement(helper.getElement(OFFER_PIXELS_CONTAINER, ITEMS_TABLE));
        return this;
    }

    // possibly extend with description and move to OverviewPage
    public void checkPixelPreview(PixelTestData pixelTestData) {
        log.info("Check Pixel code in preview popup");
        tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        int rowIndex = tableElement.getRowIndexByCellText(pixelTestData.getOfferName(), tableElement.getColumnIndexByHeaderText(OfferColumnsEnum.NAME.getValue()));
        tableElement.clickOnCellAt(rowIndex, tableElement.getColumnIndexByHeaderText("Code") + 1);
        helper.waitUntilDisplayed(DELETE_POPUP);
        assertThat("Check Pixel code in preview popup",
                helper.getElement(POPUP_TEXT).getText(),
                equalToIgnoringCase(pixelTestData.getPixelCode()));
        helper.click(POPUP_CLOSE);
        helper.waitUntilToBeInvisible(DELETE_POPUP);
    }

    @Override
    public CreatePixelsPage saveInstance(InstancesTestData testData) {
        helper.click(OFFER_PIXELS_CONTAINER + CREATE_BUTTON);
        waitPageIsLoaded();
        checkErrors(testData);
        return this;
    }

    protected void checkErrors(InstancesTestData testData) {
        if (testData.isPositive()) super.checkErrorMessage();
        else checkErrorMessage(testData);
    }


    public CreatePixelsPage checkErrorMessage(InstancesTestData pTestData) {
        if (!(pTestData instanceof PixelTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        PixelTestData testData = (PixelTestData) pTestData;
        WebElement parent = helper.getElement(pTestData.isCreateMode() ? OFFER_PIXELS_CONTAINER : BrokersPageLocators.GENERAL_CONTAINER);
        assertThat("2 error messages are required after fill in with negative data: " + testData,
                helper.getElements(parent, ERROR_MESSAGE).size() == 2);
       /*
       List<String> missedErrors = new ArrayList<>();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<WebElement> errors = helper.getElements(ERROR_MESSAGE);
        for (WebElement error : errors) {
            if (expectedErrorsDescription.contains(error.getText())) {
                expectedErrorsDescription.remove(error.getText());
            } else missedErrors.add(error.getText());
        }
        hamcrest.assertThat("There are missed error(s)\n" + expectedErrorsDescription, expectedErrorsDescription.size() == 0);
        hamcrest.assertThat("There are extra error(s)\n" + missedErrors, missedErrors.size() == 0);
        hamcrest.assertAll();*/
        return this;
    }


    @Override
    public Objectable checkDefaultValues() {
        throw new UnsupportedOperationException("Unnecessary method");
    }
}