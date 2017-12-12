package px.reports.buyerCampaign;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import pages.groups.GraphicsPage;
import pages.locators.ReportPageLocators;
import px.reports.ReportTestData;
import px.reports.ReportsPage;
import px.reports.Valued;
import px.reports.dto.AbstractFiltersResetData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;
import static px.reports.buyerCampaign.BuyerCampaignReportFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.buyerCampaign.BuyerCampaignReportTestData.*;
import static px.reports.buyers.BuyerReportFiltersEnum.DEFAULT_BAYER_CATEGORY;

/**
 * Created by kgr on 11/16/2016.
 */
public class BuyerCampaignReportPage extends ReportsPage implements GraphicsPage {

    public BuyerCampaignReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void navigateToPage() {
        setMenu(DashboardMenuEnum.REPORTS);
        helper.click(ReportPageLocators.BUYER_CAMPAIGN_REPORT_LINK);
        super.checkPage();
    }

    public void checkReportTypeFilter(BuyerCampaignReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerCampaignReportFiltersEnum.REPORT_TYPE.getValue()));
        // check table
        checkCellsData(testData, testData.getItemsByReportType(), testData.getFieldsNonCategory());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType(),
                testData.getItemsTotalCount(), DEFAULT_REPORT_TYPE.equals(testData.getReportType()));
        hamcrest.assertAll();
    }

    public void setReportTypeFilter(BuyerCampaignReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, REPORT_TYPE_FILTER), testData.getReportType());
    }

    public void checkBuyerCategoryFilter(BuyerCampaignReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerCampaignReportFiltersEnum.BUYER_CATEGORY.getValue()));
        // check table
        checkCellsData(testData, testData.getItemsByBuyerCategory());
        // filter reset
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER), testData.getBuyerCategory(),
                testData.getItemsByReportType().length(), DEFAULT_BAYER_CATEGORY.equals(testData.getBuyerCategory()));
        ObjectIdentityData defaultBuyerCampaign = testData.getDefaultBuyerCampaign();
        String defaultFilterValue = defaultBuyerCampaign.getId() + " - " + defaultBuyerCampaign.getName();
        checkFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), defaultFilterValue);
        hamcrest.assertAll();
    }

    public void setBuyerCategoriesFilter(BuyerCampaignReportTestData testData) {
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CATEGORY_FILTER), testData.getBuyerCategory());
    }

    public void checkBuyerCampaignFilter(BuyerCampaignReportTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        log.info(String.format("Check '%s' filter", BuyerCampaignReportFiltersEnum.BUYER_CAMPAIGN.getValue()));
        if (testData.hasCampaignsByCategory()) {
            ObjectIdentityData buyerCampaign = testData.getBuyerCampaign();
            ObjectIdentityData defaultBuyerCampaign = testData.getDefaultBuyerCampaignByCategory();
            String filterValue = buyerCampaign.getId() + " - " + buyerCampaign.getName();
            String defaultFilterValue = defaultBuyerCampaign.getId() + " - " + defaultBuyerCampaign.getName();
            // check parent filter
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), filterValue);
            // check table
            checkCellsData(testData, testData.getItemsByBuyerCampaign());
            // filter reset
            resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), filterValue,
                    testData.getItemsByBuyerCategory().length(), filterValue.equals(defaultFilterValue));
            checkFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), defaultFilterValue);
        } else {
            // check items
            checkFilterItems(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), new ArrayList<>());
            // check table
            checkCellsData(testData, testData.getItemsByBuyerCampaign());
        }
        hamcrest.assertAll();
    }

    public void checkBuyerCampaigns(BuyerCampaignReportTestData testData) {
        if (!testData.hasCampaignsByCategory()) return;
        checkFilterItems(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER), testData.getBuyerCampaigns().stream()
                .map(campaign -> campaign.getId() + " - " + campaign.getName())
                .collect(Collectors.toList()));
    }

    public void checkTransactionTypesFilter(BuyerCampaignReportTestData testData) {
        if (!testData.allowTransactionType()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check 'All types' filter");
        String filterLocator = String.format(GENERIC_PARAMETRIZED_FILTER, TRANSACTION_TYPE_FILTER);
        // check parent filter
        setFilter(filterLocator, testData.getTransactionTypeList());
        // check table
        checkCellsData(testData, testData.getItemsByTransactionType());
        // filter reset
        resetFilter(filterLocator, testData.getTransactionTypeList().toString(),  testData.getItemsByBuyerCategory().length());
        hamcrest.assertAll();
    }

    public void checkAllFilters(BuyerCampaignReportTestData testData) {
        if (!testData.allowTransactionType()) return;
        hamcrest = new SoftAssertionHamcrest();
        log.info("Check all filters\tSet in chain then randomly reset and check combination in table");
        // set all filters in chain
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, BUYER_CAMPAIGN_FILTER),
                testData.getBuyerCampaign().getId() + " - " + testData.getBuyerCampaign().getName());
        setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, TRANSACTION_TYPE_FILTER), testData.getTransactionTypeList());
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

    @Override
    public void checkGraphics(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        log.info("Check 'Graphics' filter");
        this.hamcrest = new SoftAssertionHamcrest();
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // stacked area chart
        hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        //  column chart
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        // after reset - default graphic
        log.info("Check graphic after reset - 'Stacked Area chart' is expected");
        resetGraphics();
        hamcrest.append(checkStackedAreaChart(testData, dataList, enums));
        hamcrest.assertAll();
    }
}