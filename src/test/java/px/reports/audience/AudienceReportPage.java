package px.reports.audience;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportsPage;
import px.reports.dto.AbstractFiltersResetData;
import utils.SoftAssertionHamcrest;

import java.util.stream.Collectors;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.audience.AudienceReportTestData.*;

/**
 * Created by kgr on 4/24/2017.
 */
public class AudienceReportPage extends ReportsPage implements GraphicsPage {

    public AudienceReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.AUDIENCE_REPORT_LINK);
        checkPage();
    }

    public void checkReportTypeFilter(AudienceReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Report Type' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getReportType());
        // check table
        checkCellsData(testData, testData.getItemsByReportType());
        // reset filter
        resetFilter(filterLocator, testData.getReportType(), /*testData.getItemsByDefaultReportType().length()*/ // if vertical already set
                testData.getItemsTotalCount(), testData.getReportType().equals(DEFAULT_REPORT_TYPE));
        hamcrest.assertAll();
    }

    public void setReportTypeFilter(AudienceReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType());
    }

    public void setVerticalFilter(AudienceReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER), testData.getVertical());
    }

    public void checkVerticalFilter(AudienceReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Vertical' filter");
        String verticalFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER);
        String reportTypeFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER);
        // check parent filter
        setFilter(verticalFilterLocator, testData.getVertical());
        // check table
        checkCellsData(testData, testData.hasChangedAfterVerticalReset()
                ? testData.getItemsByDefaultReportType() : testData.getItemsByVertical());
        // check proper report types
        checkFilterItems(reportTypeFilterLocator, testData.getReportTypes());
        // filter reset
        resetFilter(verticalFilterLocator, testData.getVertical(),
                testData.getDefaultReportTypes().contains(testData.getReportType())
                        ? testData.getItemsByReportType().length() : testData.getItemsTotalCount());
        // check proper report types
        checkFilterItems(reportTypeFilterLocator, testData.getDefaultReportTypes());
        hamcrest.assertAll();
    }

    public void checkVerticalFilter(AudienceVerticalReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Vertical' filter");
        String verticalFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, VERTICAL_FILTER);
        String reportTypeFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER);
        // check parent filter
//        setFilter(verticalFilterLocator, testData.getVertical());
        // check table
        checkCellsData(testData, testData.getItemsByDefaultReportType());
        // check proper report types
        checkFilterItems(reportTypeFilterLocator, testData.getReportTypes());
        // filter reset
        resetFilter(verticalFilterLocator, testData.getVertical(), testData.getItemsTotalCount());
        // check proper report types
        checkFilterItems(reportTypeFilterLocator, testData.getDefaultReportTypes());
        hamcrest.assertAll();
    }

    public void checkBuyerFilter(AudienceReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Buyer' filter");
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // check parent filter
        setFilter(buyerFilterLocator, buyerFilterValue);
        // check items
        checkBuyerCampaigns(testData);
        // check table
        checkCellsData(testData, testData.getItemsByBuyerGUID());
        hamcrest.assertAll();
    }

    public void checkPublisherFilter(AudienceReportTestData testData) {
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
        hamcrest.assertAll();
    }

    public void resetBuyersFilter(AudienceReportTestData testData) {
        String filterValue = testData.getBuyer().getName();
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        resetFilter(buyerFilterLocator, filterValue,
                buyerInstanceFilterLocator, testData.getItemsByReportType().length());
    }

    public void resetPublishersFilter(AudienceReportTestData testData) {
        String filterValue = testData.getBuyer().getName();
        String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
        resetFilter(publisherFilterLocator, filterValue,
                publisherInstanceFilterLocator, testData.getItemsByReportType().length());
    }

    public void checkBuyerCampaigns(AudienceReportTestData testData) {
        if (!testData.hasBuyerInstances()) return;
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        checkFilterItems(buyerInstanceFilterLocator, testData.getBuyerCampaigns().stream()
                .map(campaign -> campaign.getId() + " - " + campaign.getName())
                .collect(Collectors.toList()));
    }

    public void checkBuyerInstanceFilter(AudienceReportTestData testData) {
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
        if (Config.isAdmin()) {
            // check table
            checkCellsData(testData, testData.getItemsByBuyerGUID());
        }
        hamcrest.assertAll();
    }

    public void checkPublisherInstanceFilter(AudienceReportTestData testData) {
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

    public void checkAllFilters(AudienceReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // filter values
        String buyerFilterValue = testData.getBuyer().getName();
        String buyerInstanceFilterValue = testData.hasBuyerInstances() ?
                testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName() : null;
        String buyerFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_FILTER);
        String buyerInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_INSTANCE_FILTER);
        // set all filters in chain
//        setReportTypeFilter(testData);
        setVerticalFilter(testData);
        // there is only buyer campaigns filter under buyer user
        if (Config.isAdmin()) {
            String publisherFilterValue = testData.getPublisher().getId() + " - " + testData.getPublisher().getName();
            String publisherInstanceFilterValue = testData.hasPublisherInstances() ? testData.getSubID().getId() : null;
            String publisherFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
            String publisherInstanceFilterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_INSTANCE_FILTER);
            setFilter(publisherFilterLocator, publisherFilterValue);
            if (testData.hasPublisherInstances())
                setFilter(publisherInstanceFilterLocator, publisherInstanceFilterValue);
            setFilter(buyerFilterLocator, buyerFilterValue);
        }
        if (testData.hasBuyerInstances())
            setFilter(buyerInstanceFilterLocator, buyerInstanceFilterValue);
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
}