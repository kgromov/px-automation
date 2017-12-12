package pages.groups;

import configuration.helpers.DataHelper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.dropdown.FilteredDropDown;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import px.reports.GraphicsEnum;
import px.reports.ReportTestData;
import px.reports.Valued;
import utils.SoftAssertionHamcrest;

import static org.hamcrest.CoreMatchers.equalTo;
import static px.reports.ReportPageLocators.*;

/**
 * Created by kgr on 5/29/2017.
 */
public interface GraphicsPage extends Wait {
    Logger LOGGER = Logger.getLogger(GraphicsPage.class);
    ElementsHelper HELPER = HelperSingleton.getHelper();

    default void checkGraphics(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        LOGGER.info("Check 'Graphics' filter");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // none
        hamcrest.append(checkNoneGraphics(enums));
        // bubble chart
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        //  column chart
        hamcrest.append(checkColumnChart(testData, dataList, enums));
        // after reset - default graphic
        LOGGER.info("Check graphic after reset - 'Bubble chart' is expected");
        resetGraphics();
        hamcrest.append(checkBubbleChart(testData, dataList, enums));
        hamcrest.assertAll();
    }

    default void turnOffGraphics() {
        FilteredDropDown graphicsFilter = new FilteredDropDown(HELPER.getElement(GRAPHICS_FILTER));
        graphicsFilter.setByTitle(GraphicsEnum.NONE.getValue());
        waitPageIsLoaded();
    }

    default void resetGraphics() {
        FilteredDropDown graphicsFilter = new FilteredDropDown(HELPER.getElement(GRAPHICS_FILTER));
        graphicsFilter.resetFilter();
        waitPageIsLoaded();
    }

    default String checkBubbleChart(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        LOGGER.info("Check 'Bubble chart' graphic");
        FilteredDropDown graphicsFilter = new FilteredDropDown(HELPER.getElement(GRAPHICS_FILTER));
        graphicsFilter.setByTitle(GraphicsEnum.BUBBLE_CHART.getValue());
        waitPageIsLoaded();
        // set graphic params to get know has data or not
        testData.setBubbleChartGraphicParams(HELPER.getCurrentUrl());
        String filterDescription = String.format(", graphics are filtered by '%s'", GraphicsEnum.BUBBLE_CHART.getValue());
        hamcrest.assertThat(String.format("'%s' graphic is visible%s", GraphicsEnum.BUBBLE_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(BUBBLE_CHART_CONTAINER), equalTo(testData.hasDataToCalculate(dataList)));
        // other graphics are hidden {Bubble chart, Stacked Area chart}
        hamcrest.assertThat(String.format("'%s' graphic is hidden%s", GraphicsEnum.COLUMN_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(COLUMN_CHART_CONTAINER), equalTo(false));
        hamcrest.assertThat(String.format("'%s' graphic is hidden%s", GraphicsEnum.STACKED_AREA_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(STACKED_AREA_CHART_CONTAINER), equalTo(false));
        // check all report filters
        hamcrest.assertThat(String.format("Number of filters = %d %s %s", enums.length, DataHelper.getFilteredEnumValues(enums), filterDescription),
                HELPER.getVisibleList(HELPER.getElements(REPORT_FILTER_CONTAINER)).size(), equalTo(enums.length));
        return hamcrest.toString();
    }

    default String checkColumnChart(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // column: filtersCount - 1 category
        LOGGER.info("Check 'Column chart' graphic");
        FilteredDropDown graphicsFilter = new FilteredDropDown(HELPER.getElement(GRAPHICS_FILTER));
        graphicsFilter.setByTitle(GraphicsEnum.COLUMN_CHART.getValue());
        waitPageIsLoaded();
        // set graphic params to get know has data or not
        testData.setColumnChartGraphicParams(HELPER.getCurrentUrl());
        String filterDescription = String.format(", graphics are filtered by '%s'", GraphicsEnum.COLUMN_CHART.getValue());
        hamcrest.assertThat(String.format("'%s' graphic is visible%s", GraphicsEnum.COLUMN_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(COLUMN_CHART_CONTAINER), equalTo(testData.hasDataToCalculate(dataList)));
        // other graphics are hidden {Bubble chart, Stacked Area chart}
        hamcrest.assertThat(String.format("'%s' graphic is hidden%s", GraphicsEnum.BUBBLE_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(BUBBLE_CHART_CONTAINER), equalTo(false));
        hamcrest.assertThat(String.format("'%s' graphic is hidden%s", GraphicsEnum.STACKED_AREA_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(STACKED_AREA_CHART_CONTAINER), equalTo(false));
        // check all report filters
        hamcrest.assertThat(String.format("Number of filters = %d %s %s", enums.length, testData.graphicParams(), filterDescription),
                HELPER.getVisibleList(HELPER.getElements(REPORT_FILTER_CONTAINER)).size(), equalTo(enums.length - 1));
        return hamcrest.toString();
    }

    default String checkStackedAreaChart(ReportTestData testData, JSONArray dataList, Valued[] enums) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // stacked area: filtersCount - 1 category
        LOGGER.info("Check 'Stacked Area chart' graphic");
        FilteredDropDown graphicsFilter = new FilteredDropDown(HELPER.getElement(GRAPHICS_FILTER));
        graphicsFilter.setByTitle(GraphicsEnum.STACKED_AREA_CHART.getValue());
        waitPageIsLoaded();
        // set graphic params to get know has data or not
        testData.setStackedAreaChartGraphicParams(HELPER.getCurrentUrl());
        String filterDescription = String.format(", graphics are filtered by '%s'", GraphicsEnum.STACKED_AREA_CHART.getValue());
        hamcrest.assertThat(String.format("'%s' graphic is visible%s", GraphicsEnum.STACKED_AREA_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(STACKED_AREA_CHART_CONTAINER), equalTo(testData.hasStackedDataToCalculate(dataList)));
        // other graphics are hidden {Bubble chart, Column chart}
        hamcrest.assertThat(String.format("'%s' graphic is hidden%s", GraphicsEnum.BUBBLE_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(BUBBLE_CHART_CONTAINER), equalTo(false));
        hamcrest.assertThat(String.format("'%s' graphic is hidden%s", GraphicsEnum.COLUMN_CHART.getValue(), filterDescription),
                HELPER.isElementAccessible(COLUMN_CHART_CONTAINER), equalTo(false));
        // check all report filters
        hamcrest.assertThat(String.format("Number of filters = %d %s %s", enums.length, testData.graphicParams(), filterDescription),
                HELPER.getVisibleList(HELPER.getElements(REPORT_FILTER_CONTAINER)).size(), equalTo(enums.length - 1));
        return hamcrest.toString();
    }

    default String checkNoneGraphics(Valued[] enums) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // none: filtersCount - 3 categories
        LOGGER.info("Check graphics by 'None' filter");
        FilteredDropDown graphicsFilter = new FilteredDropDown(HELPER.getElement(GRAPHICS_FILTER));
        graphicsFilter.setByTitle(GraphicsEnum.NONE.getValue());
        waitPageIsLoaded();
        String filterDescription = String.format(", graphics are filtered by '%s'", GraphicsEnum.NONE.getValue());
        hamcrest.assertThat(String.format("There are no graphics'%s'", filterDescription),
                HELPER.isElementAccessible(GRAPHICS_CONTAINER), equalTo(false));
        hamcrest.assertThat(String.format("Number of filters = %d %s %s", enums.length, DataHelper.getFilteredEnumValues(enums, "CATEGORY"), filterDescription),
                HELPER.getVisibleList(HELPER.getElements(REPORT_FILTER_CONTAINER)).size(), equalTo(enums.length - 3));
        return hamcrest.toString();
    }
}