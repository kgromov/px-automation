package px.reports.dto;

import org.json.JSONArray;
import org.json.JSONObject;

import static configuration.helpers.DataHelper.getJSONFromJSONArray;

/**
 * Created by kgr on 9/21/2017.
 */
public class ReportDataMapping {
    private String name;
    private JSONObject data;
    private JSONObject headerTooltips;
    private JSONArray dataTooltips;

    public ReportDataMapping() {
    }

    public ReportDataMapping(String name) {
        this.name = name;
        this.data = ReportDataBinder.getReportDataByName(name);
        if (data != null) {
            // tooltip in header
            this.headerTooltips = data.has("header") ? data.getJSONObject("header") : null;
            // tooltip in cell
            this.dataTooltips = data.has("data") ? data.getJSONArray("data") : null;
        }
    }

    public boolean hasMapping() {
        return data != null;
    }

    public boolean hasFieldHeaderMapping(String fieldName) {
        return headerTooltips != null && headerTooltips.has(fieldName);
    }

    public String getFieldHeaderMapping(String value) {
        return headerTooltips.getString(value);
    }

    // change this fucking shit
    public boolean hasFieldTooltipMapping(String fieldName) {
        return dataTooltips != null && getJSONFromJSONArray(dataTooltips, fieldName) != null;
    }

    public JSONObject getFieldTooltipMapping(String fieldName) {
        return getJSONFromJSONArray(dataTooltips, fieldName);
    }

}