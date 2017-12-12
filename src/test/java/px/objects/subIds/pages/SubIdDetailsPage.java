package px.objects.subIds.pages;

import configuration.browser.PXDriver;
import pages.DetailsPage;
import pages.groups.Actions;
import pages.groups.Searchable;;
import px.objects.subIds.SubIdTestData;
import utils.SoftAssertionHamcrest;

import java.util.HashMap;
import java.util.Map;

import static px.objects.publishers.PublishersPageLocators.ADDRESS_INFO_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.CONTACT_INFO_CONTAINER;
import static px.objects.publishers.PublishersPageLocators.GENERAL_INFO_CONTAINER;
import static px.objects.subIds.SubIdTestData.CONTACT_ITEMS;

/**
 * Created by kgr on 7/14/2017.
 */
public class SubIdDetailsPage extends DetailsPage implements Searchable, Actions {

    public SubIdDetailsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void checkDetails(SubIdTestData testData) {
        log.info("Check subId details");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Map<String, String> detailsMap = testData.getDetailsMap();
        this.missedDataMap = new HashMap<>(detailsMap);
        helper.waitUntilDisplayed(listMenuElement);
        // General
        log.info("Check 'General'");
        listMenuElement.setByIndex(1);
        helper.waitUntilDisplayed(GENERAL_INFO_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(GENERAL_INFO_CONTAINER), detailsMap, testData.getFields()));
        // Commercial
        log.info("Check 'Contact Info'");
        listMenuElement.setByIndex(2);
        helper.waitUntilDisplayed(CONTACT_INFO_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(CONTACT_INFO_CONTAINER), detailsMap, testData.getFields()));
        // Volume and Quality
        log.info("Check 'Address Info'");
        listMenuElement.setByIndex(3);
        helper.waitUntilDisplayed(ADDRESS_INFO_CONTAINER);
        // check preview elements with response data
        hamcrest.append(checkPreviewElements(helper.getElement(ADDRESS_INFO_CONTAINER), detailsMap, testData.getFields()));
        // workaround cause contact items present partially and not in fields metadata format
        extraFieldsList.removeAll(CONTACT_ITEMS);
        hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
        hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
        hamcrest.assertAll();
    }
}
