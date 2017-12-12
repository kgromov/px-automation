package px.objects.credSets.pages;

import configuration.browser.PXDriver;
import pages.DetailsPage;
import px.objects.credSets.CredSetTestData;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.Map;

import static px.objects.credSets.CredSetLocators.GENERAL_CONTAINER;

/**
 * Created by kgr on 11/7/2017.
 */
public class CredSetPreviewPage extends DetailsPage {

    public CredSetPreviewPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public CredSetPreviewPage checkCredSetDetails(CredSetTestData testData) {
        log.info("Check CredSet details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, String> valuesMap = testData.getDetails();
        this.missedDataMap = new HashMap<>(valuesMap);
        Map<String, Boolean> editableMap = testData.getEditableFieldsMap();
        this.editableFieldsMap = new HashMap<>(editableMap);
        helper.waitUntilDisplayed(listMenuElement);
        // General
        log.info("Check 'General'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_CONTAINER), valuesMap));
        // click edit at first
        new EditCredSetPage(pxDriver).editInstance(GENERAL_CONTAINER);
        hamcrest.append(checkPreviewElementsEditable(helper.getElement(GENERAL_CONTAINER), editableMap));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
        return this;
    }

    public CredSetPreviewPage checkCredSetDetails(Map<String, String> valuesMap) {
        log.info("Check CredSet details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        this.missedDataMap = new HashMap<>(valuesMap);
        helper.waitUntilDisplayed(listMenuElement);
        // General
        log.info("Check 'General'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_CONTAINER), valuesMap));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
        return this;
    }
}
