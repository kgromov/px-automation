package px.objects.users.pages;

import configuration.browser.PXDriver;
import pages.DetailsPage;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.Map;

import static pages.locators.ContactInfoLocators.RIGHT_MANAGEMENT_CONTAINER;
import static px.objects.brokers.BrokersPageLocators.GENERAL_CONTAINER;

/**
 * Created by kgr on 8/17/2017.
 */
public class UserPreviewPage extends DetailsPage {

    public UserPreviewPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public UserPreviewPage checkPreview(Map<String, String> detailsMap) {
        log.info("Check subId details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        this.missedDataMap = new HashMap<>(detailsMap);
        helper.waitUntilDisplayed(listMenuElement);
        listMenuElement.setByIndex(1);
        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_CONTAINER), detailsMap));
        listMenuElement.setByIndex(2);
        hamcrest.append(checkPreviewElements(helper.getElement(RIGHT_MANAGEMENT_CONTAINER), detailsMap));
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
        return this;
    }
}
