package px.reports.publisherConversion;

import config.Config;
import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import pages.locators.ReportPageLocators;
import px.reports.ReportsPage;
import px.reports.dto.AbstractFiltersResetData;
import utils.SoftAssertionHamcrest;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.publisherConversion.PublisherConversionTestData.*;

/**
 * Created by kgr on 4/4/2017.
 */
public class PublisherConversionPage extends ReportsPage {

    public PublisherConversionPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.PUBLISHER_CONVERSION_REPORT_LINK);
        waitPageIsLoaded(40);
        displayAllTableColumns();
//        super.checkPage();
    }

    public void checkPublishersFilter(PublisherConversionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Publishers' filter");
        ObjectIdentityData publisherData = testData.getPublisher();
        String filterValue = publisherData.getId() + " - " + publisherData.getName();
        // check parent filter
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER);
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByPublisherID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
       /* // check table
        checkCellsData(testData, testData.getAllRowsArray());*/
        hamcrest.assertAll();
    }

    public void checkOffersFilter(PublisherConversionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Offers' filter");
        ObjectIdentityData offerData = testData.getOffer();
        String filterValue = offerData.getId() + " - " + offerData.getName();
        // check parent filter
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER);
        setFilter(filterLocator, filterValue);
        // check table
        checkCellsData(testData, testData.getItemsByOfferID());
        // filter reset
        resetFilter(filterLocator, filterValue, testData.getItemsTotalCount());
        /*// check table
        checkCellsData(testData, testData.getAllRowsArray());*/
        hamcrest.assertAll();
    }

    public void checkAdjustmentFilter(PublisherConversionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'Any Adjustment' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, ADJUSTMENT_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getAdjustment());
        // check table
        checkCellsData(testData, testData.getItemsByAdjustment());
        // filter reset
        resetFilter(filterLocator, testData.getAdjustment(), testData.getItemsTotalCount());
       /* // check table
        checkCellsData(testData, testData.getAllRowsArray());*/
        hamcrest.assertAll();
    }

    public void checkBrowsersFilter(PublisherConversionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Browsers' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, BROWSER_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getBrowser());
        // check table
        checkCellsData(testData, testData.getItemsByBrowser());
        // filter reset
        resetFilter(filterLocator, testData.getBrowser(), testData.getItemsTotalCount());
       /* // check table
        checkCellsData(testData, testData.getAllRowsArray());*/
        hamcrest.assertAll();
    }

    public void checkConversionStatusesFilter(PublisherConversionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All Conversion Statuses' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, CONVERSION_STATUS_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getConversionStatus());
        // check table
        checkCellsData(testData, testData.getItemsByConversionStatus());
        // filter reset
        resetFilter(filterLocator, testData.getConversionStatus(), testData.getItemsTotalCount());
       /* // check table
        checkCellsData(testData, testData.getAllRowsArray());*/
        hamcrest.assertAll();
    }

    public void checkAllFilters(PublisherConversionTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        ObjectIdentityData offerData = testData.getOffer();
        String offerFilterValue = offerData.getId() + " - " + offerData.getName();
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, ADJUSTMENT_FILTER), testData.getAdjustment());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, OFFER_FILTER), offerFilterValue);
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BROWSER_FILTER), testData.getBrowser());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, CONVERSION_STATUS_FILTER), testData.getConversionStatus());
        // admin only
        if (Config.isAdmin()) {
            ObjectIdentityData publisherData = testData.getPublisher();
            String publisherFilterValue = publisherData.getId() + " - " + publisherData.getName();
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, PUBLISHER_FILTER), publisherFilterValue);
        }
        // check table
        checkCellsData(testData, testData.getItemsByAllFilters());
        // reset filters randomly
        AbstractFiltersResetData resetData = testData.getResetData();
        while (resetData.isAnyFilterSet()) {
            resetData.resetFilter();
            log.info(String.format("Reset by '%s'", resetData.getFilterResetKey()));
            resetFilter(resetData.getResetFilterLocator(), resetData.getFilterResetValue(), testData.getItemsCurrentTotalCount());
            checkCellsData(testData, resetData.getItemsByFiltersCombination());
        }
        hamcrest.assertAll();
    }

    public void checkCalendarDateRanges(PublisherConversionTestData testData) {
        String[] exclusions = {
                PublisherConversionReportColumnsEnum.PUBLISHER_MANAGER.getValue(),
                PublisherConversionReportColumnsEnum.OFFER_URL.getValue(),
//                PublisherConversionReportColumnsEnum.SESSION_DATE.getValue(),
                PublisherConversionReportColumnsEnum.DATE_DIFF.getValue(),
                PublisherConversionReportColumnsEnum.CONVERSION_STATUS.getValue(),
                PublisherConversionReportColumnsEnum.NOTE.getValue(),
                PublisherConversionReportColumnsEnum.STATUS_MESSAGE.getValue(),
                PublisherConversionReportColumnsEnum.SESSION_IP.getValue(),
                PublisherConversionReportColumnsEnum.SOURCE.getValue(),
                PublisherConversionReportColumnsEnum.SUB_ID_3.getValue(),
                PublisherConversionReportColumnsEnum.SUB_ID_4.getValue(),
                PublisherConversionReportColumnsEnum.SUB_ID_5.getValue(),
                PublisherConversionReportColumnsEnum.BROWSER.getValue(),
                PublisherConversionReportColumnsEnum.USER_AGENT.getValue(),
                PublisherConversionReportColumnsEnum.ADJUSTMENT.getValue()
        };
        if (testData.isBigData()) checkCalendarDateRangesBigData(testData, exclusions);
        else checkCalendarDateRanges(testData, exclusions);
    }
}