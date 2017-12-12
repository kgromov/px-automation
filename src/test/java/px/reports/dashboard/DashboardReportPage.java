package px.reports.dashboard;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import elements.dropdown.FilteredDropDown;
import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import utils.SoftAssertionHamcrest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static pages.locators.DashboardPageLocators.OVERVIEW_BUTTON;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;

/**
 * Created by konstantin on 23.08.2017.
 */
public class DashboardReportPage extends ReportsPage {
    public DashboardReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.DASHBOARD);
        waitPageIsLoaded();
    }

    public void checkNestedReport(ReportTestData testData, String container) {
        log.info(String.format("Check report '%s' table inside container '%s'", testData.getInstanceGroup(), container));
        helper.waitUntilDisplayed(container);
        helper.scrollToElement(By.xpath(container));
        setTable(container);
        if (testData.getItemsTotalCount() == 0) {
            checkEmptyTable();
        } else {
            int tableRows = tableElement.getTotalRowsCount();
            int toIndex = testData.getItemsCurrentTotalCount() > tableRows ? tableRows : testData.getItemsCurrentTotalCount();
            checkCellsData(new JSONArray(testData.getAllRowsArray().toList().subList(0, toIndex)),
                    HTMLHelper.getTableCells(tableElement.getWrappedElement(), false), testData.getFields());
        }
    }

    public void checkRedirectLink(String container, String relativeLink) {
        checkRedirectLink(container, OVERVIEW_BUTTON, relativeLink);
    }

    public void checkRedirectLink(String container, By linkLocator, String relativeLink) {
        log.info("Check that overview link leads to proper place - " + relativeLink);
        helper.waitUntilDisplayed(container);
        WebElement overviewButton = helper.getElement(helper.getElement(container), linkLocator);
        try {
            assertThat("Overview link contains proper relative address",
                    URLDecoder.decode(overviewButton.getAttribute("href"), "UTF-8"),
                    containsString(relativeLink));
            overviewButton.click();
            waitPageIsLoaded();
            assertThat("Check that redirect to API builder have been conducted",
                    URLDecoder.decode(pxDriver.getCurrentUrl(), "UTF-8"),
                    containsString(relativeLink));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void checkFilterAfterRedirect(String filterName, String filterValue, ReportTestData testData) {
        setDateRanges();
        log.info(String.format("Actual calendar date range [%s - %s]", initialFromPeriod, initialToPeriod));
        log.info(String.format("Expected calendar date range [%s - %s]", testData.getFromPeriod(), testData.getToPeriod()));
        hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Check start date set correctly", initialFromPeriod, equalTo(testData.getFromPeriod()));
        hamcrest.assertThat("Check end date set correctly", initialToPeriod, equalTo(testData.getToPeriod()));
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(String.format(GENERIC_PARAMETRIZED_FILTER, filterName)));
        hamcrest.assertThat(String.format("Report '%s' is set correctly", filterName),
                filter.getValue(), equalToIgnoringCase(filterValue));
        hamcrest.assertAll();

    }
}
