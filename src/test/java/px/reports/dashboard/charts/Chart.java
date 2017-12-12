package px.reports.dashboard.charts;

import org.json.JSONObject;
import pages.groups.Chartable;
import px.reports.dashboard.DashboardChartsBinder;
import px.reports.dashboard.DashboardChartsEnum;
import px.reports.dto.ChartMetaData;

import java.util.List;

/**
 * Created by kgr on 8/25/2017.
 */
public abstract class Chart implements Chartable {
    private String name;
    private String locator;
    private JSONObject data;
    private ChartMetaData summaryField;
    private List<ChartMetaData> fields;
    // tooltip data
    private boolean hasTooltip;
    private String itemLocator;
    private List<ChartMetaData> tooltipFields;
    // proportion data
    private boolean hasProportionData;
    private ChartMetaData proportionField;
//    private WebElement element;

    public Chart(DashboardChartsEnum chart) {
        this.name = chart.getValue();
        // data
        this.data = DashboardChartsBinder.getChartDataByName(chart.getValue());
        this.locator = data.getString("container");
        this.fields = ChartMetaData.getObjectsByJSONArray(data.getJSONArray("data"));
        this.summaryField = ChartMetaData.getFieldObjectFromListByName(fields, "total");
        // tooltip data
        this.hasTooltip = data.has("tooltipData");
        if (hasTooltip) {
            JSONObject data = this.data.getJSONObject("tooltipData");
            this.itemLocator = data.getString("item");
            this.tooltipFields = ChartMetaData.getObjectsByJSONArray(data.getJSONArray("data"));
        }
        // proportion data
        this.hasProportionData = data.has("proportionData");
        if (hasProportionData) {
            this.proportionField = new ChartMetaData(data.getJSONObject("proportionData"));
        }
//        this.element = HelperSingleton.getHelper().getElement(elementLocator);
    }

    // common data
    public String getName() {
        return name;
    }

    public JSONObject getData() {
        return data;
    }

    public String getLocator() {
        return locator;
    }

    public List<ChartMetaData> getFields() {
        return fields;
    }

    public ChartMetaData getSummaryField() {
        return summaryField;
    }

    public boolean hasSummaryField() {
        return summaryField != null;
    }

    // tooltip
    public boolean hasTooltip() {
        return hasTooltip;
    }

    public String getItemLocator() {
        return itemLocator;
    }

    public List<ChartMetaData> getTooltipFields() {
        return tooltipFields;
    }

    // proportion
    public boolean hasProportionData() {
        return hasProportionData;
    }

    public ChartMetaData getProportionField() {
        return proportionField;
    }

    @Override
    public String toString() {
        return "Chart{" +
                "name='" + name + '\'' +
                ", locator='" + locator + '\'' +
                '}';
    }
}
