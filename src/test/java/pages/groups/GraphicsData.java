package pages.groups;

import configuration.helpers.DataHelper;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kgr on 8/4/2017.
 */
public interface GraphicsData {

    boolean hasBubbleDataToCalculate(JSONArray jsonArray);

    boolean hasStackedDataToCalculate(JSONArray jsonArray);

    default Set<String> getBubbleChartGraphicParams(String url) {
        return new HashSet<>(DataHelper.getValuesJSONFromURL(url, "chart", "b1", "b2", "b3"));
    }

    default Set<String> getColumnChartGraphicParams(String url) {
        return new HashSet<>(DataHelper.getValuesJSONFromURL(url, "chart", "c2")); //"c1",
    }

    default Set<String> setStackedAreaChartGraphicParams(String url) {
        return new HashSet<>(DataHelper.getValuesJSONFromURL(url, "chart", "sa1", "sa2"));
    }
}
