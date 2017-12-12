package px.reports.sourceQuality;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.GraphicsEnum;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import px.reports.Valued;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pages.locators.LoginPageLocators.LOGGED_USER_NAME;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.ReportPageLocators.GRAPHICS_FILTER;
import static px.reports.sourceQuality.SourceQualityScoreTestData.*;

/**
 * Created by kgr on 11/16/2016.
 */
public class SourceQualityScoreReportPage extends ReportsPage implements GraphicsPage {

    public SourceQualityScoreReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.SOURCE_QUALITY_SCORE_REPORT_LINK);
        super.checkPage();
    }

    public void checkPublisherFilter(SourceQualityScoreTestData testData) {
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
        checkCellsData(testData, testData.getItemsByPublisherGUID());
        // workaround for overlapped tooltip
        helper.getElement(LOGGED_USER_NAME).click();
        hamcrest.assertAll();
    }

    public void checkBuyerFilter(SourceQualityScoreTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer' filter");
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // check parent filter
        setFilter(buyerFilterLocator, buyerFilterValue);
        // check items
        checkFilterItems(buyerInstanceFilterLocator, testData.getBuyerCampaigns().stream()
                .map(campaign -> campaign.getId() + " - " + campaign.getName())
                .collect(Collectors.toList()));
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        hamcrest.assertAll();
    }

    public void resetBuyersFilter(SourceQualityScoreTestData testData) {
        String filterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // workaround for overlapped tooltip
        helper.getElement(LOGGED_USER_NAME).click();
        resetFilter(buyerFilterLocator, filterValue,
                buyerInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void resetPublishersFilter(SourceQualityScoreTestData testData) {
        String filterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // workaround for overlapped tooltip
        helper.getElement(LOGGED_USER_NAME).click();
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsTotalCount());
    }

    public void checkBuyerInstanceFilter(SourceQualityScoreTestData testData) {
        if (!testData.hasBuyerInstances()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer Instance' filter");
        String filterValue = testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName();
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // filter by instance if exists
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByBuyerInstanceGUID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsByBuyerGUID().length());
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        hamcrest.assertAll();
    }

    public void checkBuyerCampaigns(SourceQualityScoreTestData testData) {
        if (!testData.hasBuyerInstances()) return;
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        checkFilterItems(buyerInstanceFilterLocator, testData.getBuyerCampaigns().stream()
                .map(campaign -> campaign.getId() + " - " + campaign.getName())
                .collect(Collectors.toList()));
    }

    public void checkPublisherInstanceFilter(SourceQualityScoreTestData testData) {
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
        resetFilter(filterLocator, filterValue, testData.getItemsByPublisherGUID().length());
        // check table
        checkCellsData(testData, testData.getItemsByPublisherGUID());
        hamcrest.assertAll();
    }

    public void checkAllCells(SourceQualityScoreTestData testData) {
        checkCellsData(testData, testData.getAllRowsArray());
    }

    public void checkAllFilters(SourceQualityScoreTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // filter values
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerInstanceFilterValue = testData.hasBuyerInstances() ?
                testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName() : null;
        String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
        String publisherInstanceFilterValue = testData.hasPublisherInstances() ? testData.getSubID().getId() : null;
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        // set all filters in chain
        setFilter(buyerFilterLocator, buyerFilterValue);
        if (testData.hasBuyerInstances())
            setFilter(buyerInstanceFilterLocator, buyerInstanceFilterValue);
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
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getItemsTotalCount());
            checkCellsData(testData, resetData.getItemsByFiltersCombination());
        }
        hamcrest.assertAll();
    }

    public void checkGraphicsUnderBuyer(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        log.info("Check 'Graphics' filter");
        this.hamcrest = new SoftAssertionHamcrest();
        // bubble chart
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // after reset - default graphic
        log.info("Check graphic after reset - 'Bubble chart' is expected");
        resetGraphics();
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // check that column chart is missed
        checkFilterItems(GRAPHICS_FILTER, Arrays.asList(GraphicsEnum.NONE.getValue(), GraphicsEnum.BUBBLE_CHART.getValue()));
        hamcrest.assertAll();
    }

    @Override
    public void checkCalendarDateRanges(ReportTestData testData) {
        List<String> columns = FieldFormatObject.getFieldNames(testData.getFields());
        // more exclusions than columns to check
        columns.remove(SourceQualityReportColumnsEnum.LEADS.getValue());
        columns.remove(SourceQualityReportColumnsEnum.PAYOUT.getValue());
        columns.remove(SourceQualityReportColumnsEnum.CONVERSIONS.getValue());
        columns.remove(SourceQualityReportColumnsEnum.QIQ.getValue());
        columns.remove(SourceQualityReportColumnsEnum.LEAD_RETURN.getValue());
        checkCalendarDateRanges(testData, columns.toArray(new String[columns.size()]));
    }
}