package px.reports.dto;

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
public class ReportDataBinder {
    private static ReportDataBinder ourInstance = new ReportDataBinder();
    private static JSONArray reports;

    public static JSONObject getReportDataByName(String name) {
        return getJSONFromJSONArrayByCondition(reports, "report", name);
    }

    private ReportDataBinder() {
        try {
            reports = new JSONArray(new JSONTokener(new BufferedReader(
                    new FileReader(new File("./src/test/resources/binders/Tooltips.json")))));
        } catch (FileNotFoundException e) {
            throw new TestDataException("Unable to load report data binder config", e);
        }
    }
}