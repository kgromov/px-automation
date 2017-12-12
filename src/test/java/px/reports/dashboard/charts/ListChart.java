package px.reports.dashboard.charts;

import configuration.helpers.JSONWrapper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import px.reports.dashboard.DashboardChartsEnum;
import px.reports.dto.ChartMetaData;
import px.reports.dto.DateRange;
import utils.SoftAssertionHamcrest;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static pages.locators.DashboardPageLocators.SHOW_MORE_DOWN_LEADS_BUTTON;

/**
 * Created by konstantin on 23.08.2017.
 */
public class ListChart extends Chart {
    private static final DateRange RANGE = new DateRange(new Date());
    private static final String LEAD_PREVIEW_LINK = "/leads/%s/%s/preview/";

    public ListChart(DashboardChartsEnum chart) {
        super(chart);
    }

    @Override
    public String checkChartData(WebElement container, JSONObject expectedData, List<ChartMetaData> fields) {
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        for (ChartMetaData field : fields) {
            try {
                String expectedValue = JSONWrapper.getString(expectedData, field.getName());
                // get formatted value
                expectedValue = field.getValue(expectedValue);
                WebElement item = helper.getElement(container, field.getLocator());
                if (!item.isDisplayed()) {
                    helper.getElement(SHOW_MORE_DOWN_LEADS_BUTTON).click();
                    helper.waitUntilDisplayed(field.getLocator());
                    item = helper.getElement(container, field.getLocator());
                }
                // data verification
                hamcrest.assertThat(String.format("Check data accordance with UI, item = '%s'", field.getName()),
                        item.getText(), containsString(expectedValue));
            } catch (JSONException e) {
                hamcrest.assertThat(String.format("No data for item = '%s'\tDetails:\n%s",
                        field.getName(), e.getMessage()), false);
            } catch (NoSuchElementException e2) {
                hamcrest.assertThat(String.format("No element in UI, item = '%s', locator = '%s'",
                        field.getName(), field.getLocator()), false);
            }
        }
        // lead preview link verification
        int itemIndex = fields.get(0).getIndex();
        try {
            String leadID = String.valueOf(expectedData.get("leadId"));
            hamcrest.assertThat(String.format("Check link correctness item = '%d'", itemIndex),
                    helper.getElement(container, By.cssSelector(String.format("a:nth-of-type(%d)", itemIndex + 1))).getAttribute("href"),
                    containsString(String.format(LEAD_PREVIEW_LINK, leadID, RANGE.getFromPeriod())));
        } catch (JSONException e) {
            hamcrest.assertThat(String.format("No leadId for row = '%d'\tDetails:\n%s", itemIndex + 1, e.getMessage()), false);
        } catch (NoSuchElementException e2) {
            hamcrest.assertThat(String.format("No preview link in UI, row = '%d'", itemIndex + 1), false);
        }
        return hamcrest.toString();
    }
}
