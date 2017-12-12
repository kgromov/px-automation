package tests.reports.dashboard;

import org.testng.annotations.Test;
import px.reports.dashboard.DashboardReportPage;
import px.reports.dashboard.charts.Chart;
import px.reports.dashboard.charts.ColumnChart;
import px.reports.dashboard.charts.ListChart;
import px.reports.dashboard.data.LeadColumnChartsTestData;
import px.reports.dashboard.data.LeadStatisticsTestData;
import tests.LoginTest;

import static pages.locators.DashboardPageLocators.CONVERSION_COLUMN_CHART;
import static pages.locators.DashboardPageLocators.LEAD_COLUMN_CHART;
import static pages.locators.DashboardPageLocators.VIEW_MORE_BUTTON;
import static px.reports.dashboard.DashboardChartsEnum.*;
import static px.reports.dashboard.data.LeadColumnChartsTestData.CONVERSIONS_LINK;
import static px.reports.dashboard.data.LeadColumnChartsTestData.LEADS_LINK;

/**
 * Created by kgr on 8/28/2017.
 */
public class DashboardCharts extends LoginTest {

    @Test
    public void checkLeadStatisticsChart() {
        LeadStatisticsTestData testData = new LeadStatisticsTestData()
                .withLeadsStatistics();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check lead statistics charts
        Chart chart = new Chart(LEAD_STATISTICS_CHART) {
        };
        chart.checkChartData(chart, testData.getLeadsStatistics());
        // check lead buying chart
        chart = new Chart(LEADS_BUYING_CHART) {
        };
        chart.checkChartData(chart, testData.getLeadsStatistics());
    }

    @Test
    public void checkLeadNamesChart() {
        LeadStatisticsTestData testData = new LeadStatisticsTestData()
                .withLeadsNames();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check lead names chart
        ListChart chart = new ListChart(LEADS_LIST_CHART);
        // extend with links to lead preview
        chart.checkChartData(chart, testData.getLeadsNames());
    }

    @Test
    public void checkLeadsColumnChart() {
        LeadColumnChartsTestData testData = new LeadColumnChartsTestData()
                .withLeads();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check leads column chart
        ColumnChart chart = new ColumnChart(LEADS_COLUMN_CHART);
        chart.checkChartData(chart, testData.getLeadData());
        // check redirect
        reportPage.checkRedirectLink(LEAD_COLUMN_CHART, VIEW_MORE_BUTTON, LEADS_LINK);
    }

    @Test
    public void checkConversionsColumnCharts() {
        LeadColumnChartsTestData testData = new LeadColumnChartsTestData()
                .withConversions();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check conversions column chart
        ColumnChart chart = new ColumnChart(CONVERSIONS_COLUMN_CHART);
        chart.checkChartData(chart, testData.getConversionData());
        // check redirect
        reportPage.checkRedirectLink(CONVERSION_COLUMN_CHART, VIEW_MORE_BUTTON, CONVERSIONS_LINK);
    }
}
