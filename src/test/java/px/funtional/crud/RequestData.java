package px.funtional.crud;

import configuration.helpers.HttpMethodsEnum;
import dto.LxpDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/29/2017.
 */
public interface RequestData {
    LxpDataProvider dataProvider = new LxpDataProvider();

    String createURL();

    String updateURL();

    String getURL();

    JSONObject asJSON();

    default String getIdKey() {
        return "data";
    }

    default Set<String> allowedFieldsToUpdate() {
        return isAdmin() ? asJSON().keySet() : new HashSet<>();
    }

    default Set<String> allowedFieldsToUpdate(JSONObject fields) {
        return isAdmin() ? fields.keySet() : new HashSet<>();
    }

    default Map<String, String> asMap(String requestedURL) {
        JSONArray updateDetails = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        Map<String, String> objectDetails = new HashMap<>(updateDetails.length());
        for (int i = 0; i < updateDetails.length(); i++) {
            JSONObject object = updateDetails.getJSONObject(i);
            if (object.has("field")) {
                try {
                    String key = String.valueOf(object.get("field"));
                    String value = object.has("default") ? String.valueOf(object.get("default")) : "";
                    objectDetails.put(key, value);
                } catch (NullPointerException | JSONException e) {
                    System.out.println(String.format("Unable to handle field: '%s'\tDetails=\n%s", object, e.getMessage()));
                }
            }
        }
        // exceptional fields
        objectDetails.remove("modifyDate");
        return objectDetails;
    }
}