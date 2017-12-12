package px.reports.dashboard;

import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static configuration.helpers.DataHelper.getJSONFromJSONArrayByCondition;

/**
 * Created by kgr on 8/25/2017.
 */
public class DashboardChartsBinder {
    private static DashboardChartsBinder ourInstance = new DashboardChartsBinder();
    private static JSONArray chartsData;

    public static JSONArray getChartsData() {
        return chartsData;
    }

    // inPattern, outPattern FieldFormatObject does not suite
    // leads, conversions column charts, doughnuts, state progress bar are with tooltips
    public static JSONObject getChartDataByName(String name) {
        return getJSONFromJSONArrayByCondition(chartsData, "name", name);
    }

    private DashboardChartsBinder() {
        try {
            chartsData = new JSONArray(new JSONTokener(new BufferedReader(
                    new FileReader(new File("./src/test/resources/binders/DashboardCharts.json")))));
        } catch (FileNotFoundException e) {
            throw new TestDataException("Unable to load dashboard charts binder config", e);
        }
    }
}
