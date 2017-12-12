package px.reports.dashboard.charts;

import elements.ElementsHelper;
import elements.HelperSingleton;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebElement;
import px.reports.dashboard.DashboardChartsEnum;
import px.reports.dto.ChartMetaData;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by kgr on 8/30/2017.
 */
public class ColumnChart extends Chart {

    public ColumnChart(DashboardChartsEnum chart) {
        super(chart);
    }

    @Override
    public void checkChartData(Chart chart, JSONArray expectedData) {
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
        // get element text using Jsoup
        Document element = Jsoup.parse(container.getAttribute("innerHTML"));
        ChartMetaData columnField = ChartMetaData.getFieldObjectFromListByName(fields, "column");
        // columns number verification
        hamcrest.assertThat("Check that number of columns in chart equals to rows in response",
                element.select(columnField.getLocator()).size(),
                equalTo(chart.hasSummaryField() ? expectedData.length() - 1 : expectedData.length()));
        // tooltip verification
        if (chart.hasTooltip() && element.select(columnField.getLocator()).size() == (chart.hasSummaryField() ? expectedData.length() - 1 : expectedData.length())) {
            for (int i = chart.hasSummaryField() ? 1 : 0; i < expectedData.length(); i++) {
                List<ChartMetaData> tooltipFields = chart.getTooltipFields();
                // update tooltip data fields with proper index
                for (ChartMetaData field : tooltipFields) {
                    // reindexing cause total row is another element in UI
                    field.withIndex(chart.hasSummaryField() ? i - 1 : i);
                }
                hamcrest.append(checkTooltipData(container, expectedData.getJSONObject(i), tooltipFields, chart.getItemLocator()));
            }
        }
        // check chart items proportion accordance - not works cause of too small columns (e.g. '2' has the same height as '0')
       /* if (chart.hasProportionData())
            hamcrest.append(checkItemsProportion(chart, expectedData, VALUE_COMPARATOR, HEIGHT_COMPARATOR));*/
        logger.info(String.format("Time to check chart '%s' data and tooltips, %d rows = %d, ms",
                chart.getName(), expectedData.length(), System.currentTimeMillis() - start));
        // testNG is hanging if description is more than 2KB
        hamcrest = getSoftAssertionWithShorterDescription(hamcrest);
        hamcrest.assertAll();
    }
}
