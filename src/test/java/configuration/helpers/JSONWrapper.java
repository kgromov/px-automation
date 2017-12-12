package configuration.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kgr on 7/4/2017.
 */
public class JSONWrapper {
    private String source;

    public JSONWrapper(String source) {
        this.source = source;
    }

    public JSONObject getJSON() {
        try {
            return new JSONObject(source);
        } catch (JSONException e) {
            throw new JSONException(String.format("Unable to parse source '%s' to JSON object", source), e);
        }
    }

    public JSONArray getJSONArray() {
        try {
            return new JSONArray(source);
        } catch (JSONException e) {
            throw new JSONException(String.format("Unable to parse source '%s' to JSON array", source), e);
        }
    }

    public static String getString(JSONObject object, String key) {
        try {
            return String.valueOf(object.get(key)).replaceAll("\\s{2,}", " ");
        } catch (JSONException e) {
            throw new JSONException(String.format("JSON object does not contain '%s' key, available keys - '%s'", key, object.keySet()));
        }
    }

    public static List<JSONObject> toList(JSONArray objects) {
        List<JSONObject> list = new ArrayList<>(objects.length());
        for (int i = 0; i < objects.length(); i++) {
            list.add(objects.getJSONObject(i));
        }
        return list;
    }

    public static List<String> toList(JSONObject object, String key) {
        return object.getJSONArray(key).toList().stream().map(String::valueOf).collect(Collectors.toList());
    }
}