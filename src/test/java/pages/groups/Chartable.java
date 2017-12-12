package pages.groups;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import px.reports.dashboard.charts.Chart;
import px.reports.dashboard.charts.ChartItem;
import px.reports.dto.ChartMetaData;
import utils.SoftAssertionHamcrest;

import java.util.*;

import static configuration.helpers.DataHelper.getJSONFromJSONArrayByCondition;
import static org.hamcrest.Matchers.containsString;
import static pages.locators.DashboardPageLocators.CHART_TOOLTIP;
import static px.reports.dashboard.charts.ChartItem.VALUE_COMPARATOR;
import static px.reports.dashboard.charts.ChartItem.WIDTH_COMPARATOR;

/**
 * Created by kgr on 8/28/2017.
 */
public interface Chartable {
    Logger logger = Logger.getLogger(Chartable.class);

    default void checkChartData(Chart chart, JSONObject expectedData) {
        long start = System.currentTimeMillis();
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // bind data by container name
        helper.waitUntilDisplayed(chart.getLocator());
        WebElement container = helper.getElement(chart.getLocator());
        helper.scrollToElement(container);
        // get metadata
        hamcrest.append(checkChartData(container, expectedData, chart.getFields()));
        logger.info(String.format("Time to check chart '%s' = %d, ms", chart.getName(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    default void checkChartData(Chart chart, JSONArray expectedData) {
        long start = System.currentTimeMillis();
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // bind data by container name
        helper.waitUntilDisplayed(chart.getLocator());
        WebElement container = helper.getElement(chart.getLocator());
        helper.scrollToElement(container);
        // get metadata
        List<ChartMetaData> fields = chart.getFields();
        // check summary row/link
        if (chart.hasSummaryField()) {
            ChartMetaData summaryField = chart.getSummaryField();
            hamcrest.append(checkTotalRow(container, summaryField, expectedData));
            fields.remove(summaryField);
        }
        // common rows verification
        for (int i = 0; i < expectedData.length(); i++) {
            // update data fields with proper index
            for (ChartMetaData field : fields) {
                field.withIndex(i).updateLocatorWithIndex(i);
            }
            JSONObject jsonObject = expectedData.getJSONObject(i);
            // data verification
            String verifiedRow = checkChartData(container, jsonObject, fields);
            hamcrest.append(verifiedRow.isEmpty() ? "" : verifiedRow + "\tRow = " + (i + 1));
            // tooltip verification
            if (chart.hasTooltip()) {
                List<ChartMetaData> tooltipFields = chart.getTooltipFields();
                // update tooltip data fields with proper index
                for (ChartMetaData field : tooltipFields) {
                    field.withIndex(i);
                }
                hamcrest.append(checkTooltipData(container, jsonObject, tooltipFields, chart.getItemLocator()));
            }
        }
        // check chart items proportion accordance
        if (chart.hasProportionData())
            hamcrest.append(checkItemsProportion(chart, expectedData, VALUE_COMPARATOR, WIDTH_COMPARATOR));
        logger.info(String.format("Time to check chart '%s' data and tooltips, %d rows = %d, ms",
                chart.getName(), expectedData.length(), System.currentTimeMillis() - start));
        // testNG is hanging if description is more than 2KB
        hamcrest = getSoftAssertionWithShorterDescription(hamcrest);
        hamcrest.assertAll();
    }

    default String checkChartData(WebElement container, JSONObject expectedData, List<ChartMetaData> fields) {
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        for (ChartMetaData field : fields) {
            try {
                String expectedValue = JSONWrapper.getString(expectedData, field.getName());
                // get formatted value
                expectedValue = field.getValue(expectedValue);
                hamcrest.assertThat(String.format("Check data accordance with UI, item = '%s'", field.getName()),
                        helper.getElement(container, field.getLocator()).getText(),
                        containsString(expectedValue));
            } catch (JSONException e) {
                hamcrest.assertThat(String.format("No data for item = '%s'\tDetails:\n%s",
                        field.getName(), e.getMessage()), false);
            } catch (NoSuchElementException e2) {
                hamcrest.assertThat(String.format("No element in UI, item = '%s', locator = '%s'",
                        field.getName(), field.getLocator()), false);
            }
        }
        return hamcrest.toString();
    }

    default String checkTotalRow(WebElement container, ChartMetaData summaryField, JSONArray expectedData) {
        logger.info("Check summary/total data");
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // total present in response
        JSONObject totalObject = getJSONFromJSONArrayByCondition(expectedData, summaryField.getTotalBy(), "Total");
        // find in JSONArray or sort by some key
        if (totalObject == null) {
            List<String> sortedValue = DataHelper.getListFromJSONArrayByKey(expectedData, summaryField.getSortBy());
//            sortedValue.sort((o1, o2) -> o2.compareTo(o1));
            totalObject = getJSONFromJSONArrayByCondition(expectedData, summaryField.getSortBy(), sortedValue.get(0));
        }
        try {
            String expectedValue = JSONWrapper.getString(totalObject, summaryField.getSortBy());
            // get formatted value
            expectedValue = summaryField.getValue(expectedValue);
            hamcrest.assertThat(String.format("Check data accordance with UI, item = '%s'", summaryField.getName()),
                    helper.getElement(container, summaryField.getLocator()).getText(),
                    containsString(expectedValue));
        } catch (JSONException e) {
            hamcrest.assertThat(String.format("No data for item = '%s'\tDetails:\n%s",
                    summaryField.getName(), e.getMessage()), false);
        } catch (NoSuchElementException e2) {
            hamcrest.assertThat(String.format("No element in UI, item = '%s', locator = '%s'",
                    summaryField.getName(), summaryField.getLocator()), false);
        }
        return hamcrest.toString();
    }

    default String checkTooltipData(WebElement container, JSONObject expectedData, List<ChartMetaData> fields, String itemLocator) {
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        int itemIndex = fields.get(0).getIndex();
        logger.info(String.format("Check tooltip data for item '%d'", itemIndex + 1));
        try {
            // get proper item to invoke tooltip on it
            WebElement item = helper.getElements(container, By.cssSelector(itemLocator)).get(itemIndex);
            // sometimes (especially in doughnuts width could be 0 - no chance to aim)
            int width = item.getSize().getWidth();
            logger.info("DEBUG:\titem width = " + width);
            if (width < 1) return "";
            // to hide previous one
            if (itemIndex > 0) {
                helper.moveToElement(container);
                helper.pause(200);
            }
            // to show next one
//            helper.clickOnElement(item);
            helper.moveToElement(item);
            try {
                helper.waitUntilDisplayed(CHART_TOOLTIP, 1);
                WebElement tooltip = helper.getElement(CHART_TOOLTIP);
                String tooltipText = tooltip.getText();
                for (ChartMetaData field : fields) {
                    try {
                        String expectedValue = JSONWrapper.getString(expectedData, field.getName());
                        // get formatted value
                        expectedValue = field.getValue(expectedValue);
                        hamcrest.assertThat(String.format("Check tooltip data accordance with UI, " +
                                        "item = '%s', row = '%d'", field.getName(), itemIndex + 1),
                                tooltipText, containsString(expectedValue));
                    } catch (JSONException e) {
                        hamcrest.assertThat(String.format("No data for item = '%s'\tDetails:\n%s",
                                field.getName(), e.getMessage()), false);
                    }
                }
            } catch (TimeoutException | StaleElementReferenceException e) {
                hamcrest.assertThat(String.format("Tooltip is missed for row '%d'", itemIndex + 1), false);
            }
        } catch (NoSuchElementException e) {
            hamcrest.assertThat(String.format("No element in UI, item = '%s', locator = '%s'", itemIndex + 1, itemLocator), false);
        }
        return hamcrest.toString();
    }

    default String checkItemsProportion(Chart chart, JSONArray expectedData, Comparator<ChartItem> comparator1, Comparator<ChartItem> comparator2) {
        logger.info(String.format("Check chart '%s' items size accordance to data value", chart.getName()));
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ChartMetaData proportionField = chart.getProportionField();
        WebElement container = helper.getElement(chart.getLocator());
        List<WebElement> items = helper.getElements(container, By.cssSelector(proportionField.getLocator()));
        // init chart items
        List<ChartItem> chartItems = new ArrayList<>();
        // calculate total and rewrite in ColumnChart
        boolean hastTotal = chart.hasSummaryField() && getJSONFromJSONArrayByCondition(expectedData, chart.getSummaryField().getTotalBy(), "Total") != null;
        for (int i = hastTotal ? 1 : 0; i < expectedData.length(); i++) {
            int itemIndex = hastTotal ? i - 1 : i;
            WebElement item = items.get(itemIndex);
            String expectedValue = JSONWrapper.getString(expectedData.getJSONObject(i), proportionField.getName());
            // get formatted value
            expectedValue = proportionField.getValue(expectedValue);
            chartItems.add(new ChartItem(expectedValue, item.getSize()));
        }
        // sort by comparators and compare later on
        List<ChartItem> sortedChartItems = new ArrayList<>(chartItems);
        chartItems.sort(comparator1);
        sortedChartItems.sort(comparator2);
        hamcrest.assertThat(String.format("Check chart '%s' items size accordance to data '%s' values" +
                        "\tChart items sorted by data: %s\tChart items sorted by dimension: %s",
                chart.getName(), proportionField.getName(), chartItems, sortedChartItems), chartItems.equals(sortedChartItems));
        return hamcrest.toString();
    }

    // testNG is hanging if description is more than 2KB
    default SoftAssertionHamcrest getSoftAssertionWithShorterDescription(SoftAssertionHamcrest hamcrest) {
        if (hamcrest.toString().length() > 2048) {
            Set<String> failedFields = new HashSet<>();
            String[] items = hamcrest.toString().split("item = '");
            for (String item : items) {
                if (item.contains("'"))
                    failedFields.add(item.substring(0, item.indexOf("'")));
            }
            hamcrest = new SoftAssertionHamcrest();
            hamcrest.append("Chart verification finished with errors in the following fields: " + failedFields);
        }
        return hamcrest;
    }

}