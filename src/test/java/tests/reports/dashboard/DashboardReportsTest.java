package tests.reports.dashboard;

import org.testng.annotations.Test;
import px.reports.ReportTestData;
import px.reports.dashboard.DashboardReportPage;
import px.reports.dashboard.data.DashboardReportsTestData;
import tests.LoginTest;

import static pages.locators.DashboardPageLocators.CAMPAIGNS_CONTAINER;
import static pages.locators.DashboardPageLocators.OFFERS_CONTAINER;
import static px.reports.dashboard.data.DashboardReportsTestData.CAMPAIGNS_LINK;
import static px.reports.dashboard.data.DashboardReportsTestData.OFFERS_LINK;

/**
 * Created by konstantin on 23.08.2017.
 */
public class DashboardReportsTest extends LoginTest {

    @Test
    public void checkOffersOverview() {
        ReportTestData testData = new DashboardReportsTestData()
                .withOffers().getOffersData();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check offers report
        reportPage.checkNestedReport(testData, OFFERS_CONTAINER);
        // check redirect
        reportPage.checkRedirectLink(OFFERS_CONTAINER, OFFERS_LINK);
    }

    @Test
    public void checkCampaignsOverview() {
        ReportTestData testData = new DashboardReportsTestData()
                .withCampaigns().getCampaignsData();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check campaigns report
        reportPage.checkNestedReport(testData, CAMPAIGNS_CONTAINER);
        // check redirect
        reportPage.checkRedirectLink(CAMPAIGNS_CONTAINER, CAMPAIGNS_LINK);
    }
}
