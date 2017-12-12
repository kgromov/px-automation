package px.reports.dashboard.charts;

import configuration.helpers.JSONWrapper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import px.reports.dashboard.DashboardChartsEnum;
import px.reports.dto.ChartMetaData;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static pages.locators.DashboardPageLocators.CHART_TOOLTIP;

/**
 * Created by konstantin on 23.08.2017.
 */
public class DoughnutChart extends Chart {

    public DoughnutChart(DashboardChartsEnum chart) {
        super(chart);
    }

    @Override
    public String checkTooltipData(WebElement container, JSONObject expectedData, List<ChartMetaData> fields, String itemLocator) {
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
            if (width < 4) return "";
            // to hide previous one
            if (itemIndex > 0) {
                helper.moveToElement(container);
                helper.pause(200);
                // to show next one
                helper.moveToElement(item);
            } else if (itemIndex == 0 && !helper.isElementPresent(CHART_TOOLTIP, 1))
                // temp solution for doughnuts (width - 141  + width/2 +20)
                helper.mouseMove(item.getLocation().getX() + item.getSize().getWidth() - 20, item.getLocation().getY() - item.getSize().getHeight() / 2);
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
}
