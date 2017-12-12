package px.objects.pixels.pages;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import elements.table.TableElement;
import org.json.JSONException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import pages.ObjectPage;
import pages.groups.Actions;
import pages.groups.Objectable;
import px.objects.InstancesTestData;
import px.objects.pixels.PixelTestData;
import utils.SoftAssertionHamcrest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static pages.locators.ElementLocators.*;
import static px.objects.pixels.PixelPageLocators.*;
import static px.objects.publishers.PublishersPageLocators.OFFER_PIXELS_CONTAINER;

/**
 * Created by kgr on 5/3/2017.
 */
public class PreviewPixelPage extends ObjectPage implements Actions {
    private TableElement tableElement;
    // to save extra fields from UI
    private List<String> extraFieldsList = new ArrayList<>();

    public PreviewPixelPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void testPixel(PixelTestData testData) {
        tableElement = new TableElement(helper.getElement(OFFER_PIXELS_CONTAINER, ITEMS_TABLE));
        // click on 'Actions' and select 'Test' option
        WebElement cell = findCellByValue(tableElement, testData.getId());
        cell.click();
        setActionByItemLink(cell, TEST_LINK);
        pxDriver.moveToSecondTab();
        // close previous tabs if open in new
        pxDriver.closePreviousTab();
        waitPageIsLoaded();
    }

    public void checkPixelDetails(PixelTestData testData) {
        log.info("Check Pixel details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
//        Map<String, String> pixelDetailsMap = testData.getPixelDetailsMap();
        // map to get missed data elements
        Map<String, String> missedDataMap = new HashMap<>();
        listMenuElement.setByIndex(1);
        log.info("Check 'General' section");
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check preview elements with response data
//        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_CONTAINER), pixelDetailsMap, missedDataMap));
        testData.getPixelObject().keySet().forEach(field -> {
            try {
                InputElement inputElement = new InputElement(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, field)));
                String expectedValue = String.valueOf(testData.getPixelObject().get(field));
                hamcrest.assertThat(String.format("%s value verification", inputElement.getLabel()),
                        inputElement.getText(), equalToIgnoringCase(expectedValue));
            } catch (JSONException e) {
                hamcrest.assertThat(String.format("Key '%s' is missed in payout details\t%s", field, testData.getPixelObject()), false);
            } catch (NoSuchElementException e2) {
                missedDataMap.put(field, String.valueOf(testData.getPixelObject().get(field)));
            }
        });
        log.info("Check 'Test' section");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(TEST_CONTAINER);
        WebElement testContainer = helper.getElement(TEST_CONTAINER);
        InputElement offerInput = new InputElement(helper.getElement(testContainer, OFFER_INPUT));
        hamcrest.assertThat(String.format("Offer data with label '%s' value equals to json",
                offerInput.getLabel()), offerInput.getText(),
                equalToIgnoringCase(testData.getOffer().getId() + " - " + testData.getOffer().getName()));
        InputElement publisherInput = new InputElement(helper.getElement(testContainer, PUBLISHER_INPUT));
        hamcrest.assertThat(String.format("Publisher data with label '%s' value equals to json",
                publisherInput.getLabel()), publisherInput.getText(),
                equalToIgnoringCase(testData.getPublisher().getId() + " - " + testData.getPublisher().getName()));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
//        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
    }

    public void checkLinks(PixelTestData testData) {
        log.info("Check pixel details links");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Test Publisher' link");
        WebElement testContainer = helper.getElement(TEST_CONTAINER);
        InputElement publisherLinkInput = new InputElement(helper.getElement(testContainer, PUBLISHER_URL_INPUT));
        publisherLinkInput.setByText(testData.getPublisherLink());
        // test publisher link
        helper.click(OPEN_BUTTON);
        // check that new tab is opened with provided url
        pxDriver.waitForAjaxComplete();
        pxDriver.moveToSecondTab();
        hamcrest.assertThat(String.format("Check Publisher link. Check Conversion link.Check that new tab is opened with provided url '%s'",
                testData.getPublisherLink()), pxDriver.getCurrentUrl().replaceAll("/$", ""), equalToIgnoringCase(testData.getPublisherLink()));
        pxDriver.moveToFirstTabAndCloseSecondTab();
        log.info("Check 'Test Conversion' link");
        InputElement conversionLinkInput = new InputElement(helper.getElement(testContainer, CONVERSION_URL_INPUT));
        if(testData.hasConversionLinkByOfferStatus()) {
            hamcrest.assertThat(String.format("Conversion link  with label '%s' value equals to json",
                    conversionLinkInput.getLabel()), conversionLinkInput.getValue(),
                    equalToIgnoringCase(testData.getConversionLink()));
        } else {
            conversionLinkInput.setByText(testData.getConversionLink());
            helper.waitUntilDisplayed(TEST_BUTTON);
        }
        // test conversion link
        helper.getElement(helper.getElement(TEST_CONTAINER), TEST_BUTTON).click();
        pxDriver.waitForAjaxComplete();
        pxDriver.moveToSecondTab();
        try {
            hamcrest.assertThat(String.format("Check Conversion link. Check that new tab is opened with provided url '%s'", testData.getConversionLink()),
                    URLDecoder.decode(pxDriver.getCurrentUrl(), "UTF-8"), equalToIgnoringCase(testData.getConversionLink()));
        } catch (UnsupportedEncodingException ignored) {
        }
        pxDriver.moveToFirstTabAndCloseSecondTab();
        hamcrest.assertAll();
    }

    // perhaps add extra items that present in UI but not in response
    private String checkPreviewElements(WebElement container, Map<String, String> pixelDetailsMap, Map<String, String> missedDataMap) {
        log.info("Check data-field elements");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // fields with data
        List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_ELEMENT);
        for (WebElement element : dataFieldsList) {
            InputElement inputElement = new InputElement(element);
            String key = element.getAttribute("data-field-name");
            // skip element if there is no such field
            if (!missedDataMap.containsKey(key)) {
                extraFieldsList.add(key);
                continue;
            }
            String expectedValue = pixelDetailsMap.get(key);
            String actualValue = inputElement.getText();//.replaceAll("\\s+", " ");
            hamcrest.assertThat(String.format("Conversion details field '%s' with label '%s' value equals to json",
                    key, inputElement.getLabel()), actualValue, equalToIgnoringCase(expectedValue));
            missedDataMap.remove(key);
        }
        return hamcrest.toString();
    }

    @Override
    public Objectable saveInstance(InstancesTestData testData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}