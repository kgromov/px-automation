package px.reports.pingPost;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.ReportsPage;
import px.reports.Valued;
import utils.SoftAssertionHamcrest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.pingPost.PingPostReportFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.pingPost.PingPostReportTestData.*;

/**
 * Created by kgr on 5/30/2017.
 */
public class PingPostReportPage extends ReportsPage implements GraphicsPage {

    public PingPostReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.PING_POST_REPORT_LINK);
        super.checkPage();
    }

    public void checkReportTypeFilter(PingPostReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Report type' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER);
        // check parent filter
        setReportTypeFilter(testData);
        // check table
        checkCellsData(testData, testData.getItemsByReportType());
        // filter reset
        resetFilter(filterLocator, testData.getReportType(),
                testData.getItemsTotalCount(), DEFAULT_REPORT_TYPE.equals(testData.getReportType()));
        hamcrest.assertAll();
    }

    public void setReportTypeFilter(PingPostReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType());
    }

    public void checkPublisherFilter(PingPostReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publisher' filter");
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // check parent filter
        setFilter(publisherFilterLocator, publisherFilterValue);
        // check items
        checkFilterItems(publisherInstanceFilterLocator, ObjectIdentityData.getAllIDs(testData.getSubIDs()));
        // check table
        checkCellsData(testData, testData.getItemsByPublisherID());
        hamcrest.assertAll();
    }

    public void checkPublisherInstanceFilter(PingPostReportTestData testData) {
        if (!testData.hasPublisherInstances()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Publisher Instance' filter");
        String filterValue = testData.getSubID().getId();
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // filter by instance if exists
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByPublisherInstanceGUID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsByPublisherID().length());
        // check table
        checkCellsData(testData, testData.getItemsByPublisherID());
        hamcrest.assertAll();
    }

    public void resetPublishersFilter(PingPostReportTestData testData) {
        String filterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsByReportTypeCount());
    }

    public void checkAllFilters(PingPostReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // filter values
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherInstanceFilterValue = testData.hasPublisherInstances() ? testData.getSubID().getId() : null;
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PingPostReportTestData.PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PingPostReportTestData.PUBLISHER_INSTANCE_FILTER);
        // set all filters in chain
        setFilter(publisherFilterLocator, publisherFilterValue);
        if (testData.hasPublisherInstances())
            setFilter(publisherInstanceFilterLocator, publisherInstanceFilterValue);
        // check table
        checkCellsData(testData, testData.getItemsByAllFilters());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(),
                    resetData.getFilterResetKey().equals(REPORT_TYPE_FILTER) && testData.getReportType().equals(DEFAULT_REPORT_TYPE));
            checkCellsData(testData, resetData.getItemsByFiltersCombination());
        }
        hamcrest.assertAll();
    }

    /*
        1) Vertical, Publisher = {Bubble Chart, Column Chart}
        2) Hour, Day, Week = {Stacked Area, Bubble Chart}
     */
    public void checkGraphics(PingPostReportTestData testData, JSONArray dataList, Valued[] enums) {
        log.info("Check 'Graphics' filter");
        this.hamcrest = new SoftAssertionHamcrest();
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // stacked area chart
        if (testData.hasBubbleChart(testData.getReportType()))
            hamcrest.append(checkBubbleChart(testData, dataList, enums));
        else hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        //  column chart
        if (testData.hasBubbleChart(testData.getReportType()))
            hamcrest.append(checkColumnChart(testData, dataList, enums));
        else hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // after reset - default graphic
        log.info(String.format("Check graphic after reset - '%s' is expected",
                testData.hasBubbleChart(testData.getReportType()) ? "None" : "Stacked Area chart"));
        resetGraphics();
        if (testData.hasBubbleChart(testData.getReportType())) hamcrest.append(checkNoneGraphics(enums));
        else hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        hamcrest.assertAll();
    }
}
