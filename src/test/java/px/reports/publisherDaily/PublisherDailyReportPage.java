package px.reports.publisherDaily;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.openqa.selenium.By;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import px.reports.Valued;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import utils.SoftAssertionHamcrest;

import java.util.List;
import java.util.stream.Collectors;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.publisherDaily.PublisherDailyReportFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.publisherDaily.PublisherDailyReportTestData.*;

/**
 * Created by konstantin on 10.04.2017.
 */
public class PublisherDailyReportPage extends ReportsPage implements GraphicsPage {

    public PublisherDailyReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.PUBLISHER_DAILY_REPORT_LINK);
        // do not check columns cause each column is grouping
        waitPageIsLoaded(40);
    }

    public void checkReportTypeFilter(PublisherDailyReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Report Type' filter");
        // check table
        checkCellsData(testData, testData.getItemsByReportType(), testData.getNonGroupingFields());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType(),
                testData.getItemsTotalCount(), DEFAULT_REPORT_TYPE.equals(testData.getReportType()));
        hamcrest.assertAll();
    }

    public void setReportTypeFilter(PublisherDailyReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType());
    }

    public void checkGroupingFilter(PublisherDailyReportTestData testData) {
        if (!testData.hasGrouping()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Grouping' filter");
        // check table
        checkCellsData(testData, testData.getItemsByGrouping(), testData.getNonGroupingFields());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, GROUPING_FILTER), testData.getGroupingsList().toString(),
                testData.getItemsByReportType().length());
        // to be invisible
        testData.resetGrouping();
        testData.getGroupingToSetFields().forEach(field -> {
            By locator = By.cssSelector(String.format("[data-field-name='%s']", field.getName()));
            hamcrest.assertThat(String.format("Column by grouping field '%s' was removed from table", field.getName()),
                    !helper.isElementAccessible(tableElement.getWrappedElement(), locator));
        });
        hamcrest.assertAll();
    }

    /**
     * Specific verification - columns adding/removing
     * Do not check columns cause each column is grouping
     * 1) Store columns data-field-name before filter set
     * 2) Check that all grouping are hidden
     * 3) Set filter
     * 4) Check that set groupings added proper columns to table
     *
     * @param testData - PublisherDailyReportTestData
     */
    public void setGroupingFilter(PublisherDailyReportTestData testData) {
        // grouping list could be empty
        if (!testData.hasGrouping()) return;
        // check that grouping fields are hidden
        List<Integer> groupingIndexes = testData.getGroupingToSetFields().stream().map(FieldFormatObject::getIndex).collect(Collectors.toList());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, GROUPING_FILTER), groupingIndexes.toArray(new Integer[groupingIndexes.size()]));
        testData.setGrouping();
        // check visible
        testData.getGroupingToSetFields().forEach(field -> {
            By locator = By.cssSelector(String.format("[data-field-name='%s']", field.getName()));
            hamcrest.assertThat(String.format("Column by grouping field '%s' was added to table", field.getName()),
                    helper.isElementAccessible(tableElement.getWrappedElement(), locator));
        });
    }

    public void checkPublishersFilter(PublisherDailyReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Publishers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getPublishers()
                .stream().map(option -> option.getId() + " - " + option.getName())
                .collect(Collectors.toList()));
        // check table
        checkCellsData(testData, testData.getItemsByPublisherIDs(), testData.getNonGroupingFields());
        // filter reset
        String filterValue = testData.getPublishers().stream().map(p -> p.getId() +
                "-" + p.getName()).collect(Collectors.joining(", "));
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkPublisherManagersFilter(PublisherDailyReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Publisher Managers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_MANAGER_FILTER);
        // check parent filter
        setFilter(filterLocator, ObjectIdentityData.getAllNames(testData.getPublisherManagers()));
        // check table
        checkCellsData(testData, testData.getItemsByPublisherManagers(), testData.getNonGroupingFields());
        // filter reset
        resetFilter(filterLocator, ObjectIdentityData.getAllNames(
                testData.getPublisherManagers()).toString(), testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkOffersFilter(PublisherDailyReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Offers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getOffers()
                .stream().map(option -> option.getId() + " - " + option.getName())
                .collect(Collectors.toList()));
        // check table
        checkCellsData(testData, testData.getItemsByOfferIDs(), testData.getNonGroupingFields());
        // filter reset
        String filterValue = testData.getOffers().stream().map(p -> p.getId() +
                "-" + p.getName()).collect(Collectors.joining(", "));
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkBrowsersFilter(PublisherDailyReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Browsers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BROWSER_FILTER);
        setFilter(filterLocator, testData.getBrowsersList());
        // check table
        checkCellsData(testData, testData.getItemsByBrowsers(), testData.getNonGroupingFields());
        // filter reset
        resetFilter(filterLocator, testData.getBrowsersList().toString(),
                testData.getItemsTotalCount());
        hamcrest.assertAll();
    }

    public void checkAllFilters(PublisherDailyReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // set all filters in chain
        if (Config.isAdmin()) {
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), testData.getPublishers()
                    .stream().map(option -> option.getId() + " - " + option.getName())
                    .collect(Collectors.toList()));
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_MANAGER_FILTER),
                    ObjectIdentityData.getAllNames(testData.getPublisherManagers()));
        }
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER), testData.getOffers()
                .stream().map(option -> option.getId() + " - " + option.getName())
                .collect(Collectors.toList()));
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BROWSER_FILTER), testData.getBrowsersList());
        // check table
        checkCellsData(testData, testData.getItemsByAllFilters(), testData.getNonGroupingFields());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getItemsCurrentTotalCount());
            checkCellsData(testData, resetData.getItemsByFiltersCombination(), testData.getNonGroupingFields());
        }
        hamcrest.assertAll();
    }

    public void displayAllTableColumns(PublisherDailyReportTestData testData) {
        columnsFilter.setCheckboxes(FieldFormatObject.getFieldNames(testData.getNonGroupingFields()));
    }

    @Override
    public void checkGraphics(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        log.info("Check 'Graphics' filter");
        this.hamcrest = new SoftAssertionHamcrest();
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // stacked area chart
        hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        //  bubble chart
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // after reset - default graphic
        log.info("Check graphic after reset - 'Stacked Area chart' is expected");
        resetGraphics();
        hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        hamcrest.assertAll();
    }
}