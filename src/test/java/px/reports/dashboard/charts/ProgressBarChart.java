package px.reports.dashboard.charts;

import configuration.helpers.JSONWrapper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;
import px.reports.dashboard.DashboardChartsEnum;
import px.reports.dto.ChartMetaData;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static org.hamcrest.Matchers.containsString;

/**
 * Created by konstantin on 23.08.2017.
 */
public class ProgressBarChart extends Chart {

    public ProgressBarChart(DashboardChartsEnum chart) {
        super(chart);
    }

    @Override
    public String checkChartData(WebElement container, JSONObject expectedData, List<ChartMetaData> fields) {
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // get element text using Jsoup
        Document element = Jsoup.parse(container.getAttribute("innerHTML"));
        for (ChartMetaData field : fields) {
            try {
                String expectedValue = JSONWrapper.getString(expectedData, field.getName());
                // get formatted value
                expectedValue = field.getValue(expectedValue);
                Element item = element.select(field.getLocator()).get(field.getIndex());
                hamcrest.assertThat(String.format("Check data accordance with UI, item = '%s'", field.getName()),
                        item.text(), containsString(expectedValue));
            } catch (JSONException e) {
                hamcrest.assertThat(String.format("No data for item = '%s'\tDetails:\n%s",
                        field.getName(), e.getMessage()), false);
            } catch (IndexOutOfBoundsException e2) {
                hamcrest.assertThat(String.format("No element in UI, item = '%s', locator = '%s'",
                        field.getName(), field.getLocator()), false);
            }
        }
        return hamcrest.toString();
    }
}
