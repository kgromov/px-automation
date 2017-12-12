package px.objects.campaigns.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import elements.dropdown.TableDropDown;
import org.openqa.selenium.WebElement;
import pages.OverviewPage;
import pages.groups.Actions;
import px.objects.campaigns.CampaignColumnsEnum;
import px.objects.campaigns.CampaignTestData;
import px.objects.filters.FilterManagementPage;
import px.objects.sourceManagement.pages.SourceManagementPage;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static pages.locators.DashboardPageLocators.CREATE_INSTANCE_PARAMETERIZED_BUTTON;
import static pages.locators.ElementLocators.ACTIONS_CELL;
import static px.objects.campaigns.CampaignsPageLocators.FILTER_MANAGEMENT_LINK;
import static px.objects.campaigns.CampaignsPageLocators.SOURCE_MANAGEMENT_LINK;
import static px.reports.campaigns.CampaignDetailsUnderBuyerTestData.ADMIN_MENU_ITEMS;

/**
 * Created by kgr on 10/19/2016.
 */
public class CampaignsPage extends OverviewPage implements Actions {

    public CampaignsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public CampaignsPage navigateToObjects() {
        setMenu(DashboardMenuEnum.CAMPAIGNS);
        super.checkPage();
        return this;
    }

    public FilterManagementPage navigateToFilterManagement(CampaignTestData testData){
        log.info(String.format("Log navigate to 'Filter management' of campaign '%s'", testData.getName()));
        filterInstanceInTable(testData.getName(), CampaignColumnsEnum.NAME.getValue());
        WebElement cell = findCellByValue(tableElement, testData.getName());
        cell.click();
        setActionByItemLink(cell, FILTER_MANAGEMENT_LINK);
        return new FilterManagementPage(pxDriver);
    }

    public SourceManagementPage navigateToSourceManagement(ObjectIdentityData campaign){
        log.info(String.format("Log navigate to 'Filter management' of campaign '%s'", campaign.getName()));
        filterInstanceInTable(campaign.getName(), CampaignColumnsEnum.NAME.getValue());
        WebElement cell = findCellByValue(tableElement, campaign.getName());
        cell.click();
        setActionByItemLink(cell, SOURCE_MANAGEMENT_LINK);
        return new SourceManagementPage(pxDriver);
    }

    // if buyerCategory of created campaign is Lead
    public CampaignsPage redirect(CampaignTestData testData) {
        if (testData.isRedirectAfterCreation()) {
            // check by campaign guid in future
            // /campaigns/<guid>/apibuilder
            log.info("Check that redirect to API builder have been conducted");
            pxDriver.waitForUrlContains("apibuilder");
            assertThat("Check that redirect to API builder have been conducted", pxDriver.getCurrentUrl(), containsString("apibuilder"));
            log.info("Redirect from API builder to campaigns overview");
            menuElement.setByText(DashboardMenuEnum.CAMPAIGNS.getValue());
            confirm(false);
        }
        return this;
    }

    // buyer user
    public CampaignsPage checkReadOnly() {
        log.info("Check that buyer grants are correct for campaigns page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Create 'campaign' button is hidden", helper.isElementAccessible(
                String.format(CREATE_INSTANCE_PARAMETERIZED_BUTTON, instanceGroup)), equalTo(false));
        // before that some sort of condition - campaigns per buyer
        if (!tableElement.isTableEmpty()) {
            int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
            TableDropDown actions = new TableDropDown(tableElement.getCellAt(1, columnIndex + 1));
            List<String> campaignActionItems = actions.getItems();
            ADMIN_MENU_ITEMS.forEach(item -> {
                hamcrest.assertThat(String.format("Admin action item '%s' is present under buyer user", item),
                        campaignActionItems.contains(item), equalTo(false));
            });
        }
        hamcrest.assertAll();
        return this;
    }
}