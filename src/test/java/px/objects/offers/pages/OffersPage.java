package px.objects.offers.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import elements.dropdown.TableDropDown;
import pages.OverviewPage;
import px.objects.offers.OfferColumnsEnum;
import px.objects.offers.OfferTestData;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static pages.locators.DashboardPageLocators.CREATE_INSTANCE_PARAMETERIZED_BUTTON;
import static pages.locators.ElementLocators.ACTIONS_CELL;
import static px.objects.offers.OfferTestData.ADMIN_MENU_ITEMS;

/**
 * Created by konstantin on 05.10.2016.
 */
public class OffersPage extends OverviewPage {

    public OffersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public OffersPage navigateToObjects(){
        setMenu(DashboardMenuEnum.OFFERS);
        super.checkPage();
        return this;
    }

    public OffersPage checkCreatedInstanceByName(OfferTestData testData){
        try{
            checkCreatedInstanceByName(testData, OfferColumnsEnum.NAME.getValue());
        } catch (AssertionError e){
            if(testData.isVisibleByStatus()) throw new AssertionError(e.getMessage());
        }
        return this;
    }

    // buyer user
    public OffersPage checkReadOnly() {
        log.info("Check that publisher grants are correct for offers page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Create 'campaign' button is hidden", helper.isElementAccessible(
                String.format(CREATE_INSTANCE_PARAMETERIZED_BUTTON, instanceGroup)), equalTo(false));
        // before that some sort of condition - campaigns per buyer
        if (!tableElement.isTableEmpty()) {
            int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
            TableDropDown actions = new TableDropDown(tableElement.getCellAt(1, columnIndex + 1));
            List<String> offerActionItems = actions.getItems();
            ADMIN_MENU_ITEMS.forEach(item -> {
                hamcrest.assertThat(String.format("Admin action item '%s' is missed under publisher user", item),
                        offerActionItems.contains(item), equalTo(false));
            });
        }
        hamcrest.assertAll();
        return this;
    }
}