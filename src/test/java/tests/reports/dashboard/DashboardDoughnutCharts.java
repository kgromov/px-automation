package tests.reports.dashboard;

import org.testng.annotations.Test;
import px.reports.dashboard.DashboardReportPage;
import px.reports.dashboard.charts.Chart;
import px.reports.dashboard.charts.DoughnutChart;
import px.reports.dashboard.charts.ProgressBarChart;
import px.reports.dashboard.data.AudienceChartsReportTestData;
import tests.LoginTest;

import static pages.locators.DashboardPageLocators.*;
import static px.reports.audience.AudienceReportTestData.REPORT_TYPE_FILTER;
import static px.reports.dashboard.DashboardChartsEnum.AGE_DOUGHNUT_CHART;
import static px.reports.dashboard.DashboardChartsEnum.GENDER_DOUGHNUT_CHART;
import static px.reports.dashboard.DashboardChartsEnum.STATE_PROGRESSBAR_CHART;
import static px.reports.dashboard.data.AudienceChartsReportTestData.*;

/**
 * Created by kgr on 8/28/2017.
 */
public class DashboardDoughnutCharts extends LoginTest {

    @Test
    public void checkGenderDoughnutChart() {
        AudienceChartsReportTestData testData = new AudienceChartsReportTestData()
                .withGenders();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check gender chart
        Chart chart = new DoughnutChart(GENDER_DOUGHNUT_CHART);
        chart.checkChartData(chart, testData.getItemsByGender());
        // check link + report filter
        reportPage.checkRedirectLink(GENDER_DOUGHNUT, SUMMARY_LINK, REPORT_LINK);
        // check report filter is set to correct reportType
        reportPage.checkFilterAfterRedirect(REPORT_TYPE_FILTER, getReportTypeFilter(GENDER_TYPE), testData);
    }

    @Test
    public void checkAgeGroupDoughnutChart() {
        AudienceChartsReportTestData testData = new AudienceChartsReportTestData()
                .withAgeGroups();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check age groups chart
        Chart chart = new DoughnutChart(AGE_DOUGHNUT_CHART);
        chart.checkChartData(chart, testData.getItemsByAgeGroup());
        // check link + report filter
        reportPage.checkRedirectLink(AGE_DOUGHNUT, SUMMARY_LINK, REPORT_LINK);
        // check report filter is set to correct reportType
        reportPage.checkFilterAfterRedirect(REPORT_TYPE_FILTER, getReportTypeFilter(AGE_GROUP_TYPE), testData);
    }

    @Test
    public void checkLeadStatesChart() {
        AudienceChartsReportTestData testData = new AudienceChartsReportTestData()
                .withStates();
        DashboardReportPage reportPage = new DashboardReportPage(pxDriver);
        reportPage.navigateToPage();
        reportPage.setCustomRangesShort(testData);
        // check state chart
        ProgressBarChart chart = new ProgressBarChart(STATE_PROGRESSBAR_CHART);
        chart.checkChartData(chart, testData.getItemsByState());
        // check link + report filter
        reportPage.checkRedirectLink(STATE_PROGRESSBAR, SUMMARY_LINK, REPORT_LINK);
        // check report filter is set to correct reportType
        reportPage.checkFilterAfterRedirect(REPORT_TYPE_FILTER, getReportTypeFilter(STATE_TYPE), testData);
    }

}
